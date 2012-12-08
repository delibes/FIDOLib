/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.ui;

import fidolib.data.Constants;
import fidolib.data.FlightData;
import fidolib.data.AISData;
import fidolib.data.RocketInfo;
import fidolib.data.VesselInfo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/** 
 * This class is the Image Panel where the image 
 * is drawn and scaled. 
 *  
 * @author Steen
 */
public class ImagePanel extends JPanel {

    private double m_zoom = 1.0;
   
    private Image m_image;
    /**
     * Reference to the AIS data
     * 
     */
    private AISData aAISData = null;
    /**
     * Delta degrees between rad and degree
     */
    private int deltaDegree = -90;
    /**
     * Number of points in the polygon showing the direction
     */
    private int nPolygon = 4;
    /**
     * X points in the polygon
     */
    private int[] xPoints = new int[nPolygon];
    /**
     * Y points in the polygon
     */
    private int[] yPoints = new int[nPolygon];
    /**
     * The fraction of the array width compared to the length of the array
     */
    private int arrayWidthFraction = 20;
    /**
     * Smaragd icon size
     */
    private int smaragdIconSize = 20;
    /**
     * Other vessels icon size
     */
    private int otherVesselsIconSize = 16;
    /**
     * The latitude for the mouse pointer when on the map
     */
    private double mouseLat = 0;
    /**
     * The longitude for the mouse pointer when on the map
     */
    private double mouseLon = 0;
    /**
     * The pixel pox x for the mouse pointer when on the map
     */
    private int mouseX = 0;
    /**
     * The pixel pos y for the mouse pointer when on the map
     */
    private int mouseY = 0;
    /**
     * Are we dragging the the panel
     */
    private boolean isDragging = false;
    /**
     * Are we dragging the the panel
     */
    private boolean isMouseOver = false;
    /**
     * Distance line start 
     */
    private Point p1;
    /**
     * Distance line end 
     */
    private Point p2;

    /** 
     * Constructor 
     *  
     * @param image 
     * @param zoomPercentage 
     */
    public ImagePanel(AISData aAISData, String imageStr) {
        m_image = createImageIcon(imageStr).getImage();
        this.aAISData = aAISData;
    }

