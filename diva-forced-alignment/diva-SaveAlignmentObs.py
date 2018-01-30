# Bingqing Qu, June 2017
# save alignment result (observation.rec) into output file, and rename it

import re
import sys
import os
import shutil
import argparse
import numpy as np
import matplotlib.image as mpimg

parser = argparse.ArgumentParser()
parser.add_argument('-a','--path_alignment', type=str, help='PATH TO ALIGNMENT RESULT')
parser.add_argument('-d','--path_des', type=str, help='PATH TO DESTINATION')
parser.add_argument('-i','--name_image', type=str, help='NAME OF IMAGE')
parser.add_argument('-l','--name_line', type=str, help='NAME OF LINE')
parser.add_argument('-ip','--path_image', type=str, help='PATH TO IMAGE')
parser.add_argument('-n','--path_network', type=str, help='PATH TO PROBABILITY MATRIX')
args = parser.parse_args()

alignment_path = args.path_alignment
Des_path = args.path_des
Line_name = args.name_line
image_path = args.path_image
network_path = args.path_network


src_txt = alignment_path
dst_txt = Des_path + '/'+ Line_name+'.alp.txt'  # orignial aligned results before scaling
shutil.copy(src_txt, dst_txt)

# ----------------------------- re-scale the letter positions on probability matrix -------------------------
# to get the positions on the image
# image size
img = mpimg.imread(image_path)
h_i, w_i = np.array(img).shape
# print 'image size:', h_i, w_i

# network size
ProMatrix = np.loadtxt(network_path, skiprows=0)
h_p, w_p = ProMatrix.shape
# print 'network size:', h_p, w_p

pad = 16
scale = w_i*1.0/(w_p-2*pad)
# print 'scale 2:', scale

# positions on the image
file = open(alignment_path,'r')
observation_rec = file.readlines()
observation_ls = observation_rec[0]
observation_le = observation_rec[-1]

file = open(Des_path + '/'+ Line_name+'.al.txt','w')  # scaled aligned results
file.write(observation_ls)

observation = np.genfromtxt(alignment_path, skip_header=1, skip_footer=1, dtype='str')
observation_up = observation
# print 'observation:', observation

# gap = observation[1,0]
gap = 16
i =0
for s,e in zip(observation[:,0],observation[:,1]):
	observation_up[i,0] = int((int(s)-int(gap))*scale)
	observation_up[i,1] = int((int(e)-int(gap))*scale)
	# observation_up[i,0] = int(int(s-pad)*scale)
	# observation_up[i,1] = int(int(e-pad)*scale)
	file.write("%s"%observation_up[i,0])
	file.write(" ")
	file.write("%s"%observation_up[i,1])
	file.write(" ")
	file.write("%s"%observation[i,2])
	file.write(" ")
	file.write("%s"%observation[i,3])
	file.write('\r\n')
	i = i+1
file.write(observation_le)
file.close()

# print 'observation scaled:', observation_up







