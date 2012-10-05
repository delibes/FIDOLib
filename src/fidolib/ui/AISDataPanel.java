/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AISDataPanel.java
 *
 * Created on 05-10-2012, 12:41:34
 */
package fidolib.ui;

import fidolib.data.AISData;
import fidolib.data.Constants;
import fidolib.data.FlightData;
import fidolib.data.Position;
import fidolib.data.VesselInfo;
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
public class AISDataPanel extends javax.swing.JPanel {

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

    /** Creates new form AISDataPanel */
    public AISDataPanel(AISData aAISData, FlightData aFlightData) {
        this.aAISData = aAISData;
        this.aFlightData = aFlightData;
        initComponents();
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
        int deltaTextPos = this.getWidth() / 4 + 20;
        Font font = new Font("New Courier", Font.BOLD, fontSize);
        g.setFont(font);
        int fontSizeFactor = 7;
        fontSizeFactor = 7;

        // Spunik

        fontSizeFactor = 7;
        g.drawString("Sputnik", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Lat ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Lon ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("COG ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("SOG ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("TH ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("D. MC", textPos, this.getHeight() - (fontSize * fontSizeFactor--));


        textPos += deltaTextPos;
        Calendar c1 = Calendar.getInstance();
        String deltaTSputnikData = "";
        VesselInfo sputnik = aAISData.getVessel(aAISData.sputnikMMSI);
        fontSizeFactor = 7;
        if (sputnik == null) {
            deltaTSputnikData = Constants.naString;
            g.drawString(deltaTSputnikData, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        } else {
            if (sputnik.timeStamp != 0) {

                long sinceLastData = c1.getTimeInMillis() - sputnik.timeStamp;
                String secondsStr = String.format("%02d", (sinceLastData % 60000 / 1000));
                String minutesStr = String.format("%02d", (sinceLastData / 60000));
                deltaTSputnikData = "" + minutesStr + ":" + secondsStr;
            } else {
                deltaTSputnikData = Constants.naString;
            }
            g.drawString(deltaTSputnikData, textPos, this.getHeight() - (fontSize * fontSizeFactor--));

            if (sputnik.timeStamp != 0) {
                g.drawString(sputnik.pos.getLat(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString(sputnik.pos.getLon(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString("" + sputnik.cog + Constants.degreeChar, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString("" + sputnik.getSOG(true), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString(sputnik.getTH(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));

            }
            if ((aAISData.getVessel(aAISData.hjortoeMMSI) != null) && ((aAISData.getVessel(aAISData.sputnikMMSI) != null))) {
                double disanceNauticalMiles = Position.disanceNauticalMiles(aAISData.getVessel(aAISData.hjortoeMMSI).pos, aAISData.getVessel(aAISData.sputnikMMSI).pos);
                double distanceMeters = disanceNauticalMiles * Constants.nauticalMile;

                String distStr = "";
                DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
                decimalSymbols.setDecimalSeparator('.');
                decimalSymbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
                distStr = " " + df.format(disanceNauticalMiles) + " / " + df.format(distanceMeters / 1000);

                g.drawString(distStr, textPos, this.getHeight() - (fontSize * fontSizeFactor--));

            } else {
                //  g.drawString(Constants.naString, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
            }
        }

        // Hjortoe
        textPos += deltaTextPos;
        fontSizeFactor = 7;
        g.drawString("Hjortø", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Lat ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("Lon ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("COG ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("SOG ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        g.drawString("TH ", textPos, this.getHeight() - (fontSize * fontSizeFactor--));

        textPos += deltaTextPos / 2;
        fontSizeFactor = 7;
        String deltaTHjortoeData = "";
        VesselInfo hjortoe = aAISData.getVessel(aAISData.hjortoeMMSI);
        if (hjortoe == null) {
            deltaTHjortoeData = Constants.naString;
            g.drawString(deltaTHjortoeData, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
        } else {
            if (hjortoe.timeStamp != 0) {

                long sinceLastData = c1.getTimeInMillis() - hjortoe.timeStamp;
                String secondsStr = String.format("%02d", (sinceLastData % 60000 / 1000));
                String minutesStr = String.format("%02d", (sinceLastData / 60000));
                deltaTHjortoeData = "" + minutesStr + ":" + secondsStr;
            } else {
                deltaTHjortoeData = Constants.naString;
            }
            g.drawString(deltaTHjortoeData, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
            if (hjortoe.timeStamp != 0) {
                g.drawString(hjortoe.pos.getLat(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString(hjortoe.pos.getLon(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString("" + hjortoe.cog + Constants.degreeChar, textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString("" + hjortoe.getSOG(true), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
                g.drawString(hjortoe.getTH(), textPos, this.getHeight() - (fontSize * fontSizeFactor--));
            }
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
