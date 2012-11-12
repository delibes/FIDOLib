/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.ui;

import fidolib.data.AISData;
import fidolib.data.Constants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;

import java.util.Timer;
import java.util.TimerTask;

public class MapPanel extends JPanel {

    private static boolean HEAVYWEIGHT_LIGHTWEIGHT_MIXING = false;
     /**
     * Reference to the AIS data
     * 
     */
    private AISData aAISData = null;
    
    /**
     * The panel containing the image
     */
    private ImagePanel imgPanel;
    /**
     * Timer delay
     */
    private int delay = 0;
    /**
     * Timer period for GUI updates
     */
    private int period = Constants.deltaT_GUI;
    /**
     * The timer it self
     */
    private Timer timer = new Timer();
    /**
     * Drag the map or measure distance
     */
    private boolean dragMode = true;

    public MapPanel(AISData aAISData, String imageStr) {
        super(new BorderLayout());
        this.aAISData = aAISData;
        imgPanel = new ImagePanel(aAISData,imageStr);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        JViewport vport = new JViewport() {

            private boolean flag = false;

            @Override
            public void revalidate() {
                if (!HEAVYWEIGHT_LIGHTWEIGHT_MIXING && flag) {
                    return;
                }
                super.revalidate();
            }

            @Override
            public void setViewPosition(Point p) {
                if (HEAVYWEIGHT_LIGHTWEIGHT_MIXING) {
                    super.setViewPosition(p);
                } else {
                    flag = true;
                    super.setViewPosition(p);
                    flag = false;
                }
            }
        };
        vport.add(imgPanel);

        MouseAdapter hsl1 = new HandScrollListener();
        vport.addMouseMotionListener(hsl1);
        vport.addMouseListener(hsl1);

        add(vport);

        // Set and start the timer
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                repaint();
            }
        }, delay, period);
    }
    public void loadImage(String imageStr)
    {
        this.imgPanel.loadImage(imageStr);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {

        java.net.URL imgURL = MapPanel.class.getResource(path);
        System.out.println(imgURL);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    class HandScrollListener extends MouseAdapter {

        private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        private final Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        private final Cursor crossHairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        private final Point pp = new Point();

        public HandScrollListener() {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragMode == true) {
                ((JComponent) e.getSource()).setCursor(moveCursor);
                imgPanel.setIsDragging(true);
                JViewport vport = (JViewport) e.getSource();
                Point cp = e.getPoint();
                Point vp = vport.getViewPosition();
                vp.translate(pp.x - cp.x, pp.y - cp.y);
                Rectangle vpRect = new Rectangle(vp, vport.getSize());
                imgPanel.scrollRectToVisible(vpRect);
                pp.setLocation(cp);
            } else {
                JViewport vport = (JViewport) e.getSource();
                Point cp = e.getPoint();
                imgPanel.drawLine(pp, cp);
                 imgPanel.mouseMoved(e.getX(), e.getY());

            }


        }

        @Override
        public void mouseMoved(MouseEvent e) {
            imgPanel.mouseMoved(e.getX(), e.getY());


        }

        @Override
        public void mousePressed(MouseEvent e) {

            imgPanel.drawLine(null, null);
            pp.setLocation(e.getPoint());
            ((JComponent) e.getSource()).setCursor((dragMode == true) ? this.moveCursor : crossHairCursor);




        }

        @Override
        public void mouseReleased(MouseEvent e) {
            imgPanel.setIsDragging(false);

            ((JComponent) e.getSource()).setCursor((dragMode == true) ? this.defCursor : crossHairCursor);

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            imgPanel.setMouseOver(true);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (e.getClickCount() == 1) {
                    imgPanel.drawLine(null, null);
                }
                if (e.getClickCount() >= 2) {
                    dragMode = (dragMode == true) ? false : true;
                    ((JComponent) e.getSource()).setCursor((dragMode == true) ? this.defCursor : crossHairCursor);
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            imgPanel.setMouseOver(false);
            imgPanel.drawLine(null, null);
            
        }
    }
}
