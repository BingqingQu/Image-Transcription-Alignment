# Bingqing Qu, June 2017
# plot forced alignment results (the postions of each letter)

import os
import numpy as np
import matplotlib.image as mpimg
import matplotlib.pyplot as plt
import matplotlib
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-i','--path_image', type=str, help='PATH TO IMAGE')
parser.add_argument('-s','--show',default=-1,type=float,
                    help="if >0, shows aligned result in a window and waits this many seconds")
parser.add_argument('-S','--save_image_path', type=str, help='SAVE ALIGNMENT IMAGE OR NOT')
parser.add_argument('-t','--path_transcript', type=str, help='PATH TO TRANSCRIPTION')
parser.add_argument('-a','--path_alignment', type=str, help='PATH TO ALIGNMENT RESULT')
parser.add_argument('-n','--path_network', type=str, help='PATH TO PROBABILITY MATRIX')
args = parser.parse_args()

image_path = args.path_image
save_image_path = args.save_image_path
transcript_path = args.path_transcript
alignment_path = args.path_alignment
network_path = args.path_network


# --------------------------- load result --------------------------------
observation = np.genfromtxt(alignment_path, skip_header=1, skip_footer=1,dtype='str')
position = observation[:,0]
character = observation[:,2]

#-------------------------------load image and transcription --------------------------
img = mpimg.imread(image_path)
h_i, w_i = np.array(img).shape

file = open(transcript_path, "r")
Text0 = file.readlines()
transcript = " ".join(str(x) for x in Text0)
print 'transcription:', transcript

charset = ['', ' ', '~', '&', '.', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'X', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']
ProMatrix = np.loadtxt(network_path, skiprows=0)
h_p, w_p = ProMatrix.shape

# ------------------- plot letters on the images of probability matrix----------------------------------------------------
fig = plt.figure("result",figsize=(1400//75,800//75),dpi=75)
plt.clf()
plt.subplot(211)
plt.imshow(img,cmap='gray')
plt.title(transcript)
	        
plt.subplot(212)
plt.gca().set_xticks([])
id = 0
for px in position:
	c = character[id]
	id = id+1
	if c!="EPS":
		#print(px,c)
		if c=='sp':
			cc= ' '	
			py = charset.index(cc)
		else:
			py = charset.index(c)
		#print(py)
		plt.text(int(px),py+2, c, color='red', fontsize=10)

plt.imshow(ProMatrix[1:],vmin=0,cmap='hot') # don't show position of eps
#imshow(network.outputs.T,vmin=0,cmap=cm.hot) # show position of eps
plt.title(transcript)

if args.show>0:
	plt.show()
if save_image_path is not None:
	fig.savefig(save_image_path,bbox_inches=0)









