# extract line transcription of Saintgall database (IAM HistDB) from the ground truth
# Bingqing QU
# 02 May 2017

import os
import re
import numpy as np
from string import maketrans

# load transcriptions from the ground truth
transcription = open("transcription.txt", "r")

transcription = transcription.readlines()
lineNB = len(transcription)
print 'total number of lines:', lineNB

# get line transcriptions
for line in transcription:
	#print 'current lines:', line
	spaceID_1 = line.find(' ')			

	# find name of each line and creat a file to save the transcription
	line_name = line[0 : spaceID_1]		
	line_transcription = open("../../saintgalldb-v1.0/ground_truth/line_transcription/"+ line_name + ".gt.txt", "w")
	print line_name
	
	# find the transcription of each line
	spaceID_2 = line[spaceID_1+1 : ].find(' ')			
	line_content = line[spaceID_1+1 : spaceID_1+spaceID_2+1]

	# replace all the '|' as ' ', (as | can be changed, so must use translate to replace it)
	table = maketrans('|', ' ')
	line_content = line_content.translate(table)

	# replace 'et' as '&', 'pt' as '.'
	line_content = line_content.replace('et', '&')
	line_content = line_content.replace('pt', '.')
	line_content = line_content.replace('-', '')
	print line_content

	line_transcription.write(line_content)
	line_transcription.close()
