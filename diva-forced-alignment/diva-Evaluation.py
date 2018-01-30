# Bingqing Qu, Juillet 2017
# Evaluation on alignment between text image and transcriptions


import sys
import argparse

# ---------------------------- alignment error rate -----------------------------
def Alignment_error_rate(gt_location, res_location):
	# AER: measures the amount of totally erroneous assignments produced between line images and transcriptions
	if len(gt_location) != len(res_location):
		print 'Forced alignment results need the same length as the ground truth!'
		sys.exit("Error message")
	else:
		match = 0
		for loc_gt, loc_res in zip(gt_location, res_location):
			#print loc_gt, loc_res
			if loc_gt[0]<=loc_res[0]<=loc_gt[1] or loc_gt[0]<=loc_res[1]<=loc_gt[1] or (loc_res[0]<=loc_gt[0]&loc_res[1]>=loc_gt[1]):
				match = match + 1
			else:
				match = match

	AER = match*1.0/len(gt_location)
	# print "number of match word:", match
	return AER

# ---------------------------- alignment recall -----------------------------
def Alignment_recall(gt_location, res_location):
	# AR = covered pixel / word pixel in gt
	if len(gt_location) != len(res_location):
		print 'Forced alignment results need the same length as the ground truth!'
		sys.exit("Error message")
	else:
		coverd_pixel = 0
		gt_pixel = 0
		for loc_gt, loc_res in zip(gt_location, res_location):
			gt_pixel =  gt_pixel + abs(loc_gt[1] - loc_gt[0])
			#print loc_gt, loc_res
			if loc_gt[0]<=loc_res[0]<=loc_gt[1] or loc_gt[0]<=loc_res[1]<=loc_gt[1] or (loc_res[0]<=loc_gt[0]&loc_res[1]>=loc_gt[1]):
				coverd_pixel = coverd_pixel+ abs(min(loc_gt[1],loc_res[1])-max(loc_gt[0],loc_res[0]))
			else:
				coverd_pixel = coverd_pixel
	# print "number of match pixel:", coverd_pixel	
	AR = 1-coverd_pixel*1.0/gt_pixel

	return AR

# ---------------------------- alignment precision -----------------------------
def Alignment_precision(gt_location, res_location):
	# AP = covered pixel / word pixel in obtained result
	if len(gt_location) != len(res_location):
		print 'Forced alignment results need the same length as the ground truth!'
		sys.exit("Error message")
	else:
		coverd_pixel = 0
		res_pixel = 0
		for loc_gt, loc_res in zip(gt_location, res_location):
			res_pixel =  res_pixel + abs(loc_res[1] - loc_res[0])
			#print loc_gt, loc_res
			if loc_gt[0]<=loc_res[0]<=loc_gt[1] or loc_gt[0]<=loc_res[1]<=loc_gt[1] or (loc_res[0]<=loc_gt[0]&loc_res[1]>=loc_gt[1]):
				coverd_pixel = coverd_pixel+abs(min(loc_gt[1],loc_res[1])-max(loc_gt[0],loc_res[0]))
			else:
				coverd_pixel = coverd_pixel

	# print "number of match pixel:", coverd_pixel	
	AP = 1-coverd_pixel*1.0/res_pixel

	return AP


# --------------------- from letter location to word locations --------------------
def Word_location(letter_loc, letter):
	temp_loc=[]
	for id in range(len(letter)):
		if letter[id]!='EPS':
			temp_loc.append(letter_loc[id])
	word_start = int(temp_loc[0][0:(temp_loc[0].find(' '))])
	word_end = int(temp_loc[-1][(temp_loc[-1].find(' ')):])
	word_loc = [word_start, word_end]

	return word_loc

