package andreas;

import java.util.ArrayList;
import java.util.Collections;

public class Sentence {

	protected ArrayList<Word> words;
	protected ArrayList<Float> logObservationProbabilities;
	protected ArrayList<Float> logLmProbabilities;
	protected ArrayList<Integer> endPositions;
	
	public Sentence() {
		words = new ArrayList<Word>();
		logObservationProbabilities = new ArrayList<Float>();
		logLmProbabilities = new ArrayList<Float>();
		endPositions = new ArrayList<Integer>();
	}
	
	public int size() {
		return words.size();
	}
	
	public Sentence copy() {
		Sentence sentence = new Sentence();
		for (int i=0; i < words.size(); i++) {
			sentence.addWord(words.get(i), logObservationProbabilities.get(i), logLmProbabilities.get(i), endPositions.get(i));
		}
		return sentence;
	}
	
	public Sentence reverse() {
		Collections.reverse(words);
		Collections.reverse(logObservationProbabilities);
		Collections.reverse(logLmProbabilities);
		Collections.reverse(endPositions);
		return this;
	}
	
	public String getLabel() {
		String str = "";
		for (int i=0; i < words.size(); i++) {
			str += words.get(i).getLabel() + " ";
		}
		return str.trim();
	}
	
	public void addWord(Word word, float logObservationProbability, float logLmProbability, int endPosition) {
		words.add(word);
		endPositions.add(endPosition);
		logObservationProbabilities.add(logObservationProbability);
		logLmProbabilities.add(logLmProbability);
	}

	public float getLogProbability() {
		float log = 0f;
		for (int i=0; i < words.size(); i++) {
			log += logObservationProbabilities.get(i) + logLmProbabilities.get(i);
		}
		return log;
	}
	
	public void uncompress(ObservationSequence os) {
		for (int i=0; i < words.size(); i++) {
			endPositions.set(i, os.uncompressedPosition(endPositions.get(i)));
		}
	}
	
	public String strRecognition() {
		String str = "";
		for (int i=0; i < words.size(); i++) {
			str += getStartPosition(i) + " " + getEndPosition(i) + " " + words.get(i).getLabel() + " " + getLogProbability(i) + "\n";
		}
		str += ".";
		return str;
	}
	
	public String strNbest() {
		float obs = 0f;
		float lm = 0f;
		int numWords = 0;
		String labels = "";
		for (int i=0; i < words.size(); i++) {
			obs += logObservationProbabilities.get(i);
			lm += logLmProbabilities.get(i);
			numWords += 1;
			labels += words.get(i).getLabel() + " ";
		}
		return String.format("%.4f", obs) + " " + String.format("%.4f", lm) + " " + numWords + " " + labels;
	}
	
	@Override
	public String toString() {
		float obs = 0f;
		float lm = 0f;
		String labels = "";
		for (int i=0; i < words.size(); i++) {
			obs += logObservationProbabilities.get(i);
			lm += logLmProbabilities.get(i);
			labels += words.get(i).getLabel() + " ";
		}
		return String.format("%8.2f", (obs+lm)) + " " + labels + "(" + obs + "," + lm + ")";
	}
	
	protected Word getWord(int wordPosition) {
		return words.get(wordPosition);
	}
	
	protected int getStartPosition(int wordPosition) {
		if (wordPosition == 0) {
			return 0;
		}
		return endPositions.get(wordPosition-1) + 1;
	}
	
	protected int getEndPosition(int wordPosition) {
		return endPositions.get(wordPosition);
	}
	
	protected float getLogObservationProbability(int wordPosition) {
		return logObservationProbabilities.get(wordPosition);
	}
	
	protected float getLogLmProbability(int wordPosition) {
		return logLmProbabilities.get(wordPosition);
	}
	
	protected float getLogProbability(int wordPosition) {
		return logObservationProbabilities.get(wordPosition) + logLmProbabilities.get(wordPosition);
	}
	
}
