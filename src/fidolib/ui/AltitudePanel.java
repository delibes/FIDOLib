/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AltitudePanel.java
 *
 * Created on 21-09-2012, 19:31:24
 */
package fidolib.ui;

import fidolib.data.RocketInfo;
import fidolib.data.Constants;
import fidolib.data.FlightData;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen
 */
public class AltitudePanel extends ColorPanel {

    private int deltaSpotSize = 2;
    private int maxSpotSize = 16;
    private int minSpotSize = 4;
    private int spotSize = minSpotSize;
    private long lastSpotPaint = 0;
    private long deltaSpotPaint = 50;
    private Calendar calendar = Calendar.getInstance();
    private int xAxesBorder = 60; // Pixels
    private int yAxesBorder = 60; // Pixels
    private int xAxesScale = 5; // Km
    private int yAxesScale = 10; // Km
    private int xAxesScaleIncrement = xAxesScale;
    private int yAxesScaleIncrement = yAxesScale;
    private int xTicks = 5; // has to be <= xAxesScale
    private int yTicks = 10; // has to be <= yAxesScale
    private int lineWidth = 3;
    private int ticksLineWidth = 2;
    private int fontSize = 12;
    /**
     * Timer delay
     */
    private int delay = 0;
    /**
     * Timer period for GUI updates
     */
    private long period = Constants.deltaT_GUI;
    /**
     * The timer it self
     */
    private Timer timer = new Timer();
    /**
     * Reference to the flight data
     * 
     */
    private FlightData aFlightData = null;

