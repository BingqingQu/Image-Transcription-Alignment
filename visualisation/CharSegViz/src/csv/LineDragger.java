/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ms
 */
public class LineDragger {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length!=2) {
            System.err.println("Syntax: java -jar CharSegViz.jar image-data-folder segmentation-data-folder");
            System.exit(1);
        }
        new GUI(args[0], args[1]);
    }
    
}
