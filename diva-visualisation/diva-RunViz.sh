# Bingqing Qu, Juillet 2017
# Run the visualisation tool of letter locations that created by Matias

# chmod a+x diva-RunViz.sh
page_img='csg562-063'

java -Xmx4G -jar CharSegViz/dist/CharSegViz.jar '../output/book-'${page_img}'/line_images' '../output/book-'${page_img}'/line_images_alignment'
# java -Xmx4G -jar CharSegViz/dist/CharSegViz.jar 'data/image' 'data/letter_location'