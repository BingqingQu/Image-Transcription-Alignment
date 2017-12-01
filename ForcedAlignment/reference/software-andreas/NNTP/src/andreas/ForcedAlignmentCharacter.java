package andreas;

public class ForcedAlignmentCharacter {

	protected String character;
	protected float logProbability;
	protected int startPosition;
	protected int endPosition;
	
	public ForcedAlignmentCharacter(String character, float logProbability, int startPosition) {
		this.character = character;
		this.logProbability = logProbability;
		this.startPosition = startPosition;
		this.endPosition = startPosition;
	}
	
	public ForcedAlignmentCharacter(String character, float logProbability, int startPosition, int endPosition) {
		this.character = character;
		this.logProbability = logProbability;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	public ForcedAlignmentCharacter copy() {
		return new ForcedAlignmentCharacter(character, logProbability, startPosition, endPosition);
	}
	
	public void addObservation(float logProbability) {
		this.logProbability += logProbability;
		this.endPosition++;
	}

	public String getCharacter() {
		return character;
	}

	public float getLogProbability() {
		return logProbability;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}
	
}
