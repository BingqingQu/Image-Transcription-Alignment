# choose the best Model for recognition
import sys  
import glob  
import os  
import argparse

parser = argparse.ArgumentParser(description = """
select a best model with smallest error in a model file
""")

parser.add_argument("file",default=[],nargs='*',help="input lines")
args = parser.parse_args()

filemodel = args.file
filemodel = filemodel[0]
print filemodel

# filemodel = "Model_15pages"  
f = glob.glob(filemodel + '/*.err.txt')  

smallerr = 1
for file in f : 
	# find error rate files 
    filename = os.path.basename(file)  
    print filename
    file_err = open(filemodel + "/" + filename,"r") 
    Errors = file_err.read()
    print Errors
    # find error no miss rate
    errnomissIDX = Errors.find("errnomiss")
    errnomiss = Errors[errnomissIDX+12:-4]
    errnomiss = float(errnomiss)/100
    print errnomiss
    #find the model with smallest error rate
    if errnomiss < smallerr:
    	smallerr = errnomiss
    	bestmodel = filename
    else:
    	smallerr = smallerr
    	bestmodel = bestmodel

    print "the smallest error rate:", smallerr
    print "the best model name:", bestmodel



  

