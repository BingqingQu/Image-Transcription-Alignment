# The following will happen:
# - the LSTM outputs are read from the directory observation/ according to the IDs in faIds.txt
# - an alignment is performed with the labels that are specified in faLabels.txt
# - in faParams.txt you can change the parameters of the software, especially the directories
# - the result will appear in the folder alignment/, you can compare it with the results in alignment-andreas/
# - the result provides you with the start and the end position of the labels

# variable
page_img='csg562-063' # name of images
line_image='01'	# model for lstm recognition
model='../input/models/trainedModel-old.pyrnn.gz'
resultFile='../output/book-'${page_img}'/line_images_alignment/aglinemnt.json' 

# paths
path_line_image='../output/book-'${page_img}'/line_images'
path_transcript='../output/book-'${page_img}'/line_images_GT'
path_word_location='../output/book-'${page_img}'/line_images_GT'
path_alignment='../output/book-'${page_img}'/line_images_alignment' 
path_proMatrix='../output/book-'${page_img}'/line_images_proMatrix' 

# for indenpendent forced alignment running
mkdir  '../output/book-'${page_img}

# generate probability matrix
mkdir  '../output/book-'${page_img}'/line_images_proMatrix'
python ../ocropy-master/ocropus-rpred.py -Q 4 -c 20 -S ${path_proMatrix}'/'${line_image} -m ${model} ${path_line_image}'/'${line_image}'.bin.png' --probabilities --llocs --alocs

# generate parameter files
python diva-InputEdit.py -n ${path_proMatrix}'/'${line_image}'.pm.txt' -t ${path_transcript}'/'${line_image}'.gt.txt'

# forced alignment (re-save the alignment result into output folder as a file of '***.al.txt')
java -jar NNTP.jar align params/faIds.txt params/faParams.txt params/faLabels.txt
mkdir  '../output/book-'${page_img}'/line_images_alignment'
python diva-SaveAlignmentObs.py -a 'alignment/faObservation.rec' -d ${path_alignment} -l ${line_image} -ip ${path_line_image}'/'${line_image}'.bin.png' -n ${path_proMatrix}'/'${line_image}'.pm.txt'

# plot results
python diva-plot.py -i ${path_line_image}'/'${line_image}'.bin.png' -t ${path_transcript}'/'${line_image}'.gt.txt' -a ${path_alignment}'/'${line_image}'.alp.txt' -n ${path_proMatrix}'/'${line_image}'.pm.txt' -S ${path_alignment}'/'${line_image}'.png'

# copy grounth truth (line transcription and word location)
mkdir  '../output/book-'${page_img}'/GT'
python diva-CopyGroundtruth.py -i ${page_img} -g '../input/gt_line_transcription/'${page_img} -d '../output/book-'${page_img}'/GT'
python diva-CopyWordLocation.py -i ${page_img} -g '../input/gt_word_location/'${page_img} -d '../output/book-'${page_img}'/GT'

# compute recognition error
mkdir  '../output/book-'${page_img}'/line_images_GT'
python diva-errs.py -i ${page_img} -c 1 -g '../output/book-'${page_img}'/GT' -t '../output/book-'${page_img}'/line_images' -ng '../output/book-'${page_img}'/line_images_GT'

# evaluation (at the save time save letter locations and word locations)
python diva-Evaluation.py -g ${path_word_location}'/'${line_image}'.lc.txt' -l ${line_image} -r ${path_alignment} -sl 1 -sw 1


# read results
files='../output/book-'${page_img}'/line_images/*.bin.png'
textFiles='../output/book-'${page_img}'/line_images_GT/*.gt.txt'

declare -a files
files=($textFiles)
pos=$((${#files[*]}-1))
last=${files[$pos]}

echo "{\"output\":[" > $resultFile

i=1
for f in "${files[@]}"
do
 fileName=$(basename "$f")
 fileContent="$(cat $f)"
 if (($i < 10))
	then
		locFiles='../output/book-'${page_img}'/line_images_alignment/0'$i'.wlc.txt'	
		fileloc="$(cat $locFiles)"
	else
		locFiles='../output/book-'${page_img}'/line_images_alignment/'$i'.wlc.txt'
		fileloc="$(cat $locFiles)"
	fi

 if [[ $f == $last ]]
 then
   echo "{ \"file\": {\"mime-type\": \"text/plain\", \"name\":\""$fileName"\", \"options\":{ \"type\": \"text\", \"visualization\":false}, \"text content\": \""$fileContent"\", \"word locations\": \""$fileloc"\"}}" >> $resultFile
 else
   echo "{ \"file\": {\"mime-type\": \"text/plain\", \"name\":\""$fileName"\", \"options\":{ \"type\": \"text\", \"visualization\":false}, \"text content\": \""$fileContent"\", \"word locations\": \""$fileloc"\"}}," >> $resultFile
 fi
done
echo "]}" >> $resultFile