    public void loadImage(String imageStr)
    {
        m_image = createImageIcon(imageStr).getImage();
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {

        java.net.URL imgURL = ImagePanel.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    /** 
     * This method is overriden to draw the image 
     * and scale the graphics accordingly 
     */
    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;

        //set the background color to white 
        g2D.setColor(Color.WHITE);
        //fill the rect 
        g2D.fillRect(0, 0, getWidth(), getHeight());

        //draw the image 
        g2D.drawImage(m_image, 0, 0, this);
        // Remove obsolete vessels
        aAISData.removeObsoleteVesselInfo();
        if (Constants.showESD139 == true) {
           paintESD139(g);
        }

        paintVessels(g);
        if (isMouseOver == true) {
            paintMousePos(g);
            paintVesselInfo(g);
        }

        paintRocket(g);
        paintLine(g);

    }

    public void paintESD139(Graphics g) {
        g.setColor(Constants.ESD139Color);
        Graphics2D g2D = (Graphics2D) g;

        g2D.setStroke(new BasicStroke(4F));  // set stroke width of 10


        RocketInfo.calcLatLonPixels(Constants.E139NorthWest, this.getWidth(), this.getHeight());
        RocketInfo.calcLatLonPixels(Constants.E139NorthEast, this.getWidth(), this.getHeight());
        RocketInfo.calcLatLonPixels(Constants.E139SouthWest, this.getWidth(), this.getHeight());
        RocketInfo.calcLatLonPixels(Constants.E139SouthEast, this.getWidth(), this.getHeight());

        int[] xValues = {Constants.E139NorthWest.lonPixels,
            Constants.E139NorthEast.lonPixels,
            Constants.E139SouthEast.lonPixels,
            Constants.E139SouthWest.lonPixels};
        int[] yValues = {Constants.E139NorthWest.latPixels,
            Constants.E139NorthEast.latPixels,
            Constants.E139SouthEast.latPixels,
            Constants.E139SouthWest.latPixels,};

        Polygon p = new Polygon(xValues, yValues, xValues.length);
        g.drawPolygon(p);
    }

    public void paintLine(Graphics g) {

        Point p1a = this.p1;
        Point p2a = this.p2;

        if (p1a != null && p2a != null) {
            g.setColor(Constants.backGroundColor);
            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(new BasicStroke(3));
            g.drawLine(p1a.x + this.getVisibleRect().x, p1a.y + this.getVisibleRect().y,
                    p2a.x + this.getVisibleRect().x, p2a.y + this.getVisibleRect().y);
            int xDist = Math.abs(p1a.x - p2a.x);
            int yDist = Math.abs(p1a.y - p2a.y);
            int largestDist = (int) Math.sqrt(xDist * xDist + yDist * yDist);
            g.drawOval(p1a.x - (largestDist ) + this.getVisibleRect().x, p1a.y - (largestDist ) + this.getVisibleRect().y, largestDist * 2, largestDist * 2);

            RocketInfo pos1 = new RocketInfo();
            RocketInfo pos2 = new RocketInfo();
            pos1.lat = pixelToLatitude(p1a.x);
            pos1.lon = pixelToLongitude(p1a.y);
            pos2.lat = pixelToLatitude(p2a.x);
            pos2.lon = pixelToLongitude(p2a.y);



            double disanceNauticalMiles = RocketInfo.disanceNauticalMiles(pos1, pos2);
            double distanceMeters = disanceNauticalMiles * Constants.nauticalMile;
            int initialBearing = RocketInfo.initialBearing(pos1, pos2);
            String distStr = "";
            DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
            decimalSymbols.setDecimalSeparator('.');
            decimalSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
            distStr = " " + df.format(disanceNauticalMiles) + " Nm  (" + df.format(distanceMeters/1000) + " km)  "    
                    + initialBearing + Constants.degreeChar + " ";
            Font font = new Font("New Courier", Font.BOLD, (Constants.knotsLabelSize));
            g.setFont(font);
            int widthStr = g.getFontMetrics().stringWidth(distStr);
            g.setColor(Constants.blueish.darker());
            g.fill3DRect(p2a.x + this.getVisibleRect().x - (widthStr / 2), p2a.y + this.getVisibleRect().y - ((int)(Constants.knotsLabelSize * 2.4)), widthStr + 10, (int) (Constants.knotsLabelSize * 2), true);
            g.setColor(Constants.textColor);
            g.drawString(distStr, p2a.x + this.getVisibleRect().x - (widthStr / 2) + Constants.knotsLabelSize / 2, p2a.y + this.getVisibleRect().y - Constants.knotsLabelSize);




        }
    }

    public void paintMousePos(Graphics g) {
        if (this.isDragging == false) {
            if ((mouseLat != 0.0) && (mouseLon != 0.0)) {
                Font font = new Font("New Courier", Font.BOLD, Constants.knotsLabelSize);
                g.setFont(font);
                g.setColor(Constants.mousePositionColor);
                
                int xPos = this.getVisibleRect().x + Constants.knotsLabelSize + 25;
                int yPos = this.getVisibleRect().y + Constants.knotsLabelSize + 30;

                int xSpace = Constants.knotsLabelSize * 4;
                int ySpace = (int) (((double) Constants.knotsLabelSize) * 1.4);
                String latDegree = Constants.northSouth + " " + RocketInfo.formatDegrees(mouseLat);
                String lonDegree = Constants.eastWest + " " + RocketInfo.formatDegrees(mouseLon);
                int width = (int) (Constants.knotsLabelSize * 14);
                int height = 0;
                height = (int) (ySpace * 3.5);
                g.fill3DRect(xPos - Constants.knotsLabelSize, yPos - (int) (((double) Constants.knotsLabelSize) * 1.5), width, height, true);


               
            
                g.setColor(Constants.textColor);
                g.drawString(Constants.latStr, xPos, yPos);
                g.drawString(Constants.lonStr, xPos, yPos + ySpace);
                g.drawString("AIS dT", xPos, yPos + ySpace * 2);
                g.drawString("" + latDegree, xPos + xSpace, yPos);
                g.drawString("" + lonDegree, xPos + xSpace, yPos + ySpace);
                g.drawString("" + aAISData.getAISDeltaT(), xPos + xSpace, yPos + ySpace * 2);
                


            }

        }



    }

    public void paintVesselInfo(Graphics g) {
        if (this.isDragging == false) {
            if ((mouseLat != 0.0) && (mouseLon != 0.0)) {
                Font font = new Font("New Courier", Font.BOLD, Constants.knotsLabelSize);
                g.setFont(font);
                g.setColor(Color.BLACK);
                String MMSI = aAISData.getMMSI(mouseX, mouseY, this.getWidth(), this.getHeight());

                int xPos = this.getVisibleRect().x + Constants.knotsLabelSize + 25;
                int yPos = this.getVisibleRect().y + Constants.knotsLabelSize + 110;

                int xSpace = Constants.knotsLabelSize * 4;
                int ySpace = (int) (((double) Constants.knotsLabelSize) * 1.4);
//                String latDegree = RocketInfo.formatDegrees(mouseLat);
//                String lonDegree = RocketInfo.formatDegrees(mouseLon);
               
                int width = (int) (Constants.knotsLabelSize * 14);
                int height = 0;
                if (!MMSI.equals("")) // The pointer is at a vessel
                {
                    height = ySpace * (aAISData.getVessel(MMSI).getVesselInfo().length + 1) - 8;
                    g.fill3DRect(xPos - Constants.knotsLabelSize, yPos - (int) (((double) Constants.knotsLabelSize) * 1.5), width, height, true);
                }

                g.setColor(Constants.textColor);

                if (!MMSI.equals("")) {
                    String[][] info = aAISData.getVessel(MMSI).getVesselInfo();
                    for (int i = 0; i < info.length; i++) {
                        g.drawString(info[i][0], xPos, yPos + ySpace * (i));
                        g.drawString(info[i][1], xPos + xSpace, yPos + ySpace * (i));

                    }
                }

            }

        }



    }

    public void paintVessels(Graphics g) {

        LinkedList<VesselInfo> allVessels = aAISData.getAllVessels();
        synchronized (allVessels) {
            Iterator iterator = allVessels.iterator();
            // reference to sputnik (to be painted after "other" vessels
            VesselInfo sputnik = null;
            // reference to hjortoe (to be painted after "other" vessels
            VesselInfo hjortoe = null;
            // selected vessel
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if ((aVesselInfo != null)) {
                    if (aVesselInfo.MMSI.equals(aAISData.sputnikMMSI)) {
                        sputnik = aVesselInfo;
                    } else if (aVesselInfo.MMSI.equals(aAISData.mcMMSI)) {
                        hjortoe = aVesselInfo;
                    } else {
                        paintSingleVessel(g, aVesselInfo, Constants.otherVesselsColor);
                    }
                }
            }


            if (hjortoe != null) {
                paintSingleVessel(g, hjortoe, Constants.hjortoeColor);
            }
            if (sputnik != null) {
                paintSingleVessel(g, sputnik, Constants.sputnikColor);
            }


        }



    }

