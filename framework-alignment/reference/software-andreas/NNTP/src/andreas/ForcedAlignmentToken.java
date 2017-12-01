package andreas;

import java.util.ArrayList;

public class ForcedAlignmentToken implements Comparable<ForcedAlignmentToken> {

	protected Float logProbability;
	protected ArrayList<ForcedAlignmentCharacter> history;
	protected TokenPassingNode node;
	
	public ForcedAlignmentToken(TokenPassingNode root) {
		this.logProbability = 0f;
		this.history = new ArrayList<ForcedAlignmentCharacter>();
		this.node = root;
	}
	
	public ForcedAlignmentToken(float logProbability, ArrayList<ForcedAlignmentCharacter> history, TokenPassingNode node) {
		this.logProbability = logProbability;
		this.history = history;
		this.node = node;
	}

	public ForcedAlignmentToken copyNonEmitting(TokenPassingNode newNode) {
		float newProbability = logProbability;
		ArrayList<ForcedAlignmentCharacter> newHistory = copyHistory();
		return new ForcedAlignmentToken(newProbability, newHistory, newNode);
	}
	
	public ForcedAlignmentToken copyEmitting(float logOutputProbability, TokenPassingNode newNode) {
		float newProbability = logProbability + logOutputProbability;
		ArrayList<ForcedAlignmentCharacter> newHistory = copyHistory();
		if (isSameCharacter()) {
			newHistory.get(newHistory.size()-1).addObservation(logOutputProbability);
		} else {
			ForcedAlignmentCharacter newCharacter = new ForcedAlignmentCharacter(node.getCharacter(), logOutputProbability, currentPosition());
			newHistory.add(newCharacter);
		}
		return new ForcedAlignmentToken(newProbability, newHistory, newNode);
	}
	
	public int compareTo(ForcedAlignmentToken token) {
		int result = token.logProbability.compareTo(logProbability);
		if (result == 0) {
			return -1;
		}
		return result;
	}
	
	public Float getLogProbability() {
		return logProbability;
	}
	
	public ArrayList<ForcedAlignmentCharacter> getHistory() {
		return history;
	}
	
	public TokenPassingNode getNode() {
		return node;
	}
	
	// helper
	
	private ArrayList<ForcedAlignmentCharacter> copyHistory() {
		ArrayList<ForcedAlignmentCharacter> newHistory = new ArrayList<ForcedAlignmentCharacter>();
		for (int i=0; i < history.size(); i++) {
			newHistory.add(history.get(i).copy());
		}
		return newHistory;
	}
	
	private boolean historyIsEmpty() {
		return history.size() == 0;
	}
	
	private boolean isSameCharacter() {
		if (historyIsEmpty()) {
			return false;
		}
		return (node.getCharacter().compareTo(history.get(history.size()-1).getCharacter()) == 0);
	}
	
	private int currentPosition() {
		if (historyIsEmpty()) {
			return 0;
		}
		return (history.get(history.size()-1).getEndPosition() + 1);
	}
	
}
