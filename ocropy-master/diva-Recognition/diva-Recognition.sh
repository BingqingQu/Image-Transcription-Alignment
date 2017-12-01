# Bingqing Qu, April 2017
# using ocropus to run layout analysis, text recognition and compute error rate

# input page image
page_image='csg562-063'
path_gt='../../saintgalldb-v1.0/ground_truth'
model='../diva-TrainModel/Model_20pages/trainedModel-00015000.pyrnn.gz'

mkdir  '../../output/book-'${page_image}

# perform binarization
../ocropus-nlbin ../../input/${page_image}.jpg -o ../../output/book-${page_image}

# perform page layout analysis
 ../ocropus-gpageseg '../../output/book-'${page_image}'/????.bin.png'

# perform text line recognition (on four cores, with a fraktur model)
#python ocropus-rpred.py -Q 4 -c 20 -S save -s 1 -m ../diva-TrainModel/Model_20pages/trainedModel-00015000.pyrnn.gz 'book/????/010010.bin.png' --probabilities --llocs --alocs
python ocropus-rpred.py -Q 4 -c 20 -m ${model} '../../output/book-'${page_image}'/????/??????.bin.png' --probabilities --llocs --alocs

# generate HTML output
python ocropus-hocr.py '../../output/book-'${page_image}'/????.bin.png' -o '../../output/book'-${page_image}'/book.html'

#python diva-ReOrder.py -b '/BingqingQu/DIVA-Work/ocropy-master/diva-Recognition/book-csg562-058/book.html' -i '/BingqingQu/DIVA-Work/ocropy-master/diva-Recognition/book-csg562-058/0001' -o '/BingqingQu/DIVA-Work/ocropy-master/diva-Recognition/book-csg562-058/new' re-order the recognized text line images (following top-down order)
mkdir  '../../output/book-'${page_image}'/new'
python diva-ReOrder.py -b '../../output/book-'${page_image}'/book.html' -i '../../output/book-'${page_image}'/0001' -o '../../output/book-'${page_image}'/new'

cat ../../output/book-${page_image}/new/*.txt > ../../output/book-${page_image}/new.txt 

# copy grounth truth (line transcription and word location)
mkdir  '../../output/book-'${page_image}'/GT'
python diva-CopyGroundtruth.py -i ${page_image} -g ${path_gt}'/line_transcription' -d '../../output/book-'${page_image}'/GT'
python diva-CopyWordLocation.py -i ${page_image} -g ${path_gt}'/word_location' -d '../../output/book-'${page_image}'/GT'

# compute recognition error
mkdir  '../../output/book-'${page_image}'/new_GT'
python diva-errs.py -i ${page_image} -c 1 -g '../../output/book-'${page_image}'/GT' -t '../../output/book-'${page_image}'/new' -ng '../../output/book-'${page_image}'/new_GT'

