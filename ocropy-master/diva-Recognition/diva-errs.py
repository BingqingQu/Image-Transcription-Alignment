# Bingqing Qu, June 2017
# compute error rate based on edit distance for recognized text

import argparse,sys,re,os,os.path
import ocrolib
from ocrolib import edist
import shutil

parser = argparse.ArgumentParser()
parser.add_argument('-i','--img_name', type=str, help='NAME OF PAGE IMAGE')
parser.add_argument('-g','--path_gt', type=str, help='PATH TO GROUND TRUTH')
parser.add_argument('-t','--path_txt', type=str, help='PATH TO RECOGNIZED TEXT')
parser.add_argument('-c','--copy_transcript', type=str, help='COPY MATCHED TRANSCRIPTION INTO THE LINE IMAGE FOLDER')
parser.add_argument('-ng','--path_new_gt', type=str, help='PATH TO A NEW FOLDER GROUND TRUTH')
args = parser.parse_args()

page_image = args.img_name
GT_path = args.path_gt
Text_path = args.path_txt
copy_controle = args.copy_transcript
GT_path_new = args.path_new_gt


# load recognized text
txt_files = [os.path.join(Text_path, item) for item in os.listdir(Text_path) if item[-3:] == 'txt']

txt_content = []
for item in txt_files:
    with open(item, 'r') as f:
        tmp = f.read().splitlines()
    txt_content.append([item, tmp])


# load ground truth
gt_files = [os.path.join(GT_path, item) for item in os.listdir(GT_path) if item[-6:] == 'gt.txt']
gt_content = []
for item in gt_files:
    with open(item, 'r') as f:
        tmp = f.read().splitlines()
    gt_content.append([item, tmp])


# compute edit distance 
Err =[]
txt_total = 0       
err_total = 0
gt_total_missing = 0
for i in range(len(txt_content)):
	txt = txt_content[i][1][0]       # get each recognized text line
	txt_total = txt_total + len(txt)   # record the total number of characters for recognized text
	file_tmp = ''
	err_tmp = 101
	gt_total = 0	
	for j in range(len(gt_content)):
		gt = gt_content[j][1][0]		# compare with each text line ground truth
		gt_total = gt_total + len(gt)	# record the total number of characters from ground truth (all the text line transcriptions)
		err = edist.levenshtein(txt,gt)
		if err < err_tmp:				
			err_tmp = err         # keep the minimal err as the conrresponding transcription
			file_tmp = gt_content[j][0]	  # keep the transcription file name 	
			file_tmp_content = gt_content[j][1][0]   # keep the transcription file content 	
		else:
			err_tmp = err_tmp

	Err.append([err_tmp, file_tmp])
	err_total = err_total + err_tmp
	gt_total_missing = gt_total_missing + len(file_tmp_content) # record the total number of characters from ground truth that are used for computing errors

# compute the errors
err_p_missing = err_total*100.0/gt_total_missing   # precision based on the ground truth that are used for computing errors
err_p = err_total*100.0/gt_total      # precision based on the ground truth of all the text line transcriptions
err_r = err_total*100.0/txt_total     # recall based on the recognized text

print 'total errors:', err_total
print 'total transcription:', gt_total
print 'transcription with missing lines:', gt_total_missing
print 'total recognized text:', txt_total

print 'err  precision', err_p
print 'err  precision  missing',err_p_missing
print 'err  recall', err_r

output_files = open(Text_path + '/errors.txt',"w")

output_files.write("total errors                        %8d"%err_total + '\r\n') 
output_files.write("total transcription                 %8d"%gt_total + '\r\n')
output_files.write("transcription with missing lines    %8d"%gt_total_missing + '\r\n')
output_files.write("err  precision                      %8.3f %%"%err_p + '\r\n')
output_files.write("err  precision  missing             %8.3f %%"%err_p_missing + '\r\n')
output_files.write("err  recall                         %8.3f %%"%err_r + '\r\n')
output_files.close()

# copy the best matched transcriptions into the line image folder
if copy_controle>0:
	i = 0
	for err, gt in Err:
		src_gt = gt
		src_lc = gt[:-6]+'lc.txt'
		if i +1 < 10:
			dst_gt = os.path.join(GT_path_new, '{}{}.gt.txt'.format('0', i+1))  
			dst_lc = os.path.join(GT_path_new, '{}{}.lc.txt'.format('0', i+1))
		else:
			dst_gt = os.path.join(GT_path_new, '{}.gt.txt'.format(i+1))
			dst_lc = os.path.join(GT_path_new, '{}.lc.txt'.format(i+1))
		i = i+1

		shutil.copy(src_gt, dst_gt)
		shutil.copy(src_lc, dst_lc)
Err=None
        







