package andreas.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import andreas.Alphabet;
import andreas.ForcedAlignmentAlgorithm;
import andreas.Lexicon;
import andreas.ObservationSequence;
import andreas.PrefixTree;
import andreas.Sentence;
import andreas.TokenPassingAlgorithm;
import andreas.TokenPassingGraph;
import andreas.Word;
import junit.framework.TestCase;

public class TestTokenPassing extends TestCase {

	protected final String testObservation = "data/testObservation.txt";
	protected final String testSpelling = "data/testSpelling.txt";
	protected final String testLanguageModel = "data/testLanguageModel.txt";
	protected final String realObservation = "data/realObservation1.txt";
	protected final String realSpelling = "data/realSpelling.txt";
	protected final String realLanguageModel = "data/realLanguageModel.txt";
//	protected final String realObservation = "data/307-35.out";
//	protected final String realSpelling = "data/htk.spl";
//	protected final String realLanguageModel = "data/bigram.arpa";
	protected final String largeObservation = "data/hip13/test00000.out";
	protected final String largeSpelling = "data/hip13/test_ext1.kn.300k.o_spl.txt";
	protected final String largeLanguageModel = "data/hip13/test_ext1.kn.300k.o_arpa.txt";
	protected final float maxLog = ObservationSequence.log(0.99f, "logNat");
	protected final float minLog = ObservationSequence.log(0.01f, "logNat");
	protected final float maxP = 0.99f;
	protected final float minP = 0.01f;
	
	public void testForcedAlignment() {
		String logBase = "logNat";
		String transcription = "w o r d w o o d w o r l d";
		String textLine = "EPS EPS EPS w w w EPS o EPS r EPS d EPS EPS EPS w EPS o EPS o o o EPS d EPS EPS EPS w EPS o EPS r EPS l EPS d EPS EPS EPS";
		
		Word word = new Word(transcription.replaceAll("\\s", ""), transcription);
		ArrayList<Word> wordList = new ArrayList<Word>();
		wordList.add(word);
		TreeSet<Word> wordSet = new TreeSet<Word>();
		wordSet.add(word);
		
		createObservationSequence(wordList, textLine);
		ObservationSequence observationSequence = ObservationSequence.read(testObservation, logBase);
		PrefixTree prefixTree = new PrefixTree(wordSet);
		TokenPassingGraph tokenPassingGraph = new TokenPassingGraph(prefixTree);
		
		System.out.println(transcription);
		System.out.println(word.toString());
		System.out.println(observationSequence.toString());
		System.out.println(prefixTree.toString());
		System.out.println(tokenPassingGraph.toString());
		
		ForcedAlignmentAlgorithm forcedAlignmentAlgorithm = new ForcedAlignmentAlgorithm(tokenPassingGraph, observationSequence);
		forcedAlignmentAlgorithm.passTokens(true);
		Sentence alignment = forcedAlignmentAlgorithm.getAlignment();
		
		System.out.println(alignment.strRecognition());
		assertEquals("-0.39", String.format("%.2f", alignment.getLogProbability()));
	}
	
	public void xtestTokenPassing() {
		String logBase = "logNat";
		float compression = 0.9f;
		float gsf = 2f;
		float wip = 1f;
		float nodeBeam = 20f;
		int nodeNbest = 20;
		float wordBeam = 10f;
		int wordNbest = 5;
		int sentenceNbest = 10;
		
		ArrayList<Word> words = new ArrayList<Word>();
		words.add(new Word("word", "w o r d"));
		words.add(new Word("wood", "w o o d"));
		words.add(new Word("world", "w o r l d"));
		String textLine = "EPS EPS EPS w EPS o EPS r EPS d EPS EPS EPS w EPS o EPS o EPS d EPS EPS EPS w EPS o EPS r EPS l EPS d EPS EPS EPS";
		createObservationSequence(words, textLine);
		createSpelling(words);
		createLanguageModel(words);
		
		ObservationSequence observationSequence = ObservationSequence.read(testObservation, logBase).compress(compression, logBase);
		Alphabet alphabet = Alphabet.read(testObservation);
		Lexicon lexicon = Lexicon.read(testSpelling, testLanguageModel, alphabet);
		PrefixTree prefixTree = new PrefixTree(lexicon.getWords());
		TokenPassingGraph tokenPassingGraph = new TokenPassingGraph(prefixTree);
		System.out.println(observationSequence);
		System.out.println(alphabet);
		System.out.println(lexicon);
		System.out.println(prefixTree);
		System.out.println("Tree Nodes: " + prefixTree.size() + "\n");
		System.out.println(tokenPassingGraph);
		System.out.println("Graph Nodes: " + tokenPassingGraph.size() + "\n");
		
		TokenPassingAlgorithm tokenPassingAlgorithm = new TokenPassingAlgorithm(tokenPassingGraph, observationSequence);
		tokenPassingAlgorithm.setParameters(gsf, wip, nodeBeam, nodeNbest, wordBeam, wordNbest, sentenceNbest);
		tokenPassingAlgorithm.passTokens(true);
		tokenPassingAlgorithm.printSentences(10);
		assertEquals("-3.39", String.format("%.2f", tokenPassingAlgorithm.getBestLogProbability()));
	}
	
