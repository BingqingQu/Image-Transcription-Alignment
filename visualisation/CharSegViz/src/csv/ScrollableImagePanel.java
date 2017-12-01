/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author ms
 */
public abstract class ScrollableImagePanel extends JPanel implements ImagePanelOwner {
    ImagePanel panel = new ImagePanel();
    JScrollBar hScroll = new JScrollBar(JScrollBar.HORIZONTAL);
    JScrollBar vScroll = new JScrollBar(JScrollBar.VERTICAL);
    
    protected int previousMouseX;
    protected int previousMouseY;
    
    public ScrollableImagePanel() {
        panel.owner = this;
        vScroll.addAdjustmentListener(ae -> {
            panel.setOffset(hScroll.getValue(), vScroll.getValue());
            repaint();
        });
        hScroll.addAdjustmentListener(ae -> {
            panel.setOffset(hScroll.getValue(), vScroll.getValue());
            repaint();
        });
        panel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                try {
                    mouseDraggedEvent(me);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                previousMouseX = me.getX();
                previousMouseY = me.getY();
            }
        });
        
        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClickedEvent(e);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedEvent(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseReleasedEvent(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(hScroll, BorderLayout.SOUTH);
        this.add(vScroll, BorderLayout.EAST);
    }
    
    public void updateScrollBars() {
        hScroll.setMaximum((panel.getImage().getWidth()));
        hScroll.setVisibleAmount((int) (panel.getWidth() / panel.zoom));

        vScroll.setMaximum((panel.getImage().getHeight()));
        vScroll.setVisibleAmount((int) (panel.getHeight() / panel.zoom));
    }
    
    public int getOffsetX() {
        return panel.getOffsetX();
    }

    /**
     * @return the vertical offset
     */
    public int getOffsetY() {
        return panel.getOffsetY();
    }
    
    /**
     * Sets the offset of the panel
     *
     * @param ox
     * @param oy
     */
    public void setOffset(int ox, int oy) {
       panel.setOffset(ox,oy);
    }

    /**
     * Sets the zoom of the panel, takes a non-null
     * integer as parameter
     *
     * @param zoom
     */
    public void setZoom(double zoom, int centerX, int centerY) {
        panel.setZoom(zoom, centerX, centerY);
        updateScrollBars();
    }

    /**
     * @return the zoom value
     */
    public double getZoom() {
        return panel.getZoom();
    }

    /**
     * Sets the zoom of the panel, takes a non-null
     * integer as parameter
     *
     * @param zoom
     */
    public void setZoom(double zoom) {
        panel.setZoom(zoom);
        updateScrollBars();
    }

    /**
     * Turns a relative and zoomed coordinate into absolute
     * coordinate
     *
     * @param x
     * @return the real X coordinate
     */
    public float getRealX(int x) {
        return panel.getRealX(x);
    }

    /**
     * Turns a relative and zoomed coordinate into absolute
     * coordinate
     *
     * @param y
     * @return the real Y coordinated
     */
    public float getRealY(int y) {
        return panel.getRealY(y);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param x
     * @return the pixel X coordinate
     */
    public int getPanelX(int x) {
        return panel.getPanelX(x);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param y
     * @return the pixel Y coordinate
     */
    public int getPanelY(int y) {
        return panel.getPanelY(y);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return panel.getImage();
    }

    /**
     * Sets the image of the panel
     *
     * @param i
     */
    public void setImage(BufferedImage i) {
        panel.setImage(i);
        updateScrollBars();
    }
    
    public abstract void reactToMouseEvent(MouseEvent me);
    public abstract void mouseClickedEvent(MouseEvent me);
    public abstract void mouseDraggedEvent(MouseEvent me);
    public abstract void mouseReleasedEvent(MouseEvent me);
    public abstract void mousePressedEvent(MouseEvent me);
}