    public void paintSingleVessel(Graphics g, VesselInfo vessel, Color vesselColor) {
        if (vessel == null) {
            return;
        }
        RocketInfo.calcLatLonPixels(vessel.pos, this.getWidth(), this.getHeight());

        if (vessel.baseStation == true) {
            g.setColor(Constants.baseStationColor);
            int baseStationWidth = 10;
            g.fillRect(vessel.pos.lonPixels - (baseStationWidth / 2), vessel.pos.latPixels - (baseStationWidth / 2), baseStationWidth, baseStationWidth);
            return;
        }
        double headCOG = 0.0;



        if (vessel.true_heading == Constants.trueHeadingNA
                && (vessel.sog == 0.0)) { // Draw oval when no true heading

            g.setColor(Constants.otherVesselsNoMovementColor);

            if (vessel.isSelected == true) {
                g.setColor(Color.DARK_GRAY);
                Graphics2D g2 = (Graphics2D) g;

                g2.setStroke(new BasicStroke(4));
                g.fillOval(vessel.pos.lonPixels - (otherVesselsIconSize / 2), vessel.pos.latPixels - (otherVesselsIconSize / 2), otherVesselsIconSize, otherVesselsIconSize);

                g2.setStroke(new BasicStroke(1));    // Draw knots  
            } else {
                g.fillOval(vessel.pos.lonPixels - (otherVesselsIconSize / 2), vessel.pos.latPixels - (otherVesselsIconSize / 2), otherVesselsIconSize, otherVesselsIconSize);

            }
            Font font = new Font("New Courier", Font.BOLD, Constants.knotsLabelSize);
            g.setFont(font);
            g.setColor(Constants.knotsLabelColor);
            return;
        }

        int ovalWidth = 140;
        int ovalHeight = 100;
        int offSetY = 0;

        double direction = vessel.true_heading;
        if (vessel.true_heading == Constants.trueHeadingNA) {
            direction = vessel.cog;
        }
        double arrowSpike = (direction + deltaDegree) * Math.PI / 180;
        double arrowRight = (((direction + deltaDegree) + 270) % 360) * Math.PI / 180;
        double arrowLeft = (((direction + deltaDegree) + 90) % 360) * Math.PI / 180;
        double arrowBack = (((direction + deltaDegree) + 180) % 360) * Math.PI / 180;

        xPoints[0] = (int) (Math.cos(arrowSpike) * ovalWidth / 2.3) + vessel.pos.lonPixels;
        yPoints[0] = (int) (Math.sin(arrowSpike) * ovalHeight / 2.3) + vessel.pos.latPixels + offSetY;


        xPoints[1] = (int) (Math.cos(arrowRight) * ovalWidth / arrayWidthFraction) + vessel.pos.lonPixels;
        yPoints[1] = (int) (Math.sin(arrowRight) * ovalHeight / arrayWidthFraction) + vessel.pos.latPixels + offSetY;

        xPoints[2] = (int) (Math.cos(arrowBack) * ovalWidth / 6) + vessel.pos.lonPixels;
        yPoints[2] = (int) (Math.sin(arrowBack) * ovalHeight / 6) + vessel.pos.latPixels + offSetY;


        xPoints[3] = (int) (Math.cos(arrowLeft) * ovalWidth / arrayWidthFraction) + vessel.pos.lonPixels;
        yPoints[3] = (int) (Math.sin(arrowLeft) * ovalHeight / arrayWidthFraction) + vessel.pos.latPixels + offSetY;


        // Draw vessel
        g.setColor(vesselColor);
        g.fillPolygon(xPoints, yPoints, nPolygon);
        if (vessel.isSelected == true) {
            g.setColor(Color.DARK_GRAY);
            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(new BasicStroke(4));
            g.drawPolygon(xPoints, yPoints, nPolygon);
            g2.setStroke(new BasicStroke(1));
        }
        if ((vessel.sog != 0.0) && (!vessel.isSelected)) {
            Font font = new Font("New Courier", Font.BOLD, Constants.knotsLabelSize);
            g.setFont(font);
            g.setColor(Constants.knotsLabelColor);
            String sogStr = vessel.getSOG(false);
            int sLength = g.getFontMetrics().stringWidth(sogStr);

            g.drawString(sogStr, vessel.pos.lonPixels - (sLength / 2), vessel.pos.latPixels + (otherVesselsIconSize / 3) + offSetY);

        }

    }

