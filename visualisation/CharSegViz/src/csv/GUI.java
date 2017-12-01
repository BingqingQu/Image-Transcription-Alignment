/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author Mathias Seuret
 */
public class GUI extends JFrame {
    LineDraggingPanel lineDraggingPanel = new LineDraggingPanel(this);
    
    JButton zoomIn = new JButton("Zoom+");
    JButton zoomOut = new JButton("Zoom-");
    JButton nextImg = new JButton("Next");
    JButton prevImg = new JButton("Previous");
    
    JToggleButton moveLine = new JToggleButton("Move");
    JToggleButton newLine = new JToggleButton("New");
    JToggleButton delLine = new JToggleButton("Delete");
    
    
    TextLine textLine = null;
    
    BufferedImage bi;
    
    String imagePath;
    String segPath;
    
    List<String> imFilenames = new ArrayList<>();
    List<String> sgFilenames = new ArrayList<>();
    int currentFile = 0;
    
    public GUI(String imagePath, String segPath) throws IOException {
        this.setSize(800, 600);
        this.setTitle("Coffee Coffee Coffee Coffee Coffee Coffee Coffee Coffee Coffee");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(lineDraggingPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        this.add(btnPanel, BorderLayout.NORTH);
        btnPanel.add(zoomOut);
        btnPanel.add(zoomIn);
        btnPanel.add(prevImg);
        btnPanel.add(nextImg);
        btnPanel.add(moveLine);
        btnPanel.add(newLine);
        btnPanel.add(delLine);
        
        moveLine.setSelected(true);
        
        moveLine.addActionListener(GUI.this::selectMove);
        delLine.addActionListener(GUI.this::selectDel);
        newLine.addActionListener(GUI.this::selectNew);
        zoomIn.addActionListener(GUI.this::zoomIn);
        zoomOut.addActionListener(GUI.this::zoomOut);
        nextImg.addActionListener(GUI.this::nextData);
        prevImg.addActionListener(GUI.this::prevData);
        

        this.imagePath = imagePath;
        this.segPath = segPath;
        prepareFileLists();
        load(currentFile);
        this.setVisible(true);
    }
    
    public void load(int fileNum) {
        currentFile = fileNum;
        try {
            loadTextLine(sgFilenames.get(currentFile), imFilenames.get(currentFile));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot load a file - reason:\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        lineDraggingPanel.setTextLine(textLine);
    }
    
    public void nextData(ActionEvent evt) {
        save();
        load((currentFile+1) % imFilenames.size());
    }
    
    public void prevData(ActionEvent evt) {
        save();
        load((currentFile+imFilenames.size()-1) % imFilenames.size());
    }
    
    public void save() {
        String newName = sgFilenames.get(currentFile).replace(".llc.txt", "_up.llc.txt");
        try {
            textLine.save(newName);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot save \""+newName+"\" - reason:\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void prepareFileLists() {
        imFilenames = getFolderContent(imagePath, "bin.png");
        sgFilenames = getFolderContent(segPath, ".llc.txt");
        if (imFilenames.size()!=sgFilenames.size()) {
            JOptionPane.showMessageDialog(null, "Warning, there is not the same number of files in \""+imagePath+"\" than in \""+segPath+"\". You can continue, but it is recommended to check everything.", "Huho", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private List<String> getFolderContent(String path, String ext) {
        System.out.println("Listing files in "+path);
        File folder = new File(path);
        String[] arr = folder.list();
        List<String> res = new ArrayList<>(arr.length);
        for (String s : arr) {
            if (s.startsWith(".") || s.endsWith("_up.llc.txt") || !s.endsWith(ext)) {
                continue;
            }
            res.add(path+File.separator+s);
        }
        Collections.sort(res);
        return res;
    }
    
    public void loadTextLine(String transcrPath, String imPath) throws IOException {
        System.out.println("Loading textline("+transcrPath+", "+imPath+")");
        textLine = new TextLine(transcrPath, imPath);
    }
    
    public void selectMove(ActionEvent evt) {
        if (moveLine.isSelected()) {
            delLine.setSelected(false);
            newLine.setSelected(false);
        }
    }
    
    public void selectDel(ActionEvent evt) {
        if (delLine.isSelected()) {
            moveLine.setSelected(false);
            newLine.setSelected(false);
        }
    }
    
    public void selectNew(ActionEvent evt) {
        if (newLine.isSelected()) {
            moveLine.setSelected(false);
            delLine.setSelected(false);
        }
    }
    
    public void zoomIn(ActionEvent evt) {
        lineDraggingPanel.setZoom(lineDraggingPanel.getZoom()*2.0f);
        repaint();
    }
    
    public void zoomOut(ActionEvent evt) {
        lineDraggingPanel.setZoom(lineDraggingPanel.getZoom()/2.0f);
        repaint();
    }
}
