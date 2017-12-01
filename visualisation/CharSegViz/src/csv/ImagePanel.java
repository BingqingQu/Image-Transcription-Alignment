/**********************************************************************************************************************
 * Copyright HisDoc 2.0 Project                                                                                       *
 *                                                                                                                    *
 * Copyright (c) University of Fribourg, 2015                                                                         *
 *                                                                                                                    *
 * @author: Angelika Garz                                                                                             *
 *          angelika.garz@unifr.ch                                                                                    *
 *          http://diuf.unifr.ch/main/diva/home/people/angelika-garz                                                  *
 **********************************************************************************************************************/

package csv;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
/**
 * @author Mathias Seuret
 */
public class ImagePanel extends JPanel {
    double zoom = 1;
    private BufferedImage image = null;
    private int offsetX = 0;
    private int offsetY = 0;
    public ImagePanelOwner owner = null;
    
    public ImagePanel() {
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * @return the horizontal offset
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * @return the vertical offset
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Displays the panel
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getImage() == null) {
            return;
        }
        //System.out.println("<"+getWidth()+"><"+g.getClip().getBounds().width+">");
        //g.drawImage(image, 0, 0, getWidth(), getHeight(), offsetX, offsetY, offsetX+getWidth()/zoom, offsetY+getHeight()/zoom, Color.black, null);
        paintToGraphics(g, true);
    }

    /**
     * zoom bug fixed
     *
     * @param g
     * @param useZoomAndOffset
     */
    public void paintToGraphics(Graphics g, boolean useZoomAndOffset) {
        if (useZoomAndOffset) {
            g.drawImage(getImage(),
                        0, 0,
                        g.getClip().getBounds().width, g.getClip().getBounds().height,
                        offsetX, offsetY,
                        (int)(offsetX+g.getClip().getBounds().width/zoom), (int)(offsetY+g.getClip().getBounds().height/zoom),
                        Color.black, null);
        } else {
            g.drawImage(getImage(),
                        0, 0,
                        g.getClip().getBounds().width, g.getClip().getBounds().height,
                        0, 0,
                        getImage().getWidth(), getImage().getHeight(),
                        Color.black, null);
        }
        
        if (owner!=null) {
            owner.paintStuff(g);
        }
    }
    
    public void paintStuff(Graphics g) {
        // Nothing to do
    }

    /**
     * Sets the offset of the panel
     *
     * @param ox
     * @param oy
     */
    public void setOffset(int ox, int oy) {
       // System.out.println("Setting offset: " + ox + "," + oy);
        offsetX = ox;
        offsetY = oy;
    }

    /**
     * Sets the zoom of the panel, takes a non-null
     * integer as parameter
     *
     * @param zoom
     */
    public void setZoom(double zoom, int centerX, int centerY) {
        if (zoom <= 0) {
            return;
        }
        this.zoom = zoom;
        setOffset(
                centerX,
                centerY
                 );
    }

    /**
     * @return the zoom value
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Sets the zoom of the panel, takes a non-null
     * integer as parameter
     *
     * @param zoom
     */
    public void setZoom(double zoom) {
        if (zoom <= 0) {
            return;
        }
        //int centerX = offsetX + (int) (getWidth() / this.zoom / 2);
        //int centerY = offsetY + (int) (getHeight() / this.zoom / 2);
        this.zoom = zoom;
        //setOffset(
        //        centerX - (int) (getWidth() / this.zoom / 2),
        //        centerY - (int) (getHeight() / this.zoom / 2)
        //         );
    }

    /**
     * Turns a relative and zoomed coordinate into absolute
     * coordinate
     *
     * @param x
     * @return the real X coordinate
     */
    public float getRealX(int x) {
        return (float)(x / zoom + offsetX);
    }

    /**
     * Turns a relative and zoomed coordinate into absolute
     * coordinate
     *
     * @param y
     * @return the real Y coordinated
     */
    public float getRealY(int y) {
        return (float) (y / zoom + offsetY);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param x
     * @return the pixel X coordinate
     */
    public int getPanelX(int x) {
        return (int) ((x - offsetX) * zoom);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param y
     * @return the pixel Y coordinate
     */
    public int getPanelY(int y) {
        return (int) ((y - offsetY) * zoom);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image of the panel
     *
     * @param i
     */
    public void setImage(BufferedImage i) {
        image = i;
    }

}