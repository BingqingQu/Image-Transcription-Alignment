package andreas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TokenPassingNode extends LinkedNode {
	
	protected String label;
	protected String character;
	protected ArrayList<TokenPassingNode> children;
	
	// not used anymore
	protected ArrayList<Token> tokens;
	protected HashMap<Integer,Token> newTokens;
	
	// forced alignment
	protected ForcedAlignmentToken faToken;
	protected ForcedAlignmentToken faNewToken;
	public ForcedAlignmentToken getFaToken() { return faToken; }
	public ForcedAlignmentToken getFaNewToken() { return faNewToken; }
	public void setFaNewToken(ForcedAlignmentToken faNewToken) { this.faNewToken = faNewToken; }
	public void setFaTokens() {
		this.faToken = faNewToken; 
		faNewToken = null;
	}

	public TokenPassingNode(String label) {
		super();
		this.label = label;
		character = null;
		children = new ArrayList<TokenPassingNode>();
		tokens = new ArrayList<Token>();
		newTokens = new HashMap<Integer,Token>();
	}
	
	public boolean isEmitting() {
		return character != null;
	}
	
	public Iterator<TokenPassingNode> childrenIterator() {
		return children.iterator();
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setCharacter(String character) {
		this.character = character;
	}
	
	public String getCharacter() {
		return character;
	}
	
	public void addChild(TokenPassingNode node) {
		children.add(node);
	}
	
	public boolean hasTokens() {
		return tokens.size() > 0;
	}
	
	public boolean hasNewTokens() {
		return newTokens.size() > 0;
	}
	
	public Iterator<Token> tokenIterator() {
		return tokens.iterator();
	}
	
	public Iterator<Token> newTokenIterator() {
		return newTokens.values().iterator();
	}
	
	public void resetTokens() {
		tokens.clear();
	}
	
	public void resetNewTokens() {
		newTokens.clear();
	}
	
	public void addToken(Token token) {
		tokens.add(token);
	}
	
	public void addNewToken(Token token) {
		int startTime = token.getHistory().getTime();
		float log = token.getLogProbability();
		Token current = newTokens.get(startTime);
		if (current == null || log > current.getLogProbability()) {
			newTokens.put(startTime, token);
		}
	}
	
	public void beamPruning(float maxLog, float beam, ArrayList<Float> nodeLogs) {
		Iterator<Token> itr = newTokens.values().iterator();
		while (itr.hasNext()) {
			float log = itr.next().getLogProbability();
			if (log > maxLog - beam) {
				nodeLogs.add(log);
			} else {
				itr.remove();
			}
		}
	}
	
	public void nbestPruning(float minLog) {
		Iterator<Token> itr = newTokens.values().iterator();
		while (itr.hasNext()) {
			float log = itr.next().getLogProbability();
			if (!(log > minLog)) {
				itr.remove();
			}
		}
	}
	
	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
	@Override
	public String toString() {
		return label + "(" + character + ")" + "(" + tokens.size() + "):" + children.size();
	}

}
