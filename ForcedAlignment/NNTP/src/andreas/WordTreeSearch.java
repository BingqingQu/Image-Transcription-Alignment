package andreas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class WordTreeSearch {

	protected LinkedList<WordTreeSearchNode> open;
	protected int nbest;
	
	public WordTreeSearch(WordTrellisNode root, int nbest) {
		open = new LinkedList<WordTreeSearchNode>();
		addOpen(0f, root, new Sentence());
		this.nbest = nbest;
	}
	
	public ArrayList<Sentence> search(boolean verbose) {
		ArrayList<Sentence> result = new ArrayList<Sentence>();
		int expansions = 0;
		while (open.size() > 0 && result.size() < nbest) {
			WordTreeSearchNode best = open.removeFirst();
			if (best.isLeaf()) {
				result.add(best.getSentence().reverse());
			} else {
				addOpen(best.getPastCosts(), best.getNode(), best.getSentence());
			}
			expansions += 1;
		}
		if (verbose) {
			System.out.println("Search Expansions: " + expansions);
		}
		return result;
	}
	
	protected void addOpen(float startCosts, WordTrellisNode node, Sentence sentence) {
		Iterator<WordTrellisEdge> itr = node.iterator();
		while (itr.hasNext()) {
			WordTreeSearchNode newNode = new WordTreeSearchNode(startCosts, itr.next(), sentence);
			int index = Collections.binarySearch(open, newNode);
			open.add(-index-1, newNode);
		}
	}
	
	@Override
	public String toString() {
		String str = "*WordTreeSearch* " + open.size() + "\n";
		Iterator<WordTreeSearchNode> itr = open.iterator();
		while (itr.hasNext()) {
			str += itr.next() + "\n";
		}
		return str;
	}
	
}
