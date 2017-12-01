# Framework for alignment between page image and transcription


#---------------- input ---------------------
page_image='csg562-063'
path_gt='../saintgalldb-v1.0/ground_truth'
model='../ocropy-master/diva-TrainModel/Model_20pages/trainedModel-00015000.pyrnn.gz'

#----------------- Ocropus recognition -------------------------------
# using ocropus to run layout analysis, text recognition and probability matrix
mkdir  '../output/book-'${page_image}

# perform binarization
../ocropy-master/ocropus-nlbin ../input/${page_image}.jpg -o ../output/book-${page_image}

# perform page layout analysis
 ../ocropy-master/ocropus-gpageseg '../output/book-'${page_image}'/????.bin.png'

# perform text line recognition (on four cores, with a fraktur model)
python ../ocropy-master/diva-Recognition/ocropus-rpred.py -Q 4 -c 20 -m ${model} '../output/book-'${page_image}'/????/??????.bin.png'

# generate HTML output
python ../ocropy-master/diva-Recognition/ocropus-hocr.py '../output/book-'${page_image}'/????.bin.png' -o '../output/book'-${page_image}'/book.html'

#python diva-ReOrder.py -b '/BingqingQu/DIVA-Work/ocropy-master/diva-Recognition/book-csg562-058/book.html' -i '/BingqingQu/DIVA-Work/ocropy-master/diva-Recognition/book-csg562-058/0001' -o '/BingqingQu/DIVA-Work/ocropy-master/diva-Recognition/book-csg562-058/new' re-order the recognized text line images (following top-down order)
mkdir  '../output/book-'${page_image}'/new'
python ../ocropy-master/diva-Recognition/diva-ReOrder.py -b '../output/book-'${page_image}'/book.html' -i '../output/book-'${page_image}'/0001' -o '../output/book-'${page_image}'/new'

cat ../output/book-${page_image}/new/*.txt > ../output/book-${page_image}/new.txt 

# copy grounth truth (line transcription and word location)
mkdir  '../output/book-'${page_image}'/GT'
python ../ocropy-master/diva-Recognition/diva-CopyGroundtruth.py -i ${page_image} -g ${path_gt}'/line_transcription' -d '../output/book-'${page_image}'/GT'
python ../ocropy-master/diva-Recognition/diva-CopyWordLocation.py -i ${page_image} -g ${path_gt}'/word_location' -d '../output/book-'${page_image}'/GT'

# compute recognition error
mkdir  '../output/book-'${page_image}'/new_GT'
python ../ocropy-master/diva-Recognition/diva-errs.py -i ${page_image} -c 1 -g '../output/book-'${page_image}'/GT' -t '../output/book-'${page_image}'/new' -ng '../output/book-'${page_image}'/new_GT'

#------------------- Forced alignment------------------------------
# files to save intermediate outputs
path_line_image='../output/book-'${page_image}'/new'
path_transcript='../output/book-'${page_image}'/new_GT'
path_word_location='../output/book-'${page_image}'/new_GT'
path_alignment='../output/book-'${page_image}'/new_alignment' 
path_proMatrix='../output/book-'${page_image}'/new_proMatrix' 

mkdir  '../output/book-'${page_image}'/new_proMatrix'
mkdir  '../output/book-'${page_image}'/new_alignment'

# number of segmented lines for a page image
nb_line=$(ls -lR ${path_line_image} | grep "bin.png" | wc -l)

for ((i=1;i<nb_line+1;i++))
do 
	if (($i < 10))
	then
		line_image='0'$i	
	else
		line_image=$i
	fi

	# generate probability matrix
	# -s 1 (show probability matrix)
	# -S path (save probability matrix and the figure)
	python ../ocropy-master/diva-Recognition/ocropus-rpred.py -Q 4 -c 20 -S ${path_proMatrix}'/'${line_image} -m ../ocropy-master/diva-TrainModel/Model_20pages/trainedModel-00015000.pyrnn.gz ${path_line_image}'/'${line_image}'.bin.png' --probabilities --llocs --alocs

	# generate parameter files
	python ../ForcedAlignment/diva-InputEdit.py -n ${path_proMatrix}'/'${line_image}'.pm.txt' -t ${path_transcript}'/'${line_image}'.gt.txt'

	# forced alignment (re-save the alignment result into output folder as a file of '***.al.txt')
	java -jar NNTP.jar align params/faIds.txt params/faParams.txt params/faLabels.txt
	python ../ForcedAlignment/diva-SaveAlignmentObs.py -a 'alignment/faObservation.rec' -d ${path_alignment} -l ${line_image} -ip ${path_line_image}'/'${line_image}'.bin.png' -n ${path_proMatrix}'/'${line_image}'.pm.txt'

	# plot results
	# -s 1 (show aligned image)
	# -S path (save the image)
	python ../ForcedAlignment/diva-plot.py -i ${path_line_image}'/'${line_image}'.bin.png' -t ${path_transcript}'/'${line_image}'.gt.txt' -a ${path_alignment}'/'${line_image}'.alp.txt' -n ${path_proMatrix}'/'${line_image}'.pm.txt' -S ${path_alignment}'/'${line_image}'.png'

	# get ground truth of word location
	python ../ForcedAlignment/diva-CopyWordLocation.py -i ${page_image} -g '../saintgalldb-v1.0/ground_truth/word_location' -d '../output/book-'${page_image}'/GT'

	# evaluation
	python ../ForcedAlignment/diva-Evaluation.py -g ${path_word_location}'/'${line_image}'.lc.txt' -l ${line_image} -r ${path_alignment} -sl 1 -sw 1

done


# ---------------------------- Visulisation ----------------------------

java -Xmx4G -jar ../visualisation/CharSegViz/dist/CharSegViz.jar '../output/book-'${page_image}'/new' '../output/book-'${page_image}'/new_alignment'


