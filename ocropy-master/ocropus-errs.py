#!/usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function

import argparse,sys,os,os.path,multiprocessing
import ocrolib
from ocrolib import edist

parser = argparse.ArgumentParser(description = """
Compute the edit distances between ground truth and recognizer output.
Run with the ground truth files as arguments, and it will find the
corresponnding recognizer output files using the given extension (-x).
Missing output files are handled as empty strings, unless the -s
option is given.
""")
parser.add_argument("files",default=[],nargs='*',help="input lines")
parser.add_argument("-x","--extension",default=".txt",help="extension for recognizer output, default: %(default)s")
# parser.add_argument("-g","--gtextension",default=".gt.txt",help="extension for ground truth, default: %(default)s")
parser.add_argument("-k","--kind",default="exact",help="kind of comparison (exact, nospace, letdig, letters, digits, lnc), default: %(default)s")
parser.add_argument("-e","--erroronly",action="store_true",help="only output an error rate")
parser.add_argument("-s","--skipmissing",action="store_true",help="don't use missing or empty output files in the calculation")
parser.add_argument("-Q","--parallel",type=int,default=multiprocessing.cpu_count())
parser.add_argument("-m","--modelname",default="Model.pyrnn.gz")
args = parser.parse_args()
args.files = ocrolib.glob_all(args.files)

if not ".gt." in args.files[0]:
    sys.stderr.write("warning: compare on .gt.txt files, not .txt files\n")

def process1(fname):
    # fgt = ocrolib.allsplitext(fname)[0]+args.gtextension
    gt = ocrolib.project_text(ocrolib.read_text(fname),kind=args.kind)
    ftxt = ocrolib.allsplitext(fname)[0]+args.extension
    missing = 0
    if os.path.exists(ftxt):
        txt = ocrolib.project_text(ocrolib.read_text(ftxt),kind=args.kind)
    else:
        missing = len(gt)
        txt = ""
    err = edist.levenshtein(txt,gt)
    print(txt)
    print(gt)
    print(err)
    return fname,err,len(gt),missing

outputs = ocrolib.parallel_map(process1,args.files,parallel=args.parallel,chunksize=10)

print(args.files)
print(outputs)

errs = 0
total = 0
missing = 0
for fname,e,t,m in sorted(outputs):
    if not args.erroronly:
        print("%6d\t%6d\t%s" % (e, t, fname))
    errs += e
    total += t
    missing += m

err_files = args.modelname.replace("pyrnn","err")
err_files = err_files.replace("gz","txt")
output_files = open(err_files,"w")
output_files.write("errors    %8d"%errs + '\r\n') 
output_files.write("missing   %8d"%missing + '\r\n')
output_files.write("total     %8d"%total + '\r\n')
output_files.write("err       %8.3f %%"%(errs*100.0/total) + '\r\n')
output_files.write("errnomiss %8.3f %%"%((errs-missing)*100.0/total) + '\r\n')
output_files.close()

print("errors    %8d"%errs)
print("missing   %8d"%missing)
print("total     %8d"%total)
print("err       %8.3f %%"%(errs*100.0/total))
print("errnomiss %8.3f %%"%((errs-missing)*100.0/total))

if args.erroronly:
    print(errs * 1.0 / total)

