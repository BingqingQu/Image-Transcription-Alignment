package andreas;

import java.util.Iterator;
import java.util.LinkedList;

public class WordHistory {

	protected int time;
	protected LinkedList<WordTrellisNode> nodes;
	
	public WordHistory(int time) {
		this.time = time;
		nodes = new LinkedList<WordTrellisNode>();
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public float getMaxLog() {
		return nodes.getFirst().getMaxLog();
	}
	
	// X
	public float getMaxObs() {
		return nodes.getFirst().getMaxObs();
	}
	// X
	
	public int size() {
		return nodes.size();
	}
	
	public Iterator<WordTrellisNode> iterator() {
		return nodes.iterator();
	}
	
	public void add(WordTrellisNode node) {
		nodes.add(node);
	}
	
	@Override
	public String toString() {
		String str = "*WordHistory* " + size() + "\n";
		Iterator<WordTrellisNode> itr = iterator();
		while (itr.hasNext()) {
			str += itr.next() + "\n";
		}
		return str;
	}
	
}
