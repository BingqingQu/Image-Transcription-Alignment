#Bingqing Qu, April 2017
#train a lstm model using ocropus

savefreq=1000  # iterations to train before stopping
IterNb=1000000  # LSTM saved frequency
resultFile='diva-model/model.json' # a json file 

# train new model using ocropus 
mkdir diva-model 
chmod +x ../ocropy-master/ocropus-rtrain.py 
python ../ocropy-master/ocropus-rtrain.py -N ${IterNb} -F ${savefreq} -o diva-model/trainedModel TrainingSet/*.png

# select the best model which gives the best performance of recognization
MODELS=diva-model/*.pyrnn.gz

# test very model with all the test line images in the TestSet
for i in $MODELS
do 
	echo "$i"
	../ocropy-master/ocropus-rpred -m "$i" TestSet/*.png
	python ../ocropy-master/ocropus-errs.py TestSet/*.gt.txt -m "$i"
done

# plot the error rate of each saved model
python diva-PlotModelErr.py diva-model/*.pyrnn.gz

# Reading results
minError=$(cat diva-model/trainedModel-error.txt | jq -r '.minimum.error')
minModel=$(cat diva-model/trainedModel-error.txt | jq -r '.minimum.name')
lastError=$(cat diva-model/trainedModel-error.txt | jq -r '.last.error')
lastModel=$(cat diva-model/trainedModel-error.txt | jq -r '.last.name')

visContent="$(base64 diva-model/trainedModel-error.png)"
minModelContent="$(base64 $minModel)"
lastModelContent="$(base64 $lastModel)

# Write output JSON
echo "{\"output\":[{ \"file\": { \"mime-type\": \"image/png\", \"name\":\"trainingError.png\", \"options\":{ \"type\":\"color\", \"visualization\":true }, \"content\": \""$visContent"\" } },{ \"file\": { \"mime-type\": \"application/x-compressed\", \"name\":\"minModel.pyrnn.gz\", \"options\":{ \"type\":\"ocromodel\", \"visualization\":false, \"error\": \""$minError"\" }, \"content\": \""$minModelContent"\" } },{ \"file\": { \"mime-type\": \"application/x-compressed\", \"name\":\"lastModel.pyrnn.gz\", \"options\":{ \"type\":\"ocromodel\", \"visualization\":false, \"error\": \""$lastError"\" }, \"content\": \""$lastModelContent"\" } } ]}" > $resultFile

