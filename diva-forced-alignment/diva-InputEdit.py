# Bingqing Qu, May 2017
# Edit input file for forced alignment

import os
import numpy as np
import argparse


parser = argparse.ArgumentParser()
parser.add_argument('-n','--path_network', type=str, help='PATH TO PROBABILITY MATRIX')
parser.add_argument('-t','--path_transcript', type=str, help='PATH TO TRANSCRIPTION')
args = parser.parse_args()

network_path = args.path_network
transcript_path = args.path_transcript


# ------------- Generate faObservation.txt -----------------------------------------------------------------
# read probability matrix
ProMatrix = np.loadtxt(network_path, skiprows=0)
# print 'size of the probability matrix:', ProMatrix.shape
ProMatrix = np.concatenate((ProMatrix[1:],ProMatrix[0:1]),axis=0)
# print 'The probability matrix:', ProMatrix  

# read characters
file = open("charset.txt", "r")
CharSet0 = file.readlines()
CharSet = " ".join(str(x) for x in CharSet0)  # turn list to str 
# print 'The character set:', CharSet
file.close()

# generation forced alignment file
file = open('observation/faObservation.txt', 'w')


# the first line is all characters of the alphabet (the output neurons of the LSTM)
file.write("# ")
file.write("sp")
for s in CharSet[1:]:
	file.write(" %s"%s)
file.write('\r\n')

# the second line contains all outputs of the first neuron (letter) over time, the third line all outputs of the second neuron (letter), etc.
# the last line is special, it contains all outputs of the so-called 'epsilon' neuron that indicates 'no character'
np.savetxt(file, ProMatrix, fmt='%f') 
file.close()

# ----------- Generate faLabels.txt --------------------------------------------------------------------------
# read transcriptions
file = open(transcript_path, "r") # transcriptions
Text0 = file.readlines()
Text = " ".join(str(x) for x in Text0)  # turn list to str 
# print 'The transcription:', Text
file.close()

file = open('params/faLabels.txt', 'w')
for s in Text:
	if s == " ":
		s = "sp"
	else:
		s = s
	file.write("%s"%s)	
	file.write(" ")	
file.close()













