package andreas;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MainParams {

	protected ArrayList<String> ids;
	protected ArrayList<String> labels;
	protected HashMap<String,String> params;
	
	public MainParams() {
		ids = new ArrayList<String>();
		labels = new ArrayList<String>();
		params = new HashMap<String,String>();
		params.put("gsf", "1");
		params.put("wip", "0");
		params.put("obsLogBase", "logNat");
		params.put("compression", "0.999");
		params.put("nodeBeam", null);
		params.put("wordBeam", null);
		params.put("nodeNbest", "5000");
		params.put("wordNbest", "10");
		params.put("sentenceNbest", "1");
		params.put("fileSpelling", null);
		params.put("fileLanguageModel", null);
		params.put("dirObservation", null);
		params.put("dirRecognition", null);
		params.put("postfixObservation", null);
		params.put("postfixRecognition", null);
	}
	
	public int numIds() {
		return ids.size();
	}
	
	public String getId(int idx) {
		return ids.get(idx);
	}
	
	public int numLabels() {
		return labels.size();
	}
	
	public String getLabel(int idx) {
		return labels.get(idx);
	}
	
	public String get(String key) {
		return params.get(key);
	}
	
	public float getFloat(String key) {
		return Float.parseFloat(params.get(key));
	}
	
	public int getInt(String key) {
		return Integer.parseInt(params.get(key));
	}

	public String fileObservation(int idx) {
		if (get("postfixObservation") == null) {
			return get("dirObservation") + ids.get(idx);
		}
		return get("dirObservation") + ids.get(idx) + "." + get("postfixObservation");
	}
	
	public String fileRecognition(int idx) {
		if (get("postfixRecognition") == null) {
			return get("dirRecognition") + ids.get(idx);
		}
		return get("dirRecognition") + ids.get(idx) + "." + get("postfixRecognition");
	}
	
	public String fileRecognitionLattice(int idx) {
		if (get("postfixRecognition") == null) {
			return get("dirRecognition") + ids.get(idx) + ".lat";
		}
		return get("dirRecognition") + ids.get(idx) + "." + get("postfixRecognition") + ".lat";
	}
	
	public void readFileIds(String file) {
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				ids.add(line.trim());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readFileLabels(String file) {
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				labels.add(line.trim());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readEnv() {
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String env = System.getenv(key);
			if (env != null) {
				params.put(key, env);
			}
		}
	}
	
	public void readFileParams(String file) {
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if (st.countTokens() != 2) {
					reader.close();
					throw new Exception("ERROR: wrong parameter file format (expected: paramName paramValue)");
				}
				String paramName = st.nextToken();
				String paramValue = st.nextToken();
				if (params.keySet().contains(paramName)) {
					params.put(paramName, paramValue);
				} else {
					reader.close();
					throw new Exception("ERROR: unknown parameter name: " + paramName);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
