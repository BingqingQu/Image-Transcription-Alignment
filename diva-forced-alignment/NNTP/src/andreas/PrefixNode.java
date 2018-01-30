package andreas;

import java.util.ArrayList;
import java.util.Iterator;

public class PrefixNode extends LinkedNode {

	protected ArrayList<String> prefix;
	protected ArrayList<String> characters;
	protected ArrayList<PrefixNode> children;

	public PrefixNode() {
		super();
		prefix = new ArrayList<String>();
		characters = new ArrayList<String>();
		children = new ArrayList<PrefixNode>();
	}
	
	public boolean hasCharacters() {
		return characters.size() > 0;
	}
	
	public Iterator<String> charactersIterator() {
		return characters.iterator();
	}
	
	public Iterator<PrefixNode> childrenIterator() {
		return children.iterator();
	}
	
	public void addPrefix(String character) {
		this.prefix.add(character);
	}
	
	public void addCharacter(String character) {
		characters.add(character);
	}
	
	public void addChild(PrefixNode child) {
		children.add(child);
	}

	public String getLabel() {
		String str = "[";
		for (int i=0; i < prefix.size(); i++) {
			if (prefix.get(i).compareTo("sp") != 0) {
				str += prefix.get(i);
			}
		}
		str += "][";
		for (int i=0; i < characters.size(); i++) {
			if (characters.get(i).compareTo("sp") != 0) {
				str += characters.get(i);
			}
		}
		str += "]";
		return str;
	}
	
	@Override
	public String toString() {
		String str = getLabel();
		str += ":" + children.size();
		return str;
	}

}
