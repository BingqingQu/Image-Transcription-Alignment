package andreas;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class Main {

	protected MainParams params;
	protected TokenPassingGraph tokenPassingGraph;

	public static void main(String[] args) {
		ArrayList<String> modes = new ArrayList<String>();
		modes.add("latrec");
		modes.add("align");
		if (args.length < 2) {
			System.err.println("usage: java -jar NNTP.jar mode fileIds [fileParams] [fileLabels]");
			System.err.println("modes: " + modes);
		} else if (!modes.contains(args[0])) {
			System.err.println("unknown mode: " + args[0]);
		} else if (args[0].compareTo("latrec") == 0) {
			MainParams params = new MainParams();
			params.readEnv();
			params.readFileIds(args[1]);
			params.readFileParams(args[2]);
			Main main = new Main(params);
			main.initLexiconGraph();
			main.recognition();
		} else if (args[0].compareTo("align") == 0) {
			MainParams params = new MainParams();
			params.readEnv();
			params.readFileIds(args[1]);
			params.readFileParams(args[2]);
			params.readFileLabels(args[3]);
			Main main = new Main(params);
			main.alignment();
		} 
	}
	
	public Main(MainParams params) {
		this.params = params;
	}

	public void initLexiconGraph() {
		Alphabet alphabet = Alphabet.read(params.fileObservation(0));
		Lexicon lexicon = Lexicon.read(params.get("fileSpelling"), params.get("fileLanguageModel"), alphabet);
		PrefixTree prefixTree = new PrefixTree(lexicon.getWords());
		tokenPassingGraph = new TokenPassingGraph(prefixTree);
	}
	
	public void recognition() {
		for (int idx=0; idx < params.numIds(); idx++) {
			String fileObservation = params.fileObservation(idx);
			String logBase = params.get("obsLogBase");
			ObservationSequence uncompressedSequence = ObservationSequence.read(fileObservation, logBase);
			ObservationSequence observationSequence = uncompressedSequence.compress(params.getFloat("compression"), logBase);
			TokenPassingAlgorithm tokenPassingAlgorithm = new TokenPassingAlgorithm(tokenPassingGraph, observationSequence);
//			tokenPassingAlgorithm.setParameters(params.getFloat("gsf"), params.getFloat("wip"), params.getFloat("nodeBeam"), params.getInt("nodeNbest"), params.getFloat("wordBeam"), params.getInt("wordNbest"), params.getInt("sentenceNbest"));
			tokenPassingAlgorithm.setParameters(params.getFloat("gsf"), params.getFloat("wip"), null, params.getInt("nodeNbest"), null, params.getInt("wordNbest"), 1);
			tokenPassingAlgorithm.passTokens(false);
			float compressionFactor = ((float) uncompressedSequence.size()) / observationSequence.size();
			float decodingTime = tokenPassingAlgorithm.getDecodingTime();
			int length = observationSequence.size();
			WordTrellis lattice = tokenPassingAlgorithm.getLattice();
			lattice.uncompress(observationSequence);
			Sentence sentence = tokenPassingAlgorithm.getBestSentence();
			sentence.uncompress(observationSequence);
			writeLatRec(idx, decodingTime, compressionFactor, sentence.strRecognition(), lattice, length);
		}
	}
	
	public void alignment() {
		for (int idx=0; idx < params.numIds(); idx++) {
			String fileObservation = params.fileObservation(idx);
			String logBase = params.get("obsLogBase");
			ObservationSequence observationSequence = ObservationSequence.read(fileObservation, logBase);
			String label = params.getLabel(idx);
			TreeSet<Word> wordSet = new TreeSet<Word>();
			wordSet.add(new Word(label.replaceAll("\\s", ""), label));			
			PrefixTree prefixTree = new PrefixTree(wordSet);
			TokenPassingGraph tokenPassingGraph = new TokenPassingGraph(prefixTree);
			ForcedAlignmentAlgorithm forcedAlignmentAlgorithm = new ForcedAlignmentAlgorithm(tokenPassingGraph, observationSequence);
			forcedAlignmentAlgorithm.passTokens(false);
			float decodingTime = forcedAlignmentAlgorithm.getDecodingTime();
			float compressionFactor = 1f;
			Sentence alignment = forcedAlignmentAlgorithm.getAlignment();
			int length = observationSequence.size();
			writeRec(idx, decodingTime, compressionFactor, alignment.strRecognition(), length);
		}
	}
	
	// write files
	
	private void writeRec(int idx, float decodingTime, float compressionFactor, String sentence, int length) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(params.fileRecognition(idx)));
			out.println("# cpu=" + decodingTime + " compression=" + compressionFactor + " length=" + length);
			out.println(sentence);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void writeLatRec(int idx, float decodingTime, float compressionFactor, String sentence, WordTrellis lattice, int length) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(params.fileRecognition(idx)));
			out.println("# cpu=" + decodingTime + " compression=" + compressionFactor + " length=" + length);
			out.println(sentence);
			out.close();
			out = new PrintWriter(new FileOutputStream(params.fileRecognitionLattice(idx)));
			lattice.writeLattice(out, params, idx);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

//modes.add("recognition");
//modes.add("nbest");
//float searchTime = tokenPassingAlgorithm.getSearchTime();
//if (mode.compareTo("recognition") == 0) {
//	Sentence sentence = tokenPassingAlgorithm.getBestSentence();
//	sentence.uncompress(observationSequence);
//	writeRecognition(idx, compressionFactor, decodingTime, searchTime, "REC", sentence.strRecognition());
//} else if (mode.compareTo("nbest") == 0) {
//	ArrayList<Sentence> sentences = tokenPassingAlgorithm.getNbestSentences();
//	String nbest = "";
//	for (int i=0; i < sentences.size(); i++) {
//		nbest += sentences.get(i).strNbest() + "\n";
//	}
//	writeRecognition(idx, compressionFactor, decodingTime, searchTime, "NBEST", nbest);
//} else
