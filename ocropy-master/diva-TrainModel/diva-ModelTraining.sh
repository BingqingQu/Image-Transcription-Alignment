# train new model using ocropus
chmod +x ../ocropus-rtrain

python ../ocropus-rtrain.py -N 1 -F 1000 -c charset-temp.txt TrainingSet/*.gt.txt -o trainedModel TrainingSet/*.png

# select the best model which gives the best performance of recognization
MODELS=Model_20pages/*.pyrnn.gz

# test very model with all the test line images in the TestSet
for i in $MODELS
do 
	echo "$i"
	../ocropus-rpred -m "$i" TestSet/*.png
	python ocropus-errs.py TestSet/*.gt.txt -m "$i"
done

# choose the best Model for recognition
python diva-ModelSelect.py Model_20pages

