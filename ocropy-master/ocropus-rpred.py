
from __future__ import print_function

import traceback
import codecs
from pylab import *
import os.path
import ocrolib
import argparse
#matplotlib.use('Agg')
import matplotlib
import matplotlib.pyplot as plt
from multiprocessing import Pool
from ocrolib import edist
from ocrolib.exceptions import FileNotFound, OcropusException
from collections import Counter
from ocrolib import lstm
from scipy.ndimage import measurements
import time

parser = argparse.ArgumentParser("apply an RNN recognizer")

# error checking
parser.add_argument('-n','--nocheck',action="store_true",
                    help="disable error checking on inputs")

# line dewarping (usually contained in model)
parser.add_argument("-e","--nolineest",action="store_true",
                    help="target line height (overrides recognizer)")
parser.add_argument("-l","--height",default=-1,type=int,
                    help="target line height (overrides recognizer)")

# recognition
parser.add_argument('-m','--model',default="en-default.pyrnn.gz",
                    help="line recognition model")
parser.add_argument("-p","--pad",default=16,type=int,
                    help="extra blank padding to the left and right of text line")
parser.add_argument('-N',"--nonormalize",action="store_true",
                    help="don't normalize the textual output from the recognizer")
parser.add_argument('--llocs',action="store_true",
                    help="output LSTM locations for characters")
parser.add_argument('--alocs',action="store_true",
                    help="output aligned LSTM locations for characters")
parser.add_argument('--probabilities',action="store_true",
                    help="output probabilities for each letter")

# error measures
parser.add_argument("-r","--estrate",action="store_true",
                    help="estimate error rate only")
parser.add_argument("-c","--estconf",type=int,default=20,
                    help="estimate confusion matrix")
parser.add_argument("-C","--compare",default="nospace",
                    help="string comparison used for error rate estimate")
parser.add_argument("--context",default=0,type=int,
                    help="context for error reporting")

# debugging
parser.add_argument('-s','--show',default=-1,type=float,
                    help="if >0, shows recognition output in a window and waits this many seconds")
parser.add_argument('-S','--save',default=None,
                    help="save debugging output image as PNG (for bug reporting)")
parser.add_argument("-q","--quiet",action="store_true",
                    help="turn off most output")
parser.add_argument("-Q","--parallel",type=int,default=1,
                    help="number of parallel processes to use, default: %(default)s")

# input files
parser.add_argument("files",nargs="+",
                    help="input files; glob and @ expansion performed")
args = parser.parse_args()

def print_info(*objs):
    print("INFO: ", *objs, file=sys.stdout)

def print_error(*objs):
    print("ERROR: ", *objs, file=sys.stderr)

def check_line(image):
    if len(image.shape)==3: return "input image is color image %s"%(image.shape,)
    if mean(image)<median(image): return "image may be inverted"
    h,w = image.shape
    if h<20: return "image not tall enough for a text line %s"%(image.shape,)
    if h>200: return "image too tall for a text line %s"%(image.shape,)
    if w<1.5*h: return "line too short %s"%(image.shape,)
    if w>4000: return "line too long %s"%(image.shape,)
    ratio = w*1.0/h
    _,ncomps = measurements.label(image>mean(image))
    lo = int(0.5*ratio+0.5)
    hi = int(4*ratio)+1
    if ncomps<lo: return "too few connected components (got %d, wanted >=%d)"%(ncomps,lo)
    if ncomps>hi*ratio: return "too many connected components (got %d, wanted <=%d)"%(ncomps,hi)
    return None

# compute the list of files to be classified

if len(args.files)<1:
    parser.print_help()
    sys.exit(0)

print_info("")
print_info("#"*10,(" ".join(sys.argv))[:60])
print_info("")


inputs = ocrolib.glob_all(args.files)
if not args.quiet: print_info("#inputs: %d" % (len(inputs)))

# disable parallelism when anything is being displayed

if args.show>=0 or args.save is not None:
    args.parallel = 1

# load the network used for classification

