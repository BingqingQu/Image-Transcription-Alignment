# Bingqing Qu, juillet 2017
# get ground truth of word locations

import os
import re
import numpy as np
from string import maketrans

# load word location from the ground truth
location = open("word_location.txt", "r")

location = location.readlines()
lineNB = len(location)
print 'total number of lines:', lineNB

# get word location
for line in location:
	print 'current lines:', line
	spaceID_1 = line.find(' ')			

	# find name of each line and creat a file to save the word location
	line_name = line[0 : spaceID_1]		
	print line_name
	
	# find the word locations of each line
	spaceID_2 = line[spaceID_1+1 : ].find(' ')			
	line_location = line[spaceID_1+spaceID_2+2:]
	print line_location

	# replace all the '|' as ' ', (as | can be changed, so must use translate to replace it)
	table = maketrans('|', ' ')
	line_location = line_location.translate(table)

	location_file = open("word_location/" + line_name + ".lc.txt", "w")
	location_file.write(line_location)
	location_file.close()
