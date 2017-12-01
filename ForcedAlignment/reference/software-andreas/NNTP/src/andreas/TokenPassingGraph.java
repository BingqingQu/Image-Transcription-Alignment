package andreas;

import java.util.Iterator;

public class TokenPassingGraph implements Iterable<LinkedNode> {

	protected TokenPassingNode root;
	protected int size;

	public TokenPassingGraph(PrefixTree tree) {
		TokenPassingNode[] newGraphNodes = createRootNodes();
		createNodes(tree.getRoot(), newGraphNodes, newGraphNodes[1]);
		size = calculateSize();
	}
	
	public Iterator<LinkedNode> iterator() {
		return new LinkedNodeIterator(root);
	}
	
	public TokenPassingNode getRoot() {
		return root;
	}
	
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		String str = "*TokenPassingGraph*\n";
		Iterator<LinkedNode> itr = iterator();
		while (itr.hasNext()) {
			str += itr.next() + "\n";
		}
		return str;
	}
	
	protected TokenPassingNode[] createRootNodes() {
		root = new TokenPassingNode("ROOT");
		TokenPassingNode newEps = new TokenPassingNode("ROOTEPS");
		newEps.setCharacter(ObservationSequence.EPS);
		root.addChild(newEps);
		newEps.addChild(newEps);
		root.setNext(newEps);
		return new TokenPassingNode[] { root, newEps };
	}
	
	protected TokenPassingNode createNodes(PrefixNode prefixNode, TokenPassingNode[] lastGraphNodes, TokenPassingNode lastLink) {
		TokenPassingNode[] newGraphNodes = null;
		if (!prefixNode.hasCharacters()) {
			newGraphNodes = createNullNode(prefixNode, lastGraphNodes);
			lastLink.setNext(newGraphNodes[1]);
		} else {
			TokenPassingNode[] currentGraphNodes = lastGraphNodes;
			Iterator<String> itr = prefixNode.charactersIterator();
			while (itr.hasNext()) {
				newGraphNodes = createCharacterNodes(prefixNode, itr.next(), currentGraphNodes);
				newGraphNodes[0].setNext(newGraphNodes[1]);
				if (!lastLink.hasNext()) {
					lastLink.setNext(newGraphNodes[0]);
				} else {
					currentGraphNodes[1].setNext(newGraphNodes[0]);
				}
				currentGraphNodes = newGraphNodes;
			}
		}
		if (prefixNode.isLeaf()) {
			TokenPassingLeafNode leafNode = createLeafNode((PrefixLeafNode) prefixNode, newGraphNodes);
			newGraphNodes[1].setNext(leafNode);
			return leafNode;
		}
		TokenPassingNode newLink = newGraphNodes[1];
		Iterator<PrefixNode> itr = prefixNode.childrenIterator();
		while (itr.hasNext()) {
			newLink = createNodes(itr.next(), newGraphNodes, newLink);
		}
		return newLink;
	}
	
	protected TokenPassingNode[] createNullNode(PrefixNode prefixNode, TokenPassingNode[] lastGraphNodes) {
		TokenPassingNode nullNode = new TokenPassingNode(prefixNode.getLabel());
		if (lastGraphNodes[0] != null) {
			lastGraphNodes[0].addChild(nullNode);
		}
		lastGraphNodes[1].addChild(nullNode);
		return new TokenPassingNode[] { null, nullNode };
	}
	
	protected TokenPassingNode[] createCharacterNodes(PrefixNode prefixNode, String character, TokenPassingNode[] lastGraphNodes) {
		TokenPassingNode newChar = new TokenPassingNode(prefixNode.getLabel());
		newChar.setCharacter(character);
		TokenPassingNode newEps = new TokenPassingNode(prefixNode.getLabel());
		newEps.setCharacter(ObservationSequence.EPS);
		newChar.addChild(newChar);
		newChar.addChild(newEps);
		newEps.addChild(newEps);
		if (lastGraphNodes[0] != null && lastGraphNodes[0].getCharacter() != null && lastGraphNodes[0].getCharacter().compareTo(newChar.getCharacter()) != 0) {
			lastGraphNodes[0].addChild(newChar);
		}
		lastGraphNodes[1].addChild(newChar);
		return new TokenPassingNode[] { newChar, newEps };
	}
	
	protected TokenPassingLeafNode createLeafNode(PrefixLeafNode prefixLeafNode, TokenPassingNode[] lastGraphNodes) {
		TokenPassingLeafNode leafNode = new TokenPassingLeafNode(prefixLeafNode.getLabel());
		leafNode.setWord(prefixLeafNode.getWord());
		if (lastGraphNodes[0] != null) {
			lastGraphNodes[0].addChild(leafNode);
		}
		lastGraphNodes[1].addChild(leafNode);
		return leafNode;
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
