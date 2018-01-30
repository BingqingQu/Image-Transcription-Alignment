# Bingqing Qu, April 2017
# using ocropus to run layout analysis, text recognition and compute error rate

# input page image
page_img='csg562-063' # name of image, e.g., page_img='csg562-063'
model='../input/models/trainedModel-old.pyrnn.gz'
resultFile='../output/book-'${page_img}'/line_images/recognition.json'  # a json file e.g., resultFile='csg562-063.json'

mkdir  '../output/book-'${page_img}
# perform binarization
../ocropy-master/ocropus-nlbin ../input/page_image/${page_img}.jpg -o ../output/book-${page_img}

# perform page layout analysis
 ../ocropy-master/ocropus-gpageseg '../output/book-'${page_img}'/????.bin.png'

# perform text line recognition (on four cores, with a fraktur model)
python ../ocropy-master/ocropus-rpred.py -Q 4 -c 20 -m ${model} '../output/book-'${page_img}'/????/??????.bin.png' --probabilities --llocs --alocs

# generate HTML output
python ../ocropy-master/ocropus-hocr.py '../output/book-'${page_img}'/????.bin.png' -o '../output/book'-${page_img}'/book.html' -oB '../output/book'-${page_img}'/LineBoxes.txt'

# select line images
mkdir  '../output/book-'${page_img}'/line_images'
python diva-ReOrder.py -b '../output/book-'${page_img}'/book.html' -i '../output/book-'${page_img}'/0001' -o '../output/book-'${page_img}'/line_images'

# copy grounth truth (line transcription and word location)
mkdir  '../output/book-'${page_img}'/GT'
python diva-CopyGroundtruth.py -i ${page_img} -g '../input/gt_line_transcription/'${page_img} -d '../output/book-'${page_img}'/GT'
python diva-CopyWordLocation.py -i ${page_img} -g '../input/gt_word_location/'${page_img} -d '../output/book-'${page_img}'/GT'

# compute recognition error
mkdir  '../output/book-'${page_img}'/line_images_GT'
python diva-errs.py -i ${page_img} -c 1 -g '../output/book-'${page_img}'/GT' -t '../output/book-'${page_img}'/line_images' -ng '../output/book-'${page_img}'/line_images_GT'

# read results
files='../output/book-'${page_img}'/line_images/*.bin.png'
textFiles='../output/book-'${page_img}'/line_images/*.txt'

declare -a files
files=($textFiles)
pos=$((${#files[*]}-1))
last=${files[$pos]}

echo "{\"output\":[" > $resultFile

for f in "${files[@]}"
do
 fileName=$(basename "$f")
 fileContent="$(cat $f)"
 if [[ $f == $last ]]
 then
   echo "{ \"file\": {\"mime-type\": \"text/plain\", \"name\":\""$fileName"\", \"options\":{ \"type\": \"text\", \"visualization\":false}, \"content\": \""$fileContent"\"}}" >> $resultFile
 else
  echo "{ \"file\": {\"mime-type\": \"text/plain\", \"name\":\""$fileName"\", \"options\":{ \"type\": \"text\", \"visualization\":false}, \"content\": \""$fileContent"\"}}," >> $resultFile
 fi
done
echo "]}" >> $resultFile