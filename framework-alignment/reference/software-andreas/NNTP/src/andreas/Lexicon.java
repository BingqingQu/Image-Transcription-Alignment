package andreas;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class Lexicon {

	protected TreeMap<String,Word> words;
	
	public Lexicon() {
		words = new TreeMap<String,Word>();
	}

	public TreeSet<Word> getWords() {
		TreeSet<Word> treeSet = new TreeSet<Word>();
		Iterator<Word> itr = wordIterator();
		while(itr.hasNext()) {
			treeSet.add(itr.next());
		}
		return treeSet;
	}
	
	public int numWords() {
		return words.size();
	}

	public int numBigrams() {
		int num = 0;
		Iterator<Word> itr = wordIterator();
		while(itr.hasNext()) {
			num += itr.next().numBigrams();
		}
		return num;
	}

	@Override
	public String toString() {
		String str = "*Lexicon*\n";
		Iterator<Word> itr = wordIterator();
		while(itr.hasNext()) {
			str +=  itr.next() + "\n";
		}
		return str;
	}

	public static Lexicon read(String fileSpelling, String fileLanguageModel, Alphabet alphabet) {
		try {
			TreeSet<String> missingCharacters = new TreeSet<String>();
			TreeSet<String> missingWords = new TreeSet<String>();
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(fileSpelling));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			Lexicon lexicon = new Lexicon();
			String line;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				String label = tokenizer.nextToken();
				String spelling = "";
				String outOfAlphabet = null;
				while(tokenizer.hasMoreElements()) {
					String character = tokenizer.nextToken();
					spelling += character + " ";
					if (!alphabet.contains(character)) {
						outOfAlphabet = character;
						missingCharacters.add(character);
					}
				}
				if (outOfAlphabet == null) {
					lexicon.addWord(new Word(label, spelling.trim()));
				}
			}
			stream = new BufferedInputStream(new FileInputStream(fileLanguageModel));
			reader = new BufferedReader(new InputStreamReader(stream));
			while (!(line = reader.readLine()).startsWith("\\1-grams")) {}
			while ((line = reader.readLine()) != null && !line.startsWith("\\2-grams")) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				if (tokenizer.countTokens() == 2) {
					float unigram = Float.valueOf(tokenizer.nextToken());
					String label = tokenizer.nextToken();
					//REPAIR
					//float backoff = unigram;
					float backoff = 0f;
					//REPAIR
					Word word = lexicon.words.get(label);
					if (word != null) {
						word.setLogUnigram(unigram, backoff);
					} else {
						missingWords.add(label);
					}
				} else if (tokenizer.countTokens() == 3) {
					float unigram = Float.valueOf(tokenizer.nextToken());
					String label = tokenizer.nextToken();
					float backoff = Float.valueOf(tokenizer.nextToken());
					Word word = lexicon.words.get(label);
					if (word != null) {
						word.setLogUnigram(unigram, backoff);
					} else {
						missingWords.add(label);
					}
				}
			}
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				if (tokenizer.countTokens() == 3) {
					float bigram = Float.valueOf(tokenizer.nextToken());
					String label1 = tokenizer.nextToken();
					String label2 = tokenizer.nextToken();
					Word word = lexicon.words.get(label2);
					if (word != null) {
						word.addLogBigram(label1, bigram);
					} else {
						missingWords.add(label2);
					}
				}
			}
			if (missingCharacters.size() > 0) {
				System.out.println("WARNING: " + missingCharacters.size() + " missing characters " + missingCharacters);
			}
			if (missingWords.size() > 0) {
				System.out.println("WARNING: " + missingWords.size() + " missing words in spelling file " + missingWords);
			}
			TreeSet<String> missingWords2 = new TreeSet<String>();
			Iterator<Word> iter = lexicon.wordIterator();
			while (iter.hasNext()) {
				Word word = iter.next();
				if (word.unigram == null) {
					missingWords2.add(word.label);
					iter.remove();
				}
			}
			if (missingWords2.size() > 0) {
				System.out.println("WARNING: " + missingWords2.size() + " missing words in arpa file " + missingWords2);
			}
			return lexicon;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Iterator<Word> wordIterator() {
		return words.values().iterator();
	}
	
	protected void addWord(Word word) {
		words.put(word.getLabel(), word);
	}
	
	protected Word getWord(String label) {
		return words.get(label);
	}
	
}

//spelling example:
//
// \data\
// ngram 1=20002
// ngram 2=741493
//
// \1-grams:
// -2.872511	a!	-0.4038165
// [...]
//
// \2-grams:
// [...]
// -2.11815	zoo	specimens
//
// \end\

// language model example:
//
// \data\
// ngram 1=20002
// ngram 2=741493
//
// \1-grams:
// -2.872511	a!	-0.4038165
// [...]
//
// \2-grams:
// [...]
// -2.11815	zoo	specimens
//
// \end\