	public void xtestMultipleSpellings() {
		String logBase = "logNat";
		float compression = 0.9f;
		float gsf = 2f;
		float wip = 1f;
		float nodeBeam = 20f;
		int nodeNbest = 20;
		float wordBeam = 10f;
		int wordNbest = 5;
		int sentenceNbest = 10;
		
		ArrayList<Word> words = new ArrayList<Word>();
		words.add(new Word("word", "w o r d"));
		words.add(new Word("wood", "w o r l d"));
		words.add(new Word("world", "w o r l d"));
		String textLine = "EPS EPS EPS w EPS o EPS r EPS d EPS EPS EPS w EPS o EPS r EPS l EPS d EPS EPS EPS w EPS o EPS r EPS l EPS d EPS EPS EPS";
		createSpelling(words);
		createLanguageModel(words);
		createObservationSequence(words, textLine);
		
		ObservationSequence observationSequence = ObservationSequence.read(testObservation, logBase).compress(compression, logBase);
		Alphabet alphabet = Alphabet.read(testObservation);
		Lexicon lexicon = Lexicon.read(testSpelling, testLanguageModel, alphabet);
		PrefixTree prefixTree = new PrefixTree(lexicon.getWords());
		TokenPassingGraph tokenPassingGraph = new TokenPassingGraph(prefixTree);
		System.out.println(observationSequence);
		System.out.println(alphabet);
		System.out.println(lexicon);
		System.out.println(prefixTree);
		System.out.println(tokenPassingGraph);
		
		TokenPassingAlgorithm tokenPassingAlgorithm = new TokenPassingAlgorithm(tokenPassingGraph, observationSequence);
		tokenPassingAlgorithm.setParameters(gsf, wip, nodeBeam, nodeNbest, wordBeam, wordNbest, sentenceNbest);
		tokenPassingAlgorithm.passTokens(true);
		tokenPassingAlgorithm.printSentences(10);
		assertEquals("-3.41", String.format("%.2f", tokenPassingAlgorithm.getBestLogProbability()));
	}
	
	public void xtestReal() {
		String logBase = "logNat";
		float compression = 0.999f;
		float gsf = 1f;
		float wip = 0f;
		Float nodeBeam = 50f;
		int nodeNbest = 5000;
		Float wordBeam = 50f;
		int wordNbest = 10;
		int sentenceNbest = 1;
		long startTime;
		
		System.out.println("Observation ...");
		startTime = System.currentTimeMillis();
		ObservationSequence uncompressedSequence = ObservationSequence.read(realObservation, logBase);
		ObservationSequence observationSequence = uncompressedSequence.compress(compression, logBase);
		System.out.println("Observations: " + uncompressedSequence.size());
		System.out.println("Compressed: " + observationSequence.size());
		System.out.println("Observation Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");
		
		System.out.println("Alphabet ...");
		startTime = System.currentTimeMillis();
		Alphabet alphabet = Alphabet.read(realObservation);
		System.out.println("Characters: " + alphabet.numCharacters());
		System.out.println("Alphabet Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");

		System.out.println("Lexicon ...");
		startTime = System.currentTimeMillis();
		Lexicon lexicon = Lexicon.read(realSpelling, realLanguageModel, alphabet);
		System.out.println("Words: " + lexicon.numWords());
		System.out.println("Bigrams: " + lexicon.numBigrams());
		System.out.println("Lexicon Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");

		System.out.println("Prefix Tree ...");
		startTime = System.currentTimeMillis();
		PrefixTree prefixTree = new PrefixTree(lexicon.getWords());
		System.out.println("Nodes: " + prefixTree.size());
		System.out.println("Prefix Tree Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");
		
		System.out.println("Token Passing Graph ...");
		startTime = System.currentTimeMillis();
		TokenPassingGraph tokenPassingGraph = new TokenPassingGraph(prefixTree);
		System.out.println("Nodes: " + tokenPassingGraph.size());
		System.out.println("Token Passing Graph Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s\n");
		
		TokenPassingAlgorithm tokenPassingAlgorithm = new TokenPassingAlgorithm(tokenPassingGraph, observationSequence);
		tokenPassingAlgorithm.setParameters(gsf, wip, nodeBeam, nodeNbest, wordBeam, wordNbest, sentenceNbest);
		tokenPassingAlgorithm.passTokens(true);
		tokenPassingAlgorithm.printSentences(10);
		assertEquals("-57.68", String.format("%.2f", tokenPassingAlgorithm.getBestLogProbability()));
	}
	
