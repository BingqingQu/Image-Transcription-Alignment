/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Mathias Seuret
 */
public class TextLine {
    public final List<Character> characters = new LinkedList<>();
    public final BufferedImage image;
    
    public TextLine(String filename, String imname) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            for (String txt=reader.readLine(); txt!=null && txt.length()>2; txt=reader.readLine()) {
                String[] parts = txt.split(" ");
                int s = Integer.parseInt(parts[0]);
                int e = Integer.parseInt(parts[1]);
                String c = parts[2];
                characters.add(new Character(c, s, e));
            }
        }
        image = ImageIO.read(new File(imname));
    }
    
    public void save(String filename) throws IOException {
        boolean isFirst = true;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Character c : characters) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    writer.write('\n');
                }
                writer.write(c.start+" "+c.end+" "+c.text);
            }
        }
    }
}