# --------------------------- main ---------------------------
if __name__ == '__main__':
    # _, hocr_path = sys.argv
	parser = argparse.ArgumentParser()
	parser.add_argument('-g','--file_gt', type=str, help='FILE OF GROUND TRUTH')
	parser.add_argument('-r','--path_res', type=str, help='PATH TO OBTAINED LOCATION')
	parser.add_argument('-l','--line_image', type=str, help='LINE IMAGE NAME')
	parser.add_argument('-sl','--save_letter_loc', type=str, help='SAVE LETTER LOCATION TO A FILE')
	parser.add_argument('-sw','--save_word_loc', type=str, help='SAVE WORD LOCATION TO A FILE')
	args = parser.parse_args()

	file_gt_location = args.file_gt
	path_res_location = args.path_res
	line_image = args.line_image
	save_letter_controle = args.save_letter_loc
	save_word_controle = args.save_word_loc

    # ---------------------------- get word locations of forced alignment results --------------------
    # get obtained locations (need to get word locations from the given letter locations)
	file = open(path_res_location + '/'+ line_image + '.al.txt', "r")
	res = file.readlines()
	res = res[1:-1]

    # delete the first and the end 'EPS'
	if res[0].find('EPS')>0:
		res = res[1:]
	# if res[len(res)-1].find('EPS')>0:
	# 	res = res[0:-1]

	# split the locations and the letters into two different matrice for an easier programming
	tmp_location = []
	tmp_letter = []
	for loc in res:
		#print loc
		a = loc.find(' ')
		b = loc[a+1:].find(' ')
		c = loc[a+b+2:].find(' ')
		tmp_location.append(loc[0:a+b+1])
		tmp_letter.append(loc[a+b+2:a+b+c+2])

	# save letter location
	if save_letter_controle > 0:
		F = open(path_res_location + '/'+ line_image + '.llc.txt','w') 
		for item_loc, item_lt in zip(tmp_location,tmp_letter):
			if item_lt != 'EPS':
				F.write((item_loc+' '+item_lt+'\n'))
		F.close()

	# get word loactions
	res_location = []
	after_letter = tmp_letter
	after_location = tmp_location
	while 'sp' in after_letter:
		id = after_letter.index('sp')
		before_letter = after_letter[0:id]
		before_location = after_location[0:id]

		word_location = Word_location(before_location, before_letter)
		res_location.append(word_location)

		after_letter = after_letter[id+1:]
		after_location = after_location[id+1:]

	# print after_letter,after_location
	if after_letter !=[]:
		word_location = Word_location(after_location, after_letter)
		res_location.append(word_location)

	# print 'Obtained word locations:', res_location

	# save word location
	if save_word_controle > 0:
		F = open(path_res_location + '/'+ line_image + '.wlc.txt','w') 
		for item in res_location:
			F.write((str(item[0])+'-'+str(item[1])+' '))
		F.close()

# ------------------------------ get word location of ground truth  --------------------------
	file = open(file_gt_location, "r")
	gt = file.readlines()

	after_gt_location = gt[0]
	gt_location = []
	while ' ' in after_gt_location:
		id = after_gt_location.find(' ')
		before_gt_location = after_gt_location[0:id]
		id1 = before_gt_location.find('-')
		word_location = [int(before_gt_location[0:id1]), int(before_gt_location[id1+1:])]
		gt_location.append(word_location)

		after_gt_location = after_gt_location[id+1:]

	if after_gt_location != []:
		before_gt_location = after_gt_location[0:-1]
		id1 = before_gt_location.find('-')
		word_location = [int(before_gt_location[0:id1]), int(before_gt_location[id1+1:])]
		gt_location.append(word_location)

	# print 'Ground truth world locations:', gt_location
 
	# shift the obtained results to do the error rates comoutations, making the positions start with 0 
	# (because of the text line segmentation, the line image is not always start from the first letter, 
	# however the ground truth postions start from the first letter's position)
	gap = res_location[0][0]
	image_scale =1.14
	# print "gap is:", gap
	res_location_gap = []
	for item in res_location:
		item_gap = [int((item[0]-gap)*image_scale), int((item[1]-gap)*image_scale)]
		res_location_gap.append(item_gap)
	# print 'Obtained word locations with gap:', res_location_gap
	
	# evaluate the forced alignment with alignment error rate, recall and precision
	AER = Alignment_error_rate(gt_location,res_location_gap)
	AR = Alignment_recall(gt_location,res_location_gap)
	AP = Alignment_precision(gt_location,res_location_gap)

	print 'Alingment error rate:', AER
	print 'Alingment Recall:', AR
	print 'Alingment precision:', AP

	output_files = open(path_res_location + '/'+ line_image + '.err.txt',"w")

	output_files.write("alingment error rate   %8f"%AER + '\r\n') 
	output_files.write("Alingment Recall       %8f"%AR + '\r\n')
	output_files.write("Alingment precision    %8f"%AP + '\r\n')
	output_files.close()








