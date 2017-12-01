package andreas;

import java.util.ArrayList;
import java.util.Iterator;

public class WordTrellisNode implements Comparable<WordTrellisNode> {
	
	protected Word word;
	protected int time;
	protected Float logObservation;
	protected Float maxLog;
	protected ArrayList<WordTrellisEdge> edges;
	protected boolean visited;
	protected Integer latticeId;
	
	// X
	protected Float maxObs;
	public float getMaxObs() {
		return maxObs;
	}
	public void setMaxObs(float maxObs) {
		this.maxObs = maxObs;
	}
	// X
	
	public WordTrellisNode(Word word, int time, float logObservation) {
		this(word, time, logObservation, null);
	}
	
	public WordTrellisNode(Word word, int time, float logObservation, Float maxLog) {
		this.word = word;
		this.time = time;
		this.logObservation = logObservation;
		this.maxLog = maxLog;
		edges = new ArrayList<WordTrellisEdge>();
		visited = false;
		latticeId = null;
		// X
		maxObs = 0f;
		// X
	}
	
	public int compareTo(WordTrellisNode node) {
		int result = node.maxLog.compareTo(maxLog);
		if (result == 0) {
			return -1;
		}
		return result;
	}

	public Iterator<WordTrellisEdge> iterator() {
		return edges.iterator();
	}
	
	public void addNullEdge(WordTrellisNode previousNode) {
		add(previousNode, 0f);
	}
	
	public void addEdge(WordTrellisNode previousNode, float gsf, float wip) {
		float logLanguage = 0f;
		if (previousNode.word != null) {
			logLanguage = gsf * word.getLogBigram(previousNode.word) - wip;
		} else {
			logLanguage = -wip;
		}
		add(previousNode, logLanguage);
	}
	
	protected void add(WordTrellisNode previousNode, float logLanguage) {
		edges.add(new WordTrellisEdge(this, previousNode, logObservation, logLanguage));
		float logNode = previousNode.maxLog + logObservation + logLanguage;
		if (maxLog == null || logNode > maxLog) {
			maxLog = logNode;
		}
	}
	
	public float getMaxLog() {
		return maxLog;
	}
	
	public Word getWord() {
		return word;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

	public boolean isLeaf() {
		return time == -1;
	}
	
	public String getLabel() {
		if (word == null) {
			return "!NULL";
		}
		return word.getLabel();
	}
	
	public void setVisited() {
		visited = true;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public int getLatticeId() {
		return latticeId;
	}
	
	public void setLatticeId(int latticeId) {
		this.latticeId = latticeId;
	}
	
	public String strLattice() {
		String str = "I=" + String.format("%-10d", getLatticeId()) + " t=" + String.format("%-10.2f", (float) getTime()) + " W=" + String.format("%-20s", getLabel());
		if (getLabel().compareTo("!NULL") != 0) {
			str += " v=1";
		}
		return str;
	}
	
	@Override
	public String toString() {
		return word.getLabel() + " " + time + " " + maxLog;
	}
	
}
