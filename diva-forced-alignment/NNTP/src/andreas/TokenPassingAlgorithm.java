package andreas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class TokenPassingAlgorithm {

	// main attributes
	protected TokenPassingGraph graph;
	protected ObservationSequence observation;
	protected WordTrellis lattice;
	protected ArrayList<Sentence> sentences;
	protected LinkedList<Token> tokens;
	protected LinkedList<Token> newTokens;
	protected LinkedList<WordTrellisNode> wordNodes;
	
	// parameters
	protected float gsf;
	protected float wip;
	protected Float nodeBeam;
	protected Integer nodeNbest;
	protected Float wordBeam;
	protected Integer wordNbest;
	protected Integer sentenceNbest;
	
	// run time information
	protected int time;
	protected long runTime;
	protected long decodingTime;
	protected long searchTime;
	
	public TokenPassingAlgorithm(TokenPassingGraph graph, ObservationSequence observation) {
		this.graph = graph;
		this.observation = observation;
		lattice = new WordTrellis();
		tokens = new LinkedList<Token>();
		newTokens = new LinkedList<Token>();
		wordNodes = new LinkedList<WordTrellisNode>();
		gsf = 1f;
		wip = 0f;
	}

	public void setParameters(float gsf, float wip, Float nodeBeam, Integer nodeNbest, Float wordBeam, Integer wordNbest, Integer sentenceNbest) {
		this.gsf = gsf;
		this.wip = wip;
		this.nodeBeam = nodeBeam;
		this.nodeNbest = nodeNbest;
		this.wordBeam = wordBeam;
		this.wordNbest = wordNbest;
		this.sentenceNbest = sentenceNbest;
	}

	public void passTokens(boolean verbose) {
		
		/*******
		* INIT *
		*******/
		initTokenPassing();
		/**END*/
		
		if (verbose) {
			System.out.println("*TokenPassing*");
		}
		runTime = 0;
		decodingTime = 0;
		for (time=0; time < observation.size(); time++) {
			runTime = System.currentTimeMillis();
			
			/************
			* TIME STEP *
			************/
			propagateTokens();
			pruneTokens();
			setWordNodes();
			pruneWordNodes();
			updateLattice();
			addRootToken();
			setTokens();
			/**END*/
			
			runTime = (System.currentTimeMillis() - runTime);
			decodingTime += runTime;
			if (verbose) {
				System.out.println(this);
			}
		}
//		decodingTime = Math.round(decodingTime / 1000f);
		if (verbose) {
			System.out.println("\nDecoding time: " + decodingTime);
		}
		searchTime = System.currentTimeMillis();
		
		/*********
		* RESULT *
		*********/
		findSentences(verbose);
		/**END*/
		
		searchTime = (System.currentTimeMillis() - searchTime);
//		searchTime = Math.round(searchTime / 1000f);
		if (verbose) {
			System.out.println("Search time: " + searchTime + "\n");
		}
	}

	public TokenPassingGraph getGraph() {
		return graph;
	}
	
	public Sentence getBestSentence() {
		if (sentences.size() == 0) {
			Sentence result = new Sentence();
			result.addWord(new Word("NULL", "99"), -99, -99, 1);
			return result;
		}
		return sentences.get(0);
	}
	
	public ArrayList<Sentence> getNbestSentences() {
		return sentences;
	}
	
	public WordTrellis getLattice() {
		return lattice;
	}
	
	public float getBestLogProbability() {
		return sentences.get(0).getLogProbability();
	}
	
	public long getDecodingTime() {
		return decodingTime;
	}
	
	public long getSearchTime() {
		return searchTime;
	}
	
	public void printSentences(int max) {
		String str = "*Sentences* " + sentences.size() + "\n";
		for (int i=0; i < max && i < sentences.size(); i++) {
			str += sentences.get(i) + "\n";
		}
		if (max < sentences.size()) {
			str += "  ...\n";
		}
		str += sentences.get(sentences.size()-1) + "\n";
		System.out.println(str);
	}
	
	@Override
	public String toString() {
		return String.format("%-10s", (time+1) + "/" + observation.size()) + String.format("%-5d", Math.round(runTime / 1000f)) + String.format("%-6d", tokens.size()) + String.format("%-6d", lattice.getHistory(time).size());
	}
	
	protected void initTokenPassing() {
		addToken(new Token(lattice.getStartHistory(), graph.getRoot()));
		setTokens();
	}
	
	protected void addToken(Token token) {
		newTokens.add(token);
		TokenPassingNode node = token.getNode();
		if (!node.isEmitting()) {
			Iterator<TokenPassingNode> itr = node.childrenIterator();
			while (itr.hasNext()) {
				addToken(token.copy(0f, itr.next()));
			}
		}
	}
	
	protected void setTokens() {
		tokens.clear();
		Iterator<Token> itr = newTokens.iterator();
		while (itr.hasNext()) {
			tokens.add(itr.next());
		}
		newTokens.clear();
	}
	
	protected void propagateTokens() {
		Iterator<Token> itr = tokens.iterator();
		while (itr.hasNext()) {
			Token token = itr.next();
			TokenPassingNode node = token.getNode();
			if (node.isEmitting()) {
				float logObservationProbability = observation.logProbability(time, node.getCharacter());
				Iterator<TokenPassingNode> itr2 = node.childrenIterator();
				while (itr2.hasNext()) {
					addToken(token.copy(logObservationProbability, itr2.next()));
				}
			}
		}
	}
	
	protected void pruneTokens() {
		if (nodeBeam != null || nodeNbest != null) {
			Collections.sort(newTokens);
//			if (nodeBeam != null) {
//				float maxTokenLog = newTokens.getFirst().getLogProbability();
//				Iterator<Token> itr = newTokens.iterator();
//				while (itr.hasNext()) {
//					if (itr.next().getLogProbability() < maxTokenLog - nodeBeam) {
//						itr.remove();
//					}
//				}
//			}
			if (nodeNbest != null) {
				int numTokens = 0;
				Iterator<Token> itr = newTokens.iterator();
				while (itr.hasNext()) {
					itr.next();
					if (numTokens < nodeNbest) {
						numTokens += 1;
					} else {
						itr.remove();
					}
				}
			}
		}
	}

	protected void setWordNodes() {
		wordNodes.clear();
		Iterator<Token> itr = newTokens.iterator();
		while (itr.hasNext()) {
			Token token = itr.next();
			TokenPassingNode node = token.getNode();
			if (node.isLeaf()) {
				Word word = ((TokenPassingLeafNode) node).getWord();
				WordHistory history = token.getHistory();
				WordTrellisNode wordNode = new WordTrellisNode(word, time, token.getLogProbability() - history.getMaxLog());
//				WordTrellisNode wordNode = new WordTrellisNode(word, time, token.getLogProbability());
//				WordTrellisNode wordNode = new WordTrellisNode(word, time, token.getLogProbability() - history.getMaxObs());
//				wordNode.setMaxObs(token.getLogProbability());
				Iterator<WordTrellisNode> itr2 = history.iterator();
				while (itr2.hasNext()) {
					wordNode.addEdge(itr2.next(), gsf, wip);
				}
				wordNodes.add(wordNode);
			}
		}
	}
	
	protected void pruneWordNodes() {
		if (wordNodes.size() > 0) {
			Collections.sort(wordNodes);
//			if (wordBeam != null) {
//				float maxLog = wordNodes.getFirst().getMaxLog();
//				Iterator<WordTrellisNode> itr = wordNodes.iterator();
//				while (itr.hasNext()) {
//					if (!(itr.next().getMaxLog() > maxLog - wordBeam)) {
//						itr.remove();
//					}
//				}
//			}
			HashSet<String> seenLabels = new HashSet<String>();
			Iterator<WordTrellisNode> itr = wordNodes.iterator();
			while (itr.hasNext()) {
				String label = itr.next().getLabel();
				if (!seenLabels.contains(label)) {
					seenLabels.add(label);
				} else {
					itr.remove();
				}
			}
			if (wordNbest != null) {
				int numTokens = 0;
				itr = wordNodes.iterator();
				while (itr.hasNext()) {
					itr.next();
					if (numTokens < wordNbest) {
						numTokens += 1;
					} else {
						itr.remove();
					}
				}
			}
		}
	}
	
	protected void updateLattice() {
		WordHistory wordHistory = new WordHistory(time);
		if (wordNodes.size() > 0) {
			Iterator<WordTrellisNode> itr = wordNodes.iterator();
			while (itr.hasNext()) {
				wordHistory.add(itr.next());
			}
		}
		lattice.addHistory(wordHistory);
	}
	
	protected void addRootToken() {
		if (wordNodes.size() > 0) {
			addToken(new Token(lattice.getHistory(time), graph.getRoot()));
//			addToken(new Token(0f, lattice.getHistory(time), graph.getRoot()));
//			addToken(new Token(lattice.getHistory(time).getMaxObs(), lattice.getHistory(time), graph.getRoot()));
		}
	}
	
	protected void findSentences(boolean verbose) {
		lattice.finalize();		
		WordTreeSearch treeSearch = new WordTreeSearch(lattice.getLineEnd(), sentenceNbest);
		sentences = treeSearch.search(verbose);
	}
	
}
