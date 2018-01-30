/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ms
 */
public class LineDraggingPanel extends ScrollableImagePanel {

    

    public enum Side {
        START,
        END;
        
        public static Side fromButton(int mouseButton) {
            if (mouseButton==1) {
                return START;
            } else {
                return END;
            }
        }
    }
    public Side side = Side.START;
    
    public TextLine textLine = null;

    Character closest = null;
    
    GUI gui = null;
    
    public LineDraggingPanel(GUI gui) {
        this.gui = gui;    
    }
    
    @Override
    public void mouseDraggedEvent(MouseEvent me) {
        if (closest==null) {
            return;
        }
        
        if (gui.moveLine.isSelected()) {
            float x = panel.getRealX(me.getX());
            Character closest = closestCharacter(x, side);
            System.out.println(closest);
            if (closest==null) {
                return;
            }
            System.out.println(closest.text);
            
            switch (side) {
                case START:
                    closest.start = (int)x;
                    if (closest.start>closest.end) {
                        side = Side.END;
                        closest.swapSides();
                    }
                    break;
                case END:
                    closest.end = (int)x;
                    if (closest.end<closest.start) {
                        side = Side.START;
                        closest.swapSides();
                    }
                    break;
            }
            
            repaint();
        }
    }
    
    @Override
    public void mousePressedEvent(MouseEvent me) {
        side = Side.fromButton(me.getButton());
        System.out.println("Caring about: "+side);
        float x = panel.getRealX(me.getX());
        closest = closestCharacter(x, side);
    }
    
    @Override
    public void reactToMouseEvent(MouseEvent me) {
        float x = panel.getRealX(me.getX());
        
        
    }

    @Override
    public void paintStuff(Graphics g) {
        if (textLine==null) {
            return;
        }
        
        g.setColor(Color.blue);
        for (Character c : textLine.characters) {
            int x = (int)this.getPanelX(c.start);
            int y = (int)this.getPanelY(panel.getImage().getHeight());
            g.setColor(Color.BLUE);
            g.drawLine(x, 0, x, y);
            
            x = (int)this.getPanelX(c.end);
            g.setColor(Color.RED);
            g.drawLine(x, 0, x, y);
            
            
            if (c.text==null || c.text.length()==0) {
                continue;
            }
            int width = (int)Math.round((c.end - c.start) * getZoom());
            int fontSize = 1;
            FontMetrics fm = null;
            do {
                g.setFont(new Font( "SansSerif", Font.BOLD, ++fontSize));
            } while (g.getFontMetrics().stringWidth(c.text)<width && g.getFontMetrics().getHeight()<width);
            g.setFont(new Font( "SansSerif", Font.BOLD, --fontSize));
            
            int txtWidth = g.getFontMetrics().stringWidth(c.text);
            
            int cx = (int)((c.start + c.end) / 2 * getZoom() - txtWidth/2 - getOffsetX()*getZoom());
            
            g.drawString(
                    c.text,
                    cx,
                    getPanelY(textLine.image.getHeight())+g.getFontMetrics().getHeight() - (int)(this.getOffsetY() * getZoom())
            );
        }
        
    }

    @Override
    public void mouseReleasedEvent(MouseEvent me) {
//        if (gui!=null) {
//            try {
//                gui.saveCurrentData();
//            } catch (IOException ex) {
//                Logger.getLogger(LineDraggingPanel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    void setTextLine(TextLine textLine) {
        setImage(textLine.image);
        this.textLine = textLine;
    }
    
    private Character closestCharacter(float x, Side side) {
        float d = Float.POSITIVE_INFINITY;
        Character r = null;
        for (Character c : textLine.characters) {
            int cx = side==Side.START ? c.start : c.end;
            float dd = (cx-x) * (cx-x);
            if (dd<d) {
                d = dd;
                r = c;
            }
        }
        return r;
    }
    
    @Override
    public void mouseClickedEvent(MouseEvent me) {}
    
}
