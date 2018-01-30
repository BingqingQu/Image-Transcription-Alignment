package andreas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class PrefixTree implements Iterable<LinkedNode> {

	protected PrefixNode root;
	protected int size;

	public PrefixTree(TreeSet<Word> words) {
		root = createTree(new ArrayList<String>(), words);
		linkDepthFirst(root, null);
		size = calculateSize();
	}
	
	public Iterator<LinkedNode> iterator() {
		return new LinkedNodeIterator(root);
	}
	
	public PrefixNode getRoot() {
		return root;
	}
	
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		String str = "*PrefixTree*\n";
		Iterator<LinkedNode> itr = iterator();
		while (itr.hasNext()) {
			str += itr.next() + "\n";
		}
		return str;
	}

	protected PrefixNode createTree(ArrayList<String> prefix, TreeSet<Word> words) {
		if (words.size() == 0) {
			return null;
		} else if (words.size() == 1) {
			return createPrefixLeafNode(prefix, words.first());
		} else {
			return createPrefixNode(prefix, words);
		}
	}
	
	protected PrefixNode linkDepthFirst(PrefixNode node, PrefixNode parent) {
		PrefixNode newParent = node;
		if (parent != null) {
			parent.setNext(node);
		}
		Iterator<PrefixNode> itr = node.childrenIterator();
		while (itr.hasNext()) {
			newParent = linkDepthFirst(itr.next(), newParent);
		}
		return newParent;
	}
	
	protected PrefixLeafNode createPrefixLeafNode(ArrayList<String> prefix, Word word) {
		PrefixLeafNode node = new PrefixLeafNode();
		addPrefix(node, prefix);
		for (int i=prefix.size(); i < word.numCharacters(); i++) {
			node.addCharacter(word.getCharacter(i));
		}
		node.setWord(word);
		return node;
	}
	
	protected PrefixNode createPrefixNode(ArrayList<String> prefix, TreeSet<Word> words) {
		PrefixNode node = new PrefixNode();
		addPrefix(node, prefix);
		int characterPosition = prefix.size();
		ArrayList<String> newPrefix = new ArrayList<String>();
		for (int i=0; i < prefix.size(); i++) {
			newPrefix.add(prefix.get(i));
		}
		ArrayList<TreeSet<Word>> prefixSets = getPrefixSets(characterPosition, words);
		while (prefixSets.size() == 1) {
			String character = words.first().getCharacter(characterPosition);
			node.addCharacter(character);
			newPrefix.add(character);
			characterPosition += 1;
			prefixSets = getPrefixSets(characterPosition, words);
		}
		for (int i=0; i < prefixSets.size(); i++) {
			node.addChild(createTree(newPrefix, prefixSets.get(i)));
		}
		return node;
	}
	
	protected void addPrefix(PrefixNode node, ArrayList<String> prefix) {
		for (int i = 0; i < prefix.size(); i++) {
			node.addPrefix(prefix.get(i));
		}
	}
	
	protected ArrayList<TreeSet<Word>> getPrefixSets(int characterPosition, TreeSet<Word> words) {
		ArrayList<TreeSet<Word>> prefixSets = new ArrayList<TreeSet<Word>>();
		TreeSet<Word> currentSet = new TreeSet<Word>();
		Iterator<Word> itr = words.iterator();
		if (words.size() > 1 && characterPosition > words.first().numCharacters()-1) {
			while (itr.hasNext()) {
				currentSet = new TreeSet<Word>();
				currentSet.add(itr.next());
				prefixSets.add(currentSet);
			}
			return prefixSets;
		}
		while (itr.hasNext()) {
			Word currentWord = itr.next();
			if (!currentSet.isEmpty() && currentWord.getCharacter(characterPosition).compareTo(currentSet.last().getCharacter(characterPosition)) != 0) {
				prefixSets.add(currentSet);
				currentSet = new TreeSet<Word>();
			}
			currentSet.add(currentWord);
		}
		if (!currentSet.isEmpty()) {
			prefixSets.add(currentSet);
		}
		return prefixSets;
	}
	
	protected int calculateSize() {
		int numNodes = 0;
		Iterator<LinkedNode> itr = iterator();
		while (itr.hasNext()) {
			itr.next();
			numNodes += 1;
		}
		return numNodes;
	}

}
