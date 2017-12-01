# The following will happen:
# - the LSTM outputs are read from the directory observation/ according to the IDs in faIds.txt
# - an alignment is performed with the labels that are specified in faLabels.txt
# - in faParams.txt you can change the parameters of the software, especially the directories
# - the result will appear in the folder alignment/, you can compare it with the results in alignment-andreas/
# - the result provides you with the start and the end position of the labels

# variable
page_image='csg562-058'
line_image='01'
path_line_image='../output/book-'${page_image}'/new'
path_transcript='../output/book-'${page_image}'/new_GT'
path_word_location='../output/book-'${page_image}'/new_GT'
path_alignment='../output/book-'${page_image}'/new_alignment' 
path_proMatrix='../output/book-'${page_image}'/new_proMatrix' 

# generate probability matrix
mkdir  '../output/book-'${page_image}'/new_proMatrix'
python ../ocropy-master/diva-Recognition/ocropus-rpred.py -Q 4 -c 20 -S ${path_proMatrix}'/'${line_image} -m ../ocropy-master/diva-TrainModel/Model_20pages/trainedModel-00015000.pyrnn.gz ${path_line_image}'/'${line_image}'.bin.png' --probabilities --llocs --alocs

# generate parameter files
python diva-InputEdit.py -n ${path_proMatrix}'/'${line_image}'.pm.txt' -t ${path_transcript}'/'${line_image}'.gt.txt'

# forced alignment (re-save the alignment result into output folder as a file of '***.al.txt')
java -jar NNTP.jar align params/faIds.txt params/faParams.txt params/faLabels.txt
mkdir  '../output/book-'${page_image}'/new_alignment'
python diva-SaveAlignmentObs.py -a 'alignment/faObservation.rec' -d ${path_alignment} -l ${line_image} -ip ${path_line_image}'/'${line_image}'.bin.png' -n ${path_proMatrix}'/'${line_image}'.pm.txt'

# plot results
python diva-plot.py -i ${path_line_image}'/'${line_image}'.bin.png' -t ${path_transcript}'/'${line_image}'.gt.txt' -a ${path_alignment}'/'${line_image}'.alp.txt' -n ${path_proMatrix}'/'${line_image}'.pm.txt' -s ${path_alignment}'/'${line_image}'.png'

# get ground truth of word location
#python diva-CopyWordLocation.py -i ${page_image} -g '../saintgalldb-v1.0/ground_truth/word_location' -d '../output/book-'${page_image}'/GT'

# evaluation (at the save time save letter locations and word locations)
python diva-Evaluation.py -g ${path_word_location}'/'${line_image}'.lc.txt' -l ${line_image} -r ${path_alignment} -sl 1 -sw 1