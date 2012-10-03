/**
 *
 * @author Steen Andersen
 */
package fidolib.ui;

import fidolib.data.Constants;
import fidolib.data.CountDownData;
import fidolib.data.VesselInfo;
import fidolib.data.AISData;
import fidolib.data.FlightData;
import fidolib.misc.AuxiliaryFunctions;
import java.awt.Font;
import java.awt.Graphics;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen Andersen
 */
public class CountDownPanel extends javax.swing.JPanel {

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

    /** Creates new form CountDownPanel */
    public CountDownPanel(AISData aAISData) {
        initComponents();
        this.aAISData = aAISData;
        MouseAdaptorCountDownPanel aMouseAdaptorCountDownPanel = new MouseAdaptorCountDownPanel(this);
        this.addMouseMotionListener(aMouseAdaptorCountDownPanel);
        this.addMouseListener(aMouseAdaptorCountDownPanel);

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
        // Paint count down clock
        String timeStr = AuxiliaryFunctions.long2StringTime(CountDownData.remainingTime);
        sLength = g.getFontMetrics().stringWidth(timeStr);
        g.drawString(timeStr, (width / 2) - (sLength / 2), height / 2 + fontSize / 3);

        if (Constants.paintData == true) {
            paintData(g);
        }


    }

    public void paintData(Graphics g) {

        int fontSize = this.getHeight() / 32;
        Font font = new Font("New Courier", Font.BOLD, fontSize);
        g.setFont(font);
        // Rocket
        int textPos = 10;
        g.drawString("Rocket", textPos, this.getHeight() - (fontSize * 6));
        g.drawString("Lat ", textPos, this.getHeight() - (fontSize * 5));
        g.drawString("Lon ", textPos, this.getHeight() - (fontSize * 4));
        g.drawString("Alt ", textPos, this.getHeight() - (fontSize * 3));
        g.drawString("B.A. ", textPos, this.getHeight() - (fontSize * 2));
        g.drawString("Time ", textPos, this.getHeight() - (fontSize * 1));

        textPos = 100;
        // Print time since last valid data reception
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
            Calendar calender = Calendar.getInstance();
            long now = calender.getTime().getTime();

            font = new Font("New Courier", Font.BOLD, fontSize);
            g.setFont(font);
            long sinceLastData = now - FlightData.getInstance().lastValidDataTimeStamp;
            if (sinceLastData > 1000) {
                String secondsStr = String.format("%02d", (sinceLastData % 60000 / 1000));
                String minutesStr = String.format("%02d", (sinceLastData / 60000));

                String sinceLastDataStr = "" + minutesStr + ":" + secondsStr;
                g.drawString(sinceLastDataStr + " " + FlightData.getInstance().noGoodPackets + "/" + FlightData.getInstance().noBadPackets, textPos, this.getHeight() - (fontSize * 6));

            } else {
                g.drawString("" + FlightData.getInstance().noGoodPackets + "/" + FlightData.getInstance().noBadPackets, textPos, this.getHeight() - (fontSize * 6));

            }

        }

        String latStr = "" + FlightData.getInstance().rocketPosition.getLat();
        if (FlightData.getInstance().rocketPosition.latitudeGood != true) {

            latStr = "(" + latStr + ")";
        }
        String lonStr = "" + FlightData.getInstance().rocketPosition.getLon();
        if (FlightData.getInstance().rocketPosition.longitudeGood != true) {
            lonStr = "(" + lonStr + ")";
        }
        g.drawString(latStr, textPos, this.getHeight() - (fontSize * 5));
        g.drawString(lonStr, textPos, this.getHeight() - (fontSize * 4));
        g.drawString("" + FlightData.getInstance().gpsAltitude + " m", textPos, this.getHeight() - (fontSize * 3));
        g.drawString("" + FlightData.getInstance().barometerAltitude + " m", textPos, this.getHeight() - (fontSize * 2));
        g.drawString("" + getTimeStamp(), textPos, this.getHeight() - (fontSize * 1));

