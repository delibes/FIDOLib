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
import fidolib.data.Position;
import fidolib.misc.AuxiliaryFunctions;
import java.awt.Font;
import java.awt.Graphics;
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
public class FlightDataPanel extends javax.swing.JPanel {
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
    public FlightDataPanel( AISData aAISData, FlightData aFlightData) {
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
        g.setColor(Constants.backGroundColor);
        g.fillRect(0, 0, width, height);
        // Draw a surrounding rectangle
        g.setColor(Constants.textColor);


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

        int fontSize = this.getWidth() / 25;
        int textPos = 10;
        int deltaTextPos = this.getWidth() / 4;
        Font font = new Font("New Courier", Font.BOLD, fontSize);
        g.setFont(font);
        int fontSizeFactor = 6;
        g.drawString("Payload", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Lat ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Lon ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Alt ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("B.A. ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Time ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("D. MC", textPos, this.getHeight() - (fontSize * fontSizeFactor--));


        textPos += deltaTextPos ;
        // Print time since last valid data reception
        fontSizeFactor = 6;
        if (aFlightData.lastValidDataTimeStamp > 0) {
            Calendar calender = Calendar.getInstance();
            long now = calender.getTime().getTime();

            font = new Font("New Courier", Font.BOLD, fontSize);
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

        }
        else {
            fontSizeFactor--;
        }
        String latStr = "" + aFlightData.rocketPosition.getLat();
        if (aFlightData.rocketPosition.latitudeGood != true) {

            latStr = "(" + latStr + ")";
        }
        String lonStr = "" + aFlightData.rocketPosition.getLon();
        if (aFlightData.rocketPosition.longitudeGood != true) {
            lonStr = "(" + lonStr + ")";
        }
        g.drawString(latStr, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString(lonStr, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.rocketPosition.GPAAltitude + " m", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + aFlightData.barometerAltitude + " m", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("" + AuxiliaryFunctions.getTimeStamp(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        if (aAISData.getVessel(aAISData.hjortoeMMSI) != null) {
            double disanceNauticalMiles = Position.disanceNauticalMiles(aFlightData.rocketPosition, aAISData.getVessel(aAISData.hjortoeMMSI).pos);
            double distanceMeters = disanceNauticalMiles * Constants.nauticalMile;

            String distStr = "";
            DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
            decimalSymbols.setDecimalSeparator('.');
            decimalSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
            distStr = " " + df.format(disanceNauticalMiles) + " / " + df.format(distanceMeters / 1000);

            g.drawString(distStr, textPos, this.getHeight() - (fontSize * fontSizeFactor--));

        } 

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
