package andreas;

public class PrefixLeafNode extends PrefixNode {

	protected Word word;

	public PrefixLeafNode() {
		super();
		word = null;
	}
	
	public boolean isLeaf() {
		return true;
	}
	
	public void setWord(Word word) {
		this.word = word;
	}

	public Word getWord() {
		return word;
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += " *" + word.getLabel() + "*";
		return str;
	}

}
