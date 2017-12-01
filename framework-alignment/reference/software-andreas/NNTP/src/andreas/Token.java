package andreas;

public class Token implements Comparable<Token> {

	protected Float logProbability;
	protected WordHistory history;
	protected TokenPassingNode node;
	
	public Token(WordHistory history, TokenPassingNode node) {
		this(history.getMaxLog(), history, node);
	}
	
	public Token(float logProbability, WordHistory history, TokenPassingNode node) {
		this.logProbability = logProbability;
		this.history = history;
		this.node = node;
	}
	
	public int compareTo(Token token) {
		int result = token.logProbability.compareTo(logProbability);
		if (result == 0) {
			return -1;
		}
		return result;
	}
	
	public Float getLogProbability() {
		return logProbability;
	}
	
	public WordHistory getHistory() {
		return history;
	}
	
	public TokenPassingNode getNode() {
		return node;
	}

	public Token copy(Float logOutputProbability, TokenPassingNode newNode) {
		return new Token(logProbability + logOutputProbability, history, newNode);
	}
	
}
