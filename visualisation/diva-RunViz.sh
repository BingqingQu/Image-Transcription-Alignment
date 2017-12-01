# Bingqing Qu, Juillet 2017
# Run the visualisation tool of letter locations that created by Matias

# chmod a+x diva-RunViz.sh
page_image='csg562-063'

java -Xmx4G -jar CharSegViz/dist/CharSegViz.jar '../output/book-'${page_image}'/new' '../output/book-'${page_image}'/new_alignment'
# java -Xmx4G -jar CharSegViz/dist/CharSegViz.jar 'data/image' 'data/letter_location'