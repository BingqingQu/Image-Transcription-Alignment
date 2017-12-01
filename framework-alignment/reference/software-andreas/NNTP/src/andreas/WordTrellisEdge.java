package andreas;

public class WordTrellisEdge {

	protected WordTrellisNode start;
	protected WordTrellisNode goal;
	protected float logObservation;
	protected float logLanguage;
	protected Integer latticeId;
	
	public WordTrellisEdge(WordTrellisNode start, WordTrellisNode goal, float logObservation, float logLanguage) {
		this.start = start;
		this.goal = goal;
		this.logObservation = logObservation;
		this.logLanguage = logLanguage;
		latticeId = null;
	}

	public float getGoalMaxLog() {
		return goal.getMaxLog();
	}

	public float getLogObservation() {
		return logObservation;
	}

	public float getLogLanguage() {
		return logLanguage;
	}

	public Word getStartWord() {
		return start.getWord();
	}

	public int getStartTime() {
		return start.getTime();
	}

	public WordTrellisNode getGoalNode() {
		return goal;
	}
	
	public String getGoalLabel() {
		Word goalWord = goal.getWord();
		if (goalWord == null) {
			return "NULL";
		}
		return goalWord.getLabel();
	}
	
	public int getLatticeId() {
		return latticeId;
	}
	
	public void setLatticeId(int latticeId) {
		this.latticeId = latticeId;
	}
	
	public String strLattice() {
		String str = "";
		str += "J=" + String.format("%-10d", getLatticeId());
		str += " S=" + String.format("%-10d", goal.getLatticeId());
		str += " E=" + String.format("%-10d", start.getLatticeId());
		str += " a=" + String.format("%-10.2f", logObservation);
		str += " l=" + String.format("%.2f", logLanguage);
		return str;
	}

}
