/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

/**
 *
 * @author Mathias Seuret
 */
public class Character {
    public final String text;
    public int start;
    public int end;
    public Character(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    void swapSides() {
        int s = start;
        start = end;
        end = s;
    }
}
