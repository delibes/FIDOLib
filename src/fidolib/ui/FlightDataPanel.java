/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FlightDataPanel.java
 *
 * Created on 05-10-2012, 12:36:42
 */
package fidolib.ui;

import fidolib.data.AISData;
import fidolib.data.Constants;
import fidolib.data.FlightData;
import fidolib.data.RocketInfo;
import fidolib.data.VesselInfo;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen
 */
public class FlightDataPanel extends ColorPanel {

    /**
     * Reference to the flight data
     * 
     */
    private FlightData aFlightData = null;
    /**
     * Reference to the AIS data
     * 
     */
    private AISData aAISData = null;
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

    /** Creates new form FlightDataPanel */
    public FlightDataPanel(boolean useGradientColors, Color textColor, Color backgroundColor, Color gradientColorStart,Color gradientColorStop, AISData aAISData, FlightData aFlightData) {
         this.useGradientColors = useGradientColors;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.gradientColorStart = gradientColorStart;
        this.gradientColorStop = gradientColorStop;
        this.aAISData = aAISData;
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
        int width = this.getWidth();
        int height = this.getHeight();
        int smallest = (int) ((double) width / 2.6);
        if (height < smallest) {
            smallest = height;
        }
        // Clear the background
        if (useGradientColors == true) {
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, gradientColorStart, 0, height, gradientColorStop, true);
            Paint p = g2.getPaint();
            g2.setPaint(gp);
        } else {
            g.setColor(backgroundColor);
        }

        g.fillRect(0, 0, width, height);
        // Set text color and font size
        g.setColor(Constants.textColor);
        int sLength = 0;
        // Larger font for count down clock
        int fontSize = ((int) (((double) smallest / 1.7)));
        Font font = new Font("New Courier", Font.BOLD, fontSize);

        g.setFont(font);

        if (Constants.paintData == true) {
            paintData(g);
        }


    }

    public void paintData(Graphics g) {

        int fontSize = this.getWidth() / 24;
        int textPos = 10;
        int deltaTextPos = this.getWidth() / 5 + 40;
        Font font = new Font("SansSerif", Font.BOLD, fontSize);
        g.setFont(font);
        int maxFontSizeFactor = 9;
        int fontSizeFactor = maxFontSizeFactor;
        //g.drawString("GPS data", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Telemetry", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Latitude", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Longitude", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Dist. MC", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("BRG MC", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("GPS", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("ETA", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("AAU v", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        
        textPos += deltaTextPos;
        // Print time since last valid data reception
        fontSizeFactor = maxFontSizeFactor;
        if (aFlightData.lastValidDataTimeStamp > 0) {
            Calendar calender = Calendar.getInstance();
            long now = calender.getTime().getTime();

            font = new Font("SansSerif", Font.BOLD, fontSize);
            g.setFont(font);
            long sinceLastData = now - aFlightData.lastValidDataTimeStamp;
            if (sinceLastData > 1000) {
                String secondsStr = String.format("%02d", (sinceLastData % 60000 / 1000));
                String minutesStr = String.format("%02d", (sinceLastData / 60000));

                String sinceLastDataStr = "" + minutesStr + ":" + secondsStr;
                g.drawString(sinceLastDataStr + " " + aFlightData.noGoodPackets + "/" + aFlightData.noBadPackets, textPos, this.getHeight() - (fontSize * fontSizeFactor--));

            } else {
                g.drawString("" + aFlightData.noGoodPackets + "/" + aFlightData.noBadPackets, textPos, this.getHeight() - (fontSize * fontSizeFactor--));

            }

        } else {
            fontSizeFactor--;
        }
        String latStr = "" + aFlightData.rocketPosition.getLat();
        if (aFlightData.rocketPosition.latitudeGood != true) {

            latStr =  Constants.naString;
        }
        String lonStr = "" + aFlightData.rocketPosition.getLon();
        if (aFlightData.rocketPosition.longitudeGood != true) {
            lonStr = Constants.naString;
        }
        g.drawString(latStr, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString(lonStr, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString(aFlightData.getMCDistance(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.getMCBearing(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.getGPSTime(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.getETA(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.gettAAUVoltage(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        
        fontSizeFactor = maxFontSizeFactor - 1;
        textPos += deltaTextPos +40;
        g.drawString("Alt", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Dwn", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("COG", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Fix", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("V", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("V v", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("V h", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        fontSizeFactor = maxFontSizeFactor - 1;
        textPos += deltaTextPos -60;
        g.drawString("" + aFlightData.rocketPosition.GPSAltitude  + " m", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + (int) (aFlightData.rocketPosition.downRange) + " m", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + (int) (aFlightData.rocketPosition.COG) + Constants.degreeChar, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.rocketPosition.getGPSFix(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + (int) (aFlightData.rocketPosition.velocity) + " m/s", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + (int) (aFlightData.rocketPosition.verticalVelocity) + " m/s", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + (int) (aFlightData.rocketPosition.horizontalVelocity) + " m/s", textPos, this.getHeight() - (fontSize * fontSizeFactor--));


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