    /** Creates new form AltPanel */
    public AltitudePanel(boolean useGradientColors, Color textColor, Color backgroundColor, Color gradientColorStart, Color gradientColorStop, FlightData aFlightData) {
        this.useGradientColors = useGradientColors;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.gradientColorStart = gradientColorStart;
        this.gradientColorStop = gradientColorStop;
        this.aFlightData = aFlightData;
        initComponents();


        // Set and start the timer
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                repaint();
            }
        }, delay, period);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        paintCoorSys(g);
        paintRocketPositions(g);

    }

    /**
     * Paint the privious rocket positions and the present position
     * @param g 
     */
    public void paintRocketPositions(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(lineWidth));
        g.setColor(Constants.textColor);
        calendar = Calendar.getInstance();
        if ((calendar.getTimeInMillis() - lastSpotPaint) > deltaSpotPaint) {
            lastSpotPaint = calendar.getTimeInMillis();
            spotSize = (spotSize + deltaSpotSize);
            if (spotSize >= maxSpotSize) {
                deltaSpotSize *= -1;
            } else if (spotSize <= minSpotSize) {
                deltaSpotSize *= -1;
            }

        }
        int xTicksPixels = (int) ((width - xAxesBorder * 2) / xAxesScale);
        int yTicksPixels = (int) ((height - yAxesBorder * 2) / yAxesScale);

        if (aFlightData.rocketPosition.downRange > (xAxesScale * 1000)) {
                    xAxesScale += xAxesScaleIncrement;
                }
        if (aFlightData.rocketPosition.GPSAltitude > (yAxesScale * 1000)) {
                    yAxesScale += yAxesScaleIncrement;
                    
                }
        List positions = aFlightData.positions;
        RocketInfo liftOffPosition = aFlightData.liftOffPosition;
        if (positions != null && liftOffPosition != null && positions.size() > 2) {
            for (int i = 0; i < positions.size() - 2; i++) {
                RocketInfo p1 = (RocketInfo) positions.get(i);
                RocketInfo p2 = (RocketInfo) positions.get(i + 1);
                double xDist = RocketInfo.disanceNauticalMiles(liftOffPosition, p1)
                        * Constants.nauticalMile;
                

              //  System.out.println(xDist);
                int x1 = (int) (RocketInfo.disanceNauticalMiles(liftOffPosition, p1)
                        * Constants.nauticalMile / 1000.0 * xTicksPixels + xAxesBorder);
                int x2 = (int) (RocketInfo.disanceNauticalMiles(liftOffPosition, p2)
                        * Constants.nauticalMile / 1000.0 * xTicksPixels + xAxesBorder);

                int y1 = height - yAxesBorder - (int) (p1.GPSAltitude * yTicksPixels / 1000.0);
                

                
                int y2 = height - yAxesBorder - (int) (p2.GPSAltitude * yTicksPixels / 1000.0);
                g.drawLine(x1, y1, x2, y2);
            }
        }

        
        g.setColor(Constants.rocketColor);
        int xPos = 0;
        if (aFlightData.rocketPosition != null) {
            xPos = (int) (aFlightData.rocketPosition.downRange / 1000.0 * xTicksPixels + xAxesBorder - spotSize / 2);
        } else {
            xPos = xAxesBorder - spotSize / 2;

        }
        int yPos = height - yAxesBorder - (int) (aFlightData.rocketPosition.GPSAltitude * yTicksPixels / 1000.0) - spotSize / 2;
        g.fillOval(xPos, yPos, spotSize, spotSize);
        g.setColor(Constants.textColor);
        int textXPos = xPos + spotSize / 2 + maxSpotSize;
        int textYPos = yPos + spotSize / 2 - maxSpotSize;
        g.drawString("A " + aFlightData.rocketPosition.GPSAltitude + " m", textXPos, textYPos - g.getFont().getSize());
        g.drawString("D " + (int) (aFlightData.rocketPosition.downRange) + " m", textXPos, textYPos);
        //g.drawString("V    " + (int) (aFlightData.rocketPosition.velocity) + " m/s", textXPos, textYPos - g.getFont().getSize());
        //g.drawString("V v " + (int) (aFlightData.rocketPosition.verticalVelocity) + " m/s", textXPos, textYPos);
        //g.drawString("V h " + (int) (aFlightData.rocketPosition.horizontalVelocity) + " m/s", textXPos, textYPos +g.getFont().getSize() );

    }

    /**
     * Paint the coordinate system
     * @param g 
     */
    public void paintCoorSys(Graphics g) {

        int width = this.getWidth();
        int height = this.getHeight();

        if (useGradientColors == true) {
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, gradientColorStart, 0, height, gradientColorStop, true);
            Paint p = g2.getPaint();
            g2.setPaint(gp);
        } else {
            g.setColor(backgroundColor);
        }

        g.fillRect(0, 0, width, height);
        g.setColor(Constants.textColor);

        Font font = new Font("New Courier", Font.BOLD, fontSize);
        g.setFont(font);
        String title = "GPS data";
        int sLength = 0;
        sLength = g.getFontMetrics().stringWidth(title);
        g.drawString(title, width / 2 - sLength / 2, yAxesBorder / 2);

        g.fillRect(xAxesBorder, height - yAxesBorder, width - xAxesBorder * 2, lineWidth);
        g.fillRect(xAxesBorder, yAxesBorder, lineWidth, height - xAxesBorder * 2);



        // X axes
        int xTicksPixels = (int) ((width - xAxesBorder * 2) / xAxesScale);
        for (int i = 0; i <= xAxesScale; i += xAxesScale / xTicks) {
            g.fillRect(xAxesBorder + (i * xTicksPixels), height - yAxesBorder, ticksLineWidth, ticksLineWidth * 4);
            sLength = g.getFontMetrics().stringWidth("" + i);
            g.drawString("" + i, xAxesBorder + (i * xTicksPixels) - sLength / 4, height - yAxesBorder / 2);
        }
        String downRangeStr = "Down range (Km)";
        sLength = g.getFontMetrics().stringWidth(downRangeStr);
        g.drawString(downRangeStr, width / 2 - sLength / 2, height - yAxesBorder / 4);

        // Y axes
        int yTicksPixels = (int) ((height - yAxesBorder * 2) / yAxesScale);
        for (int i = 0; i <= yAxesScale; i += yAxesScale / yTicks) {
            g.fillRect(xAxesBorder - ticksLineWidth * 4, height - yAxesBorder - (i * yTicksPixels), ticksLineWidth * 4, ticksLineWidth);
            sLength = g.getFontMetrics().stringWidth("" + i);
            g.drawString("" + i, xAxesBorder / 3 * 2 - sLength, height + (fontSize / 2) - yAxesBorder - (i * yTicksPixels) - sLength / 4);
        }
        String altitudeRangeStr = "Altitude (Km)";
        sLength = g.getFontMetrics().stringWidth(altitudeRangeStr);
        g.drawString(altitudeRangeStr, xAxesBorder - sLength / 2, yAxesBorder / 4 * 3);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