    public void paintRocket(Graphics g) {

        RocketInfo.calcLatLonPixels(FlightData.getInstance().getPosition(), this.getWidth(), this.getHeight());
        g.setColor(Constants.smaragdColor);
        g.fillOval(FlightData.getInstance().getPosition().lonPixels - (smaragdIconSize / 2), FlightData.getInstance().getPosition().latPixels - (smaragdIconSize / 2), smaragdIconSize, smaragdIconSize);

    }

    /** 
     * This method is overriden to return the preferred size 
     * which will be the width and height of the image plus 
     * the zoomed width width and height.  
     * while zooming out the zoomed width and height is negative 
     */
    public Dimension getPreferredSize() {
        return new Dimension((int) (m_image.getWidth(this)
                + (m_image.getWidth(this) * (m_zoom - 1))),
                (int) (m_image.getHeight(this)
                + (m_image.getHeight(this) * (m_zoom - 1))));
    }

    public double pixelToLatitude(int p) {
        return ((p + this.getVisibleRect().y) / (double) this.getHeight()) * (Constants.lowerRightCornerLat - Constants.upperLeftCornerLat) + Constants.upperLeftCornerLat;
    }

    public double pixelToLongitude(int p) {
        return ((p + this.getVisibleRect().x) / (double) this.getWidth()) * (Constants.lowerRightCornerLon - Constants.upperLeftCornerLon) + Constants.upperLeftCornerLon;
    }

    public void mouseMoved(double x, double y) {
        double width = this.getWidth();
        double height = this.getHeight();
        mouseX = (int) x + this.getVisibleRect().x;
        mouseY = (int) y + this.getVisibleRect().y;
        mouseLat = (mouseY / height) * (Constants.lowerRightCornerLat - Constants.upperLeftCornerLat) + Constants.upperLeftCornerLat;
        mouseLon = (mouseX / width) * (Constants.lowerRightCornerLon - Constants.upperLeftCornerLon) + Constants.upperLeftCornerLon;

    }

    public void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    public void setMouseOver(boolean isMouseOver) {
        this.isMouseOver = isMouseOver;
    }

    public void drawLine(Point p1, Point p2) {

        this.p1 = p1;
        this.p2 = p2;

    }
}
