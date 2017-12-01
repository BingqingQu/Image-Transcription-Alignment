# perform binarization
./ocropus-nlbin ../input/csg562-009.jpg -o book

# perform page layout analysis
./ocropus-gpageseg 'book/????.bin.png'

# perform text line recognition (on four cores, with a fraktur model)
./ocropus-rpred -Q 4 -m models/fraktur.pyrnn.gz 'book/????/??????.bin.png'

# generate HTML output
python ocropus-hocr.py 'book/????.bin.png' -o ../output/ersch.html -oB ../output/BBox_text.txt

# get text region
python diva-TextBlock.py


