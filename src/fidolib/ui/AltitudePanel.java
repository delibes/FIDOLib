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

import fidolib.data.Position;
import fidolib.data.Constants;
import fidolib.data.FlightData;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen
 */
public class AltitudePanel extends javax.swing.JPanel {

    private int spotSize = 1;
    private int maxSpotSize = 14;
    private long lastSpotPaint = 0;
    private long deltaSpotPaint = 50;
    private Calendar calendar = Calendar.getInstance();
    private int xAxesBorder = 60; // Pixels
    private int yAxesBorder = 60; // Pixels
    private int xAxesScale = 10; // Km
    private int yAxesScale = 10; // Km
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
    public AltitudePanel(FlightData aFlightData) {
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

    public void paintRocketPositions(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        g.setColor(Constants.textColor);
        calendar = Calendar.getInstance();
        if ((calendar.getTimeInMillis() - lastSpotPaint) > deltaSpotPaint) {
            lastSpotPaint = calendar.getTimeInMillis();
            spotSize = (spotSize + 2) % maxSpotSize;

        }
        int xTicksPixels = (int) ((width - xAxesBorder * 2) / xAxesScale);
        int yTicksPixels = (int) ((height - yAxesBorder * 2) / yAxesScale);

        List positions = aFlightData.positions;
        if (positions != null) {
            for (int i = 0; i < positions.size() - 1; i++) {
                Position p1 = (Position)positions.get(i);
                Position p2 = (Position)positions.get(i);
                int x1 = (int) (Position.disanceNauticalMiles((Position)positions.get(0), p1) * Constants.nauticalMile / 1000.0);
                int x2 = (int) (Position.disanceNauticalMiles((Position)positions.get(0), p2) * Constants.nauticalMile / 1000.0);
                
                int y1 = height - yAxesBorder - (int) (p1.GPAAltitude * yTicksPixels / 1000.0) - spotSize / 2;
                int y2 = height - yAxesBorder - (int) (p2.GPAAltitude * yTicksPixels / 1000.0) - spotSize / 2;
                g.drawLine(x1, y1, x2, y2);
            }
        }

        g.setColor(Constants.rocketColor);
        int xPos = 0;
        if ((positions != null) && (positions.size() > 0)) {
           xPos = (int) (Position.disanceNauticalMiles((Position)positions.get(0), aFlightData.rocketPosition) * Constants.nauticalMile / 1000.0) * xTicksPixels + xAxesBorder - spotSize / 2;
        }
        else {
           xPos = xAxesBorder - spotSize / 2;
            
        }
        int yPos = height - yAxesBorder - (int) (aFlightData.rocketPosition.GPAAltitude * yTicksPixels / 1000.0) - spotSize / 2;
        g.fillOval(xPos, yPos, spotSize, spotSize);

    }

    /**
     * Paint the coordinate system
     * @param g 
     */
    public void paintCoorSys(Graphics g) {

        int width = this.getWidth();
        int height = this.getHeight();
        g.setColor(Constants.backGroundColor);
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
        for (int i = 0; i <= xAxesScale; i += 1) {
            g.fillRect(xAxesBorder + (i * xTicksPixels / (xAxesScale / xAxesScale)), height - yAxesBorder, ticksLineWidth, ticksLineWidth * 4);
            sLength = g.getFontMetrics().stringWidth("" + i);
            g.drawString("" + i, xAxesBorder + (i * xTicksPixels) - sLength / 4, height - yAxesBorder / 2);
        }
        String downRangeStr = "Down range (Km)";
        sLength = g.getFontMetrics().stringWidth(downRangeStr);
        g.drawString(downRangeStr, width / 2 - sLength / 2, height - yAxesBorder / 4);

        // Y axes
        int yTicksPixels = (int) ((height - yAxesBorder * 2) / yAxesScale);
        for (int i = 0; i <= yAxesScale; i += 1) {
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
