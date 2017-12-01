package andreas;

import java.util.Iterator;

public class LinkedNodeIterator implements Iterator<LinkedNode> {

	protected LinkedNode current;
	
	public LinkedNodeIterator(LinkedNode node) {
		current = new LinkedNode();
		current.setNext(node);
	}
	
	public boolean hasNext() {
		return current.hasNext();
	}

	public LinkedNode next() {
		current = current.getNext();
		return current;
	}

	public void remove() {
		// do nothing
	}
	
}
