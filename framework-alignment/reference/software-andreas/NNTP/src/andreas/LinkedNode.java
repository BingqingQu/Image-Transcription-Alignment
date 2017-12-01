package andreas;

public class LinkedNode {

	protected LinkedNode nextNode;

	public LinkedNode() {
		nextNode = null;
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public void setNext(LinkedNode node) {
		nextNode = node;
	}
	
	public boolean hasNext() {
		return nextNode != null;
	}
	
	public LinkedNode getNext() {
		return nextNode;
	}

}
