# extract text regions using text line info from page segmentation of OCROpus
# Bingqing QU
# 13 April 2017

import os
import numpy as np

# read text line info and them into a matrix, we drop the first line
textlines = np.loadtxt("../output/BBox_text.txt", skiprows=1)
print 'The positions of the text lines:', textlines  

# find the text block further for text line alignment
# text block is defined in a horizontal-x vertical-y coordinate system, the zero is the up left point
textblock = np.empty((1,4))
textblock [ 0, 0 ] = textlines [ :, 0 ].min()
textblock [ 0, 1 ] = textlines [ :, 1 ].min()
textblock [ 0, 2 ] = textlines [ :, 2 ].max()
textblock [ 0, 3 ] = textlines [ :, 3 ].max()
print 'The position of the text block:', textblock

# write the text region position into a txt file
TextBlock_file = open("../output/TextBlock.txt","w") 
np.savetxt(TextBlock_file, textblock, fmt='%.2f') 
TextBlock_file.close() 

