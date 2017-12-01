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
parser.add_argument('-t','--path_transcript', type=str, help='PATH TO TRANSCRIPTION')
args = parser.parse_args()

image_path = args.path_image
transcript_path = args.path_transcript

# --------------------------- load result --------------------------------
observation = np.genfromtxt("alignment/faObservation.rec", skip_header=1, skip_footer=1,dtype='str')
position = observation[:,0]
character = observation[:,2]
print 'The recognized text position:', position
print 'The recognized text:', character

#-------------------------------load image and transcription --------------------------
img = mpimg.imread(image_path)
file = open(transcript_path, "r")
# img = mpimg.imread("../ocropy-master/diva-Recognition/book-csg562-058/0001/010014.bin.png")
# file = open("../saintgalldb-v1.0/ground_truth/line_transcription/csg562-058-20.gt.txt", "r")
Text0 = file.readlines()
transcript = " ".join(str(x) for x in Text0)
print 'transcription:', transcript

charset = ['', ' ', '~', '&', '.', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'X', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']
ProMatrix = np.loadtxt("../ocropy-master/diva-Recognition/Network_14.txt", skiprows=0)

# ------------------- plot letters on the images of probability matrix----------------------------------------------------
plt.figure("result",figsize=(1400//75,800//75),dpi=75)
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
		print(px,c)
		if c=='sp':
			cc= ' '	
			py = charset.index(cc)
		else:
			py = charset.index(c)
		print(py)
		plt.text(int(px),py+2,c, color='red', fontsize=10)

plt.imshow(ProMatrix[1:],vmin=0,cmap='hot') # don't show position of eps
#imshow(network.outputs.T,vmin=0,cmap=cm.hot) # show position of eps
plt.title(transcript)

plt.show()








