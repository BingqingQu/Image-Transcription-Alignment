package andreas;

import java.util.Iterator;
import java.util.TreeMap;

public class Observation {

	protected TreeMap<String,Float> logProbabilities;
	protected int length;
	
	public Observation() {
		logProbabilities = new TreeMap<String,Float>();
		length = 1;
	}
	
	public Iterator<String> characterIterator() {
		return logProbabilities.keySet().iterator();
	}

	public void add(String character, float logProbability) {
		logProbabilities.put(character, logProbability);
	}
	
	public void add(Observation observation) {
		Iterator<String> itr = characterIterator();
		while (itr.hasNext()) {
			String character = itr.next();
			add(character, logProbability(character) + observation.logProbability(character));
		}
		length += 1;
	}

	public float logProbability(String character) {
		return logProbabilities.get(character);
	}
	
	public int observationLength() {
		return length;
	}

	public Observation copy() {
		Observation observation = new Observation();
		Iterator<String> itr = characterIterator();
		while (itr.hasNext()) {
			String character = itr.next();
			observation.add(character, logProbability(character));
		}
		observation.length = length;
		return observation;
	}
	
}
