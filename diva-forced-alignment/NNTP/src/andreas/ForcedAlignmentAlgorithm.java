package andreas;

import java.util.ArrayList;
import java.util.Iterator;

public class ForcedAlignmentAlgorithm {

	// main attributes
	protected TokenPassingGraph graph;
	protected ObservationSequence observation;
	protected Sentence alignment;

	// run time information
	protected int time;
	protected long runTime;
	protected long decodingTime;
	
	public ForcedAlignmentAlgorithm(TokenPassingGraph graph, ObservationSequence observation) {
		this.graph = graph;
		this.observation = observation;
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
			setTokens();
			/**END*/
			
			runTime = (System.currentTimeMillis() - runTime);
			decodingTime += runTime;
			if (verbose) {
				System.out.println(this);
			}
		}
		if (verbose) {
			System.out.println("\nDecoding time: " + decodingTime);
		}
		
		/*********
		* RESULT *
		*********/
		findAlignment();
		/**END*/
	}

	public Sentence getAlignment() {
		return alignment;
	}
	
	public TokenPassingGraph getGraph() {
		return graph;
	}
	
	public long getDecodingTime() {
		return decodingTime;
	}
	
	@Override
	public String toString() {
		return String.format("%-10s", (time+1) + "/" + observation.size()) + String.format("%-5d", Math.round(runTime / 1000f));
	}
	
	protected void initTokenPassing() {
		addToken(new ForcedAlignmentToken(graph.getRoot()));
		setTokens();
	}
	
	protected void addToken(ForcedAlignmentToken token) {
		TokenPassingNode node = token.getNode();
		if (node.getFaNewToken() == null || node.getFaNewToken().getLogProbability() < token.getLogProbability()) {
			node.setFaNewToken(token);
			if (!node.isEmitting()) {
				Iterator<TokenPassingNode> itr = node.childrenIterator();
				while (itr.hasNext()) {
					addToken(token.copyNonEmitting(itr.next()));
				}
			}
		}
	}
	
	protected void setTokens() {
		Iterator<LinkedNode> itr = graph.iterator();
		while (itr.hasNext()) {
			TokenPassingNode node = (TokenPassingNode) itr.next();
			node.setFaTokens();
		}
	}
	
	protected void propagateTokens() {
		Iterator<LinkedNode> itr = graph.iterator();
		while (itr.hasNext()) {
			TokenPassingNode node = (TokenPassingNode) itr.next();
			ForcedAlignmentToken token = node.getFaToken();
			if (token != null && node.isEmitting()) {
				float logObservationProbability = observation.logProbability(time, node.getCharacter());
				Iterator<TokenPassingNode> itr2 = node.childrenIterator();
				while (itr2.hasNext()) {
					addToken(token.copyEmitting(logObservationProbability, itr2.next()));
				}
			}
		}
	}

	protected void findAlignment() {
		alignment = new Sentence();
		Iterator<LinkedNode> itr = graph.iterator();
		while (itr.hasNext()) {
			TokenPassingNode node = (TokenPassingNode) itr.next();
			if (node.isLeaf() && alignment.size() == 0) {
				ArrayList<ForcedAlignmentCharacter> history = node.getFaToken().getHistory();
				for (int i=0; i < history.size(); i++) {
					ForcedAlignmentCharacter c = history.get(i);
					alignment.addWord(new Word(c.getCharacter(), c.getCharacter()), c.getLogProbability(), 0f, c.getEndPosition());
//					System.out.println(c.getStartPosition() + " " + c.getEndPosition() + " " + c.getCharacter() + " " + c.getLogProbability());
				}
			}
		}
	}
	
}
