package andreas;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Alphabet {

	protected TreeSet<String> characters;
	
	public Alphabet() {
		characters = new TreeSet<String>();
	}
	
	public int numCharacters() {
		return characters.size();
	}
	
	public boolean contains(String character) {
		return characters.contains(character);
	}
	
	@Override
	public String toString() {
		String str = "*Alphabet*\n";
		Iterator<String> itr = characters.iterator();
		while (itr.hasNext()) {
			str += itr.next() + " ";
		}
		return str.trim() + "\n";
	}
	
	public static Alphabet read(String file) {
		try {
			Alphabet alphabet = new Alphabet();
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			while(st.hasMoreElements()) {
				alphabet.characters.add(st.nextToken());
			}
			alphabet.characters.add(ObservationSequence.EPS);
			return alphabet;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