	public void xtestLarge() {
		String logBase = "logTen";
		float compression = 1.1f;
		float gsf = 1f;
		float wip = 0f;
		int nodeNbest = 15000;
		int wordNbest = 10;
		long startTime;
		
		System.out.println("Observation ...");
		startTime = System.currentTimeMillis();
		ObservationSequence uncompressedSequence = ObservationSequence.read(largeObservation, logBase);
		ObservationSequence observationSequence = uncompressedSequence.compress(compression, logBase);
		System.out.println("Observations: " + uncompressedSequence.size());
		System.out.println("Compressed: " + observationSequence.size());
		System.out.println("Observation Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");
		
		System.out.println("Alphabet ...");
		startTime = System.currentTimeMillis();
		Alphabet alphabet = Alphabet.read(largeObservation);
		System.out.println("Characters: " + alphabet.numCharacters());
		System.out.println("Alphabet Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");

		System.out.println("Lexicon ...");
		startTime = System.currentTimeMillis();
		Lexicon lexicon = Lexicon.read(largeSpelling, largeLanguageModel, alphabet);
		System.out.println("Words: " + lexicon.numWords());
		System.out.println("Bigrams: " + lexicon.numBigrams());
		System.out.println("Lexicon Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");

		System.out.println("Prefix Tree ...");
		startTime = System.currentTimeMillis();
		PrefixTree prefixTree = new PrefixTree(lexicon.getWords());
		System.out.println("Nodes: " + prefixTree.size());
		System.out.println("Prefix Tree Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s");
		
		System.out.println("Token Passing Graph ...");
		startTime = System.currentTimeMillis();
		TokenPassingGraph tokenPassingGraph = new TokenPassingGraph(prefixTree);
		System.out.println("Nodes: " + tokenPassingGraph.size());
		System.out.println("Token Passing Graph Time: " + ((System.currentTimeMillis()-startTime)/1000f) + "s\n");
		
		TokenPassingAlgorithm tokenPassingAlgorithm = new TokenPassingAlgorithm(tokenPassingGraph, observationSequence);
		tokenPassingAlgorithm.setParameters(gsf, wip, null, nodeNbest, null, wordNbest, 1);
		tokenPassingAlgorithm.passTokens(true);
		tokenPassingAlgorithm.printSentences(10);
	}
	
	protected void createSpelling(ArrayList<Word> words) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(testSpelling));
			for (int i=0; i < words.size(); i++) {
				out.println(words.get(i).getLabel() + " " + words.get(i).getSpelling());
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void createLanguageModel(ArrayList<Word> words) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(testLanguageModel));
			out.println("\n\\data\\");
			out.println("ngram 1=" + words.size());
			out.println("ngram 2=" + (words.size()-1));
			out.println("\n\\1-grams:");
			for (int i=0; i < words.size(); i++) {
				out.println(maxLog + "\t" + words.get(i).getLabel() + "\t" + minLog);
			}
			out.println("\n\\2-grams:");
			for (int i=0; i < words.size()-1; i++) {
				out.println(maxLog + "\t" + words.get(i).getLabel() + "\t" + words.get(i+1).getLabel());
			}
			out.println("\n\\end\\");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void createObservationSequence(ArrayList<Word> words, String textLine) {
		ArrayList<String> characters = new ArrayList<String>();
		for (int i=0; i < words.size(); i++) {
			Word word = words.get(i);
			for (int j=0; j < word.numCharacters(); j++) {
				String character = word.getCharacter(j);
				if (!characters.contains(character)) {
					characters.add(character);
				}
			}
		}
		characters.add(ObservationSequence.EPS);
		String[] textLineCharacters = textLine.split(" ");
		ArrayList<ArrayList<Float>> sequence = new ArrayList<ArrayList<Float>>();
		for (int time=0; time < textLineCharacters.length; time++) {
			String textLineCharacter = textLineCharacters[time];
			ArrayList<Float> vector = new ArrayList<Float>();
			for (int i=0; i < characters.size(); i++) {
				String character = characters.get(i);
				if (character.compareTo(textLineCharacter) == 0) {
					vector.add(maxP);
				} else {
					vector.add(minP);
				}
			}
			sequence.add(vector);
		}
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(testObservation));
			String line = "# ";
			for (int i=0; i < characters.size(); i++) {
				if (characters.get(i).compareTo(ObservationSequence.EPS) != 0) {
					line += characters.get(i) + " ";
				}
			}
			out.println(line.trim());
			for (int i=0; i < characters.size(); i++) {
				line = "";
				for (int time=0; time < sequence.size(); time++) {
					line += sequence.get(time).get(i) + " ";
				}
				out.println(line.trim());
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