try:
  network = ocrolib.load_object(args.model,verbose=1)
  for x in network.walk(): x.postLoad()
  for x in network.walk():
      if isinstance(x,lstm.LSTM):
          x.allocate(5000)
except FileNotFound:
  print_error("")
  print_error("Cannot find OCR model file:" + args.model)
  print_error("Download a model and put it into:" + ocrolib.default.modeldir)
  print_error("(Or override the location with OCROPUS_DATA.)")
  print_error("")
  sys.exit(1)

# get the line normalizer from the loaded network, or optionally
# let the user override it (this is not very useful)

lnorm = getattr(network,"lnorm",None)

if args.height>0:
    lnorm.setHeight(args.height)

# process one file

def process1(arg):
    (trial,fname) = arg
    base,_ = ocrolib.allsplitext(fname)
    line = ocrolib.read_image_gray(fname)
    raw_line = line.copy()
    if prod(line.shape)==0: return None
    if amax(line)==amin(line): return None

    if not args.nocheck:
        check = check_line(amax(line)-line)
        if check is not None:
            print_error("%s SKIPPED %s (use -n to disable this check)" % (fname, check))
            return (0,[],0,trial,fname)

    if not args.nolineest:
        assert "dew.png" not in fname,"don't dewarp dewarped images"
        temp = amax(line)-line
        temp = temp*1.0/amax(temp)
        lnorm.measure(temp)
        line = lnorm.normalize(line,cval=amax(line))
    else:
        assert "dew.png" in fname,"only apply to dewarped images"

    line = lstm.prepare_line(line,args.pad)
    pred = network.predictString(line)

    if args.llocs:
        # output recognized LSTM locations of characters
        result = lstm.translate_back(network.outputs,pos=1)
        scale = len(raw_line.T)*1.0/(len(network.outputs)-2*args.pad)
        #print ('scale 1:', scale)

        postition = []
        postition_image = []
        character = []
        #ion(); imshow(raw_line,cmap=cm.gray)
        with codecs.open(base+".llocs","w","utf-8") as locs:
            for r,c in result:
                #--------------------
                postition.append(r) # position on the probability matrix
                #--------------------
                c = network.l2s([c])
                r = (r-args.pad)*scale
                locs.write("%s\t%.1f\n"%(c,r))
                # ----------------------------------
                character.append(c)
                postition_image.append(int(r))  # position on the image
                # ----------------------------------
                #plot([r,r],[0,20],'r' if c==" " else 'b')
        #ginput(1,1000)
        # print ('positons on the image:', postition_image)
        # print ('positons on the probability matrix:', postition)
        # print ('characters:',  character)

    if args.alocs:
        # output recognized and aligned LSTM locations
        if os.path.exists(base+".gt.txt"):
            transcript = ocrolib.read_text(base+".gt.txt")
            transcript = ocrolib.normalize_text(transcript)
            network.trainString(line,transcript,update=0)
            result = lstm.translate_back(network.aligned,pos=1)
            print(result)
            scale = len(raw_line.T)*1.0/(len(network.aligned)-2*args.pad)
            with codecs.open(base+".alocs","w","utf-8") as locs:
                for r,c in result:
                    c = network.l2s([c])
                    r = (r-args.pad)*scale
                    locs.write("%s\t%.1f\n"%(c,r))

    if args.probabilities:
        # output character probabilities
        result = lstm.translate_back(network.outputs,pos=2)
        with codecs.open(base+".prob","w","utf-8") as file:
            for c,p in result:
                c = network.l2s([c])
                file.write("%s\t%s\n"%(c,p))

    if not args.nonormalize:
        pred = ocrolib.normalize_text(pred)

    if args.estrate:
        try:
            gt = ocrolib.read_text(base+".gt.txt")
        except:
            return (0,[],0,trial,fname)
        pred0 = ocrolib.project_text(pred,args.compare)
        gt0 = ocrolib.project_text(gt,args.compare)
        if args.estconf>0:
            err,conf = edist.xlevenshtein(pred0,gt0,context=args.context)
        else:
            err = edist.xlevenshtein(pred0,gt0)
            conf = []
        if not args.quiet:
            print_info("%3d %3d %s:%s" % (err, len(gt), fname, pred))
            sys.stdout.flush()
        return (err,conf,len(gt0),trial,fname)

    if not args.quiet:
        print_info(fname+":"+pred)
    ocrolib.write_text(base+".txt",pred)

    if args.show>0 or args.save is not None:
        ion()
        matplotlib.rc('xtick',labelsize=7)
        matplotlib.rc('ytick',labelsize=7)
        matplotlib.rcParams.update({"font.size":7})
        if os.path.exists(base+".gt.txt"):
            transcript = ocrolib.read_text(base+".gt.txt")
            transcript = ocrolib.normalize_text(transcript)
        else:
            transcript = pred
        pred2 = network.trainString(line,transcript,update=0)

        # -------------------- output characters that used in the network --------------
        # it is initially defined in the trained model


        charset = []
        for i in range(0,len(network.outputs.T)):
            c1 = network.l2s([i])
            charset.append(str(c1.encode('utf-8')))
        # print ('charset:',  charset)

        charfile = open('charset.txt','w')
        for item in charset:
            charfile.write("%s" % item)
        charfile.close()

        #-----------------------------------------------------------
        figure("result",figsize=(1400//75,800//75),dpi=75)
        clf()
        subplot(211)

        imshow(line.T,cmap=cm.gray)
        title(transcript)
        
        subplot(212)
        gca().set_xticks([])
        # plot letters on the images of probability matrix----------------------------------------------------
        id = 0
        for px in postition:
            c = character[id]
            #print(px,c)
            id = id+1
            py = charset.index(c)
            #print(py)
            if c==" ":
                c= "sp"
            else:
                c= c
            plt.text(px-2,py+5,c, color='green', fontsize=12)
        
        imshow(network.outputs.T[1:],vmin=0,cmap=cm.hot)


        title(pred[:80])
    # -----------------------------------------------------

        if args.save is not None:
            draw()
            savename = args.save
            if "%" in savename: savename = savename%trial
            print_info("saving "+savename)
            savefig(savename,bbox_inches=0)
        if args.show>0:
            if trial==len(inputs)-1:
                ginput(1,99999999)
            else:
                ginput(1,args.show)
    

        # output probability matrix -------------------------------
        if args.save is not None:
            Text_out = open(savename + ".pm.txt","w") 
            np.savetxt(Text_out, network.outputs.T, fmt='%f') 
            Text_out.close() 


    # ---------------------------------------------------------
    return None



def safe_process1(arg):
    trial,fname = arg
    try:
        return process1(arg)
    except IOError as e:
        if ocrolib.trace: traceback.print_exc()
        print_info(fname+":"+e)
    except ocrolib.OcropusException as e:
        if e.trace: traceback.print_exc()
        print_info(fname+":"+e)
    except:
        traceback.print_exc()
        return None

if args.parallel==0:
    result = []
    for trial,fname in enumerate(inputs):
        result.append(process1((trial,fname)))
elif args.parallel==1:
    result = []
    for trial,fname in enumerate(inputs):
        result.append(safe_process1((trial,fname)))
else:
    pool = Pool(processes=args.parallel)
    result = []
    for r  in pool.imap_unordered(safe_process1,enumerate(inputs)):
        result.append(r)
        if not args.quiet and len(result)%100==0:
            sys.stderr.write("==== %d of %d\n"%(len(result),len(inputs)))

result = [x for x in result if x is not None]

confusions = []

if args.estrate:
    terr = 0
    total = 0
    for err,conf,n,trial,fname, in result:
        terr += err
        total += n
        confusions += conf
    print_info("%.5f %d %d %s" % (terr*1.0/total, terr, total, args.model))
    if args.estconf>0:
        print_info("top %d confusions (count pred gt), comparison: %s" % (
            args.estconf, args.compare))
        for ((u,v),n) in Counter(confusions).most_common(args.estconf):
            print_info("%6d %-4s %-4s" % (n, u ,v))