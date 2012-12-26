/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Steen Andersen
 */
package fidolib.ui;

import fidolib.data.Constants;
import fidolib.data.CountDownData;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen Andersen
 */
public class ProgressBarPanel extends javax.swing.JPanel {

    /**
     * Text border
     */
    private int border = 40;
    /**
     * Font size relative to panel size
     */
    //private int fontSize = 0;
    /**
     * The width of the time line markings
     */
    private int barWidth = 4;
    /**
     * Timer delay
     */
    int delay = 0;
    /**
     * Timer period for GUI updates
     */
    int period = Constants.deltaT_GUI;
    /**
     * The timer it self
     */
    private Timer timer = new Timer();
    /**
     * Progress bar start x pixel
     */
    private int progressBarStart = 40;
    /**
     * Progress bar width x pixel
     */
    private int progressBarWidth = -1; // To be set when the panel is painted
    /**
     * The height border of the progress bar
     */
    private int progressBarBorder = 40;

    /** Creates new form ProgressBarPanel */
    public ProgressBarPanel() {
        initComponents();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                repaint();
            }
        }, delay, period);
    }

    @Override
    public void paint(Graphics g) {
        // Clear the backgroupd
        clearBackgrouod(g);

        // Paint the progress bar
        paintProgressBar(g);
    }

    /**
     * Paint the progress bar
     */
    public void paintProgressBar(Graphics g) {

        int width = this.getWidth();
        int height = this.getHeight();
        int progressBarHeight = height / 4;

        progressBarWidth = width - (progressBarStart * 2);
        g.setColor(Constants.progressBarColor);
        // Draw the surrounding the rectangle
        g.drawRect(progressBarStart, progressBarBorder, progressBarWidth, progressBarHeight);


        // Draw the text
        g.setColor(Constants.textColor);
        int fontSize = ((int) (progressBarHeight * 4 / 6));
        Font font = new Font("SansSerif", Font.BOLD, fontSize);
        g.setFont(font);
        // Calculate the time distance
        double diffMSec = (double) ((Constants.progressbarEND - Constants.progressbarSTART) * 1000);
        double relativePosOfMECO = (double) (((Constants.progressbarSTART * -1) + Constants.progressbarMECO) * 1000) / diffMSec;
        double relativePosOf2IGN = (double) (((Constants.progressbarSTART * -1) + Constants.progressbar2IGN) * 1000) / diffMSec;
        double relativePosOf2MECO = (double) (((Constants.progressbarSTART * -1) + Constants.progressbar2MECO) * 1000) / diffMSec;
        double relativePosOfSEP = (double) (((Constants.progressbarSTART * -1) + Constants.progressbarSEP) * 1000) / diffMSec;

        double relativePosOfStart = 0.0;

        // Draw T+XX at the end of the time line
        String timeStr = "" + (CountDownData.getMinSec(Constants.progressbarEND));
        int sLength = g.getFontMetrics().stringWidth(timeStr);
        g.drawString(timeStr, progressBarStart + progressBarWidth - (sLength / 2), height - 6);
        g.fillRect(progressBarStart + progressBarWidth - (barWidth / 2), border, barWidth, progressBarHeight + 6);

        // Draw T- start
        String str = "" + CountDownData.getMinSec(Constants.progressbarSTART);
        sLength = g.getFontMetrics().stringWidth(str);
        g.drawString(str, progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfStart)) - (sLength / 2), height - 6);
        g.fillRect(progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfStart)) - (barWidth / 2), border, barWidth, progressBarHeight + 6);
// Draw MECO
        str = Constants.progressbarMECOStr;
        sLength = g.getFontMetrics().stringWidth(str);
        g.drawString(str, progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfMECO)) - (sLength / 2),border-10);
        g.fillRect(progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfMECO)) - (barWidth / 2), border-6, barWidth, progressBarHeight+6);

        // Draw 2. STG IGN
        str = Constants.progressbar2STGStr;
        
        sLength = g.getFontMetrics().stringWidth(str);
        g.drawString(str, progressBarStart + ((int) (((double) progressBarWidth) * relativePosOf2IGN)) - (sLength / 2), height - 6);
        g.fillRect(progressBarStart + ((int) (((double) progressBarWidth) * relativePosOf2IGN)) - (barWidth / 2), border, barWidth, progressBarHeight + 6);

        // Draw 2.MECO
        str = Constants.progressbar2STGMECOStr;
        sLength = g.getFontMetrics().stringWidth(str);
        g.drawString(str, progressBarStart + ((int) (((double) progressBarWidth) * relativePosOf2MECO)) - (sLength / 2), border-10);
        g.fillRect(progressBarStart + ((int) (((double) progressBarWidth) * relativePosOf2MECO)) - (barWidth / 2), border-6, barWidth, progressBarHeight+6);


        // Draw SEP
        str = Constants.progressbarSEPStr;
        sLength = g.getFontMetrics().stringWidth(str);
        g.drawString(str, progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfSEP)) - (sLength / 2), height - 6);
        g.fillRect(progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfSEP)) - (barWidth / 2), border, barWidth, progressBarHeight + 6);


        double relativePosOfTMinusZero = (double) (Constants.progressbarSTART * -1 * 1000) / diffMSec;

        // Draw T+00
        str = "" + CountDownData.getMinSec(0);
        sLength = g.getFontMetrics().stringWidth(str);
        g.drawString(str, progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfTMinusZero)) - (sLength / 2), height - 6);
        g.fillRect(progressBarStart + ((int) (((double) progressBarWidth) * relativePosOfTMinusZero)) - (barWidth / 2), border, barWidth, progressBarHeight + 6);



        // Draw the progress

        double relativePosOfRemainingTime = (double) (((Constants.progressbarSTART * -1000L) - CountDownData.remainingTime + 500L)) / diffMSec;
        if ((relativePosOfRemainingTime > 0)) {
            if (relativePosOfRemainingTime <= 1.0) {
                g.fillRect(progressBarStart, progressBarBorder, ((int) ((double) progressBarWidth * relativePosOfRemainingTime)), progressBarHeight);
            } else {
                relativePosOfRemainingTime = 1.0;
                g.fillRect(progressBarStart, progressBarBorder, ((int) ((double) progressBarWidth * relativePosOfRemainingTime)), progressBarHeight);
            }
        }

    }

    /**
     * Clear the back ground
     */
    public void clearBackgrouod(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        // Clear the background
        g.setColor(Constants.backGroundColor);
        g.fillRect(0, 0, width, height);

    }

    /**
     * Print the time stamp
     * @param g the graphics object to paint on
     */
    public void paintTimeStamp(Graphics g) {
        int width = this.getWidth();


        // Set text color and font size
        g.setColor(Constants.textColor);
        int timeStampFontSize = 30;
        Font font = new Font("SansSerif", Font.BOLD, timeStampFontSize);
        g.setFont(font);

        Calendar rightNow = Calendar.getInstance();
        Date d = rightNow.getTime();
        SimpleDateFormat formatter;
        String timeStr;
        String pattern = "HH:mm:ss";
        formatter = new SimpleDateFormat(pattern);
        timeStr = formatter.format(d);
        int sLength = g.getFontMetrics().stringWidth(timeStr);
        g.drawString(timeStr, width - (sLength + border), timeStampFontSize);




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
            .addGap(0, 93, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
