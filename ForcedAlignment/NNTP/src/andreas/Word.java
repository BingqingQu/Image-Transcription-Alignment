package andreas;

import java.util.TreeMap;

public class Word implements Comparable<Word> {

	protected String label;
	protected String spelling;
	protected String[] characters;
	protected Float unigram;
	protected Float backoff;
	protected TreeMap<String,Float> bigrams;
	
	public Word(String label, String spelling) {
		this.label = label;
		this.spelling = spelling;
		characters = spelling.split(" ");
		unigram = null;
		backoff = null;
		bigrams = new TreeMap<String,Float>();
	}
	
	public int compareTo(Word word) {
		return label.compareTo(word.label);
	}
	
	public int numCharacters() {
		return characters.length;
	}
	
	public int numBigrams() {
		return bigrams.size();
	}
	
	public void setLogUnigram(float unigram, float backoff) {
		this.unigram = unigram;
		this.backoff = backoff;
	}
	
	public void addLogBigram(String label, float bigram) {
		bigrams.put(label, bigram);
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getSpelling() {
		return spelling;
	}
	
	public String getCharacter(int characterPosition) {
		return characters[characterPosition];
	}

	public Float getLogBigram(Word lastWord) {
		Float log = bigrams.get(lastWord.getLabel());
		if (log != null) {
			return log;
		}
		if (lastWord.backoff != null && unigram != null) {
			return lastWord.backoff + unigram;
		}
		if (unigram != null) {
			return unigram;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "[" + label + "][" + spelling + "](" + unigram + ")(" + backoff + "):" + bigrams.size();
	}
	
}
