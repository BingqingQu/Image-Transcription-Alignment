package andreas;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ObservationSequence {

	public static final String EPS = "EPS";
	protected ArrayList<Observation> observations;
	
	public static float log(float f, String logBase) {
		if (logBase.compareTo("logNat") == 0) {
			return (float) Math.log(f);
		}
		return (float) Math.log10(f);
	}
	
	public ObservationSequence() {
		observations = new ArrayList<Observation>();
	}

	public int size() {
		return observations.size();
	}

	public float logProbability(int time, String character) {
		return observations.get(time).logProbability(character);
	}
	
	public int observationLength(int time) {
		return observations.get(time).observationLength();
	}

	public ObservationSequence compress(float compression, String logBase) {
		float minLogProbability = ObservationSequence.log(compression, logBase);
		ObservationSequence observationSequence = new ObservationSequence();
		Observation currentObservation = null;
		Observation lastObservation = null;
		boolean compressing = false;
		for (int i=0; i < size(); i++) {
			currentObservation = observations.get(i).copy();
			if (currentObservation.logProbability(EPS) >= minLogProbability) {
				if (compressing) {
					lastObservation.add(currentObservation);
				} else {
					observationSequence.add(currentObservation);
					lastObservation = currentObservation;
					compressing = true;
				}
			} else {
				observationSequence.add(currentObservation);
				lastObservation = currentObservation;
				compressing = false;
			}
		}
		return observationSequence;
	}
	
	public int uncompressedPosition(int position) {
		int result = 0;
		for (int i=0; i <= position; i++) {
			result += observations.get(i).length;
		}
		return result-1;
	}

	@Override
	public String toString() {
		String str = "*ObservationProbability*\n";
		Iterator<String> iter = characterIterator();
		while (iter.hasNext()) {
			String character = iter.next();
			for (int time=0; time < size(); time++) {
				if (time == 0) {
					str += String.format("%3s ", character);
				}
				str += String.format("%6.02f", logProbability(time, character));
				if (observationLength(time) > 1) {
					str += " (" + observationLength(time) + ") ";
				}
				if (time < size()-1) {
					str += " ";
				} else {
					str += "\n";
				}
			}
		}
		return str;
	}

	public static ObservationSequence read(String file, String logBase) {
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			ArrayList<String> characterList = new ArrayList<String>();
			String line = reader.readLine();
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			while(st.hasMoreElements()) {
				characterList.add(st.nextToken());
			}
			characterList.add(EPS);
			
			ObservationSequence observationSequence = new ObservationSequence();
			int idxCharacter = 0;
			while ((line = reader.readLine()) != null) {
				st = new StringTokenizer(line);
				int time = 0;
				while(st.hasMoreElements()) {
					if (idxCharacter == 0) {
						observationSequence.add(new Observation());
					}
					float logProbability = ObservationSequence.log(Float.valueOf(st.nextToken()).floatValue(), logBase);
					observationSequence.add(time, characterList.get(idxCharacter), logProbability);
					time += 1;
				}
				idxCharacter += 1;
			}
			return observationSequence;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Iterator<String> characterIterator() {
		return observations.get(0).characterIterator();
	}
	
	protected void add(Observation observation) {
		observations.add(observation);
	}
	
	protected void add(int time, String character, float logProbability) {
		observations.get(time).add(character, logProbability);
	}

}

// example observation file
// # A B [...] z
// 9.02961e-06 2.20362e-08 [...] 1.88909e-13
// [...]
// 0.997403 0.999919 [...] 1.45824e-14