package andreas;

public class WordTreeSearchNode implements Comparable<WordTreeSearchNode> {

	protected Float pastCosts;
	protected Float futureCosts;
	protected Float score;
	protected WordTrellisNode node;
	protected Sentence sentence;

	public WordTreeSearchNode(float startCosts, WordTrellisEdge edge, Sentence startSentence) {
		pastCosts = startCosts + edge.getLogObservation() + edge.getLogLanguage();
		futureCosts = edge.getGoalMaxLog();
		score = pastCosts + futureCosts;
		node = edge.getGoalNode();
		this.sentence = startSentence.copy();
		Word startWord = edge.getStartWord();
		if (startWord != null) {
			sentence.addWord(startWord, edge.getLogObservation(), edge.getLogLanguage(), edge.getStartTime());
		}
	}

	public int compareTo(WordTreeSearchNode node) {
		int result = node.score.compareTo(score);
		if (result == 0) {
			return -1;
		}
		return result; 
	}
	
	public float getPastCosts() {
		return pastCosts;
	}
	
	public WordTrellisNode getNode() {
		return node;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public boolean isLeaf() {
		return node.isLeaf();
	}
	
	public String getLabel() {
		return sentence.getLabel();
	}
	
	public float getScore() {
		return score;
	}

	@Override
	public String toString() {
		return score + " " + isLeaf() + " " + sentence;
	}

}
