package andreas;

public class TokenPassingLeafNode extends TokenPassingNode {

	protected Word word;
	
	public TokenPassingLeafNode(String label) {
		super(label);
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