        // Spunik
        textPos = 300;
        g.drawString("Sputnik", textPos, this.getHeight() - (fontSize * 6));
        g.drawString("Lat ", textPos, this.getHeight() - (fontSize * 5));
        g.drawString("Lon ", textPos, this.getHeight() - (fontSize * 4));
        g.drawString("COG ", textPos, this.getHeight() - (fontSize * 3));
        g.drawString("SOG ", textPos, this.getHeight() - (fontSize * 2));
        g.drawString("TH ", textPos, this.getHeight() - (fontSize * 1));

        textPos = 400;
        Calendar c1 = Calendar.getInstance();
        String deltaTSputnikData = "";
        VesselInfo sputnik = aAISData.getVessel(aAISData.sputnikMMSI);
        if (sputnik == null) {
            deltaTSputnikData = Constants.naString;
            g.drawString(deltaTSputnikData, textPos, this.getHeight() - (fontSize * 6));
        } else {
            if (sputnik.timeStamp != 0) {

                long sinceLastData = c1.getTimeInMillis() - sputnik.timeStamp;
                String secondsStr = String.format("%02d", (sinceLastData % 60000 / 1000));
                String minutesStr = String.format("%02d", (sinceLastData / 60000));
                deltaTSputnikData = "" + minutesStr + ":" + secondsStr;
            } else {
                deltaTSputnikData = Constants.naString;
            }
            g.drawString(deltaTSputnikData, textPos, this.getHeight() - (fontSize * 6));

            if (sputnik.timeStamp != 0) {
                g.drawString(sputnik.pos.getLat(), textPos, this.getHeight() - (fontSize * 5));
                g.drawString(sputnik.pos.getLon(), textPos, this.getHeight() - (fontSize * 4));
                g.drawString("" + sputnik.cog + Constants.degreeChar, textPos, this.getHeight() - (fontSize * 3));
                g.drawString("" + sputnik.getSOG(true), textPos, this.getHeight() - (fontSize * 2));
                g.drawString(sputnik.getTH(), textPos, this.getHeight() - (fontSize * 1));
            }
        }
        // Hjortoe
        textPos = 550;
        g.drawString("Hjortø", textPos, this.getHeight() - (fontSize * 6));
        g.drawString("Lat ", textPos, this.getHeight() - (fontSize * 5));
        g.drawString("Lon ", textPos, this.getHeight() - (fontSize * 4));
        g.drawString("COG ", textPos, this.getHeight() - (fontSize * 3));
        g.drawString("SOG ", textPos, this.getHeight() - (fontSize * 2));
        g.drawString("TH ", textPos, this.getHeight() - (fontSize * 1));

        textPos = 650;
        String deltaTHjortoeData = "";
        VesselInfo hjortoe = aAISData.getVessel(aAISData.hjortoeMMSI);
        if (hjortoe == null) {
            deltaTHjortoeData = Constants.naString;
            g.drawString(deltaTHjortoeData, textPos, this.getHeight() - (fontSize * 6));
        } else {
            if (hjortoe.timeStamp != 0) {

                long sinceLastData = c1.getTimeInMillis() - hjortoe.timeStamp;
                String secondsStr = String.format("%02d", (sinceLastData % 60000 / 1000));
                String minutesStr = String.format("%02d", (sinceLastData / 60000));
                deltaTHjortoeData = "" + minutesStr + ":" + secondsStr;
            } else {
                deltaTHjortoeData = Constants.naString;
            }
            g.drawString(deltaTHjortoeData, textPos, this.getHeight() - (fontSize * 6));
            if (hjortoe.timeStamp != 0) {
                g.drawString(hjortoe.pos.getLat(), textPos, this.getHeight() - (fontSize * 5));
                g.drawString(hjortoe.pos.getLon(), textPos, this.getHeight() - (fontSize * 4));
                g.drawString("" + hjortoe.cog + Constants.degreeChar, textPos, this.getHeight() - (fontSize * 3));
                g.drawString("" + hjortoe.getSOG(true), textPos, this.getHeight() - (fontSize * 2));
                g.drawString(hjortoe.getTH(), textPos, this.getHeight() - (fontSize * 1));
            }
        }
    }

    /**
     * Return the real time as a string
     * @return the time stamp
     */
    public String getTimeStamp() {
        String s;
        Format formatter;
        Calendar calender = Calendar.getInstance();
        long now = calender.getTime().getTime();
        formatter = new SimpleDateFormat("HH.mm.ss");
        s = formatter.format(now);
        return s;
    }

    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N

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
