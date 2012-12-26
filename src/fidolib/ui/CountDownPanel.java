/**
 *
 * @author Steen Andersen
 */
package fidolib.ui;

import fidolib.data.Constants;
import fidolib.data.CountDownData;
import fidolib.misc.AuxiliaryFunctions;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen Andersen
 */
public class CountDownPanel extends ColorPanel {

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
     * Creates new form CountDownPanel
     */
    public CountDownPanel(boolean useGradientColors, Color textColor, Color backgroundColor, Color gradientColorStart, Color gradientColorStop) {
        this.useGradientColors = useGradientColors;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.gradientColorStart = gradientColorStart;
        this.gradientColorStop = gradientColorStop;
        initComponents();
//        this.aAISData = aAISData;
//        this.aFlightData = aFlightData;
        MouseAdaptorCountDownPanel aMouseAdaptorCountDownPanel = new MouseAdaptorCountDownPanel(this);
        this.addMouseMotionListener(aMouseAdaptorCountDownPanel);
        this.addMouseListener(aMouseAdaptorCountDownPanel);

        // Set and start the timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
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
            Graphics2D g2;
            if (g instanceof Graphics2D) {
                g2 = (Graphics2D) g;
            } else {
                return;
            }
            GradientPaint gp = new GradientPaint(0, 0, gradientColorStart, 0, height, gradientColorStop, true);
            g2.setPaint(gp);
        } else {
            g.setColor(backgroundColor);
        }

        g.fillRect(0, 0, width, height);
        // Set text color and font size
        g.setColor(textColor);

        // Larger font for count down clock
        int fontSize = ((int) (((double) smallest / 1.7)));
        Font font = new Font("New Courier", Font.BOLD, fontSize);

        g.setFont(font);
        // Paint count down clock
        String timeStr = AuxiliaryFunctions.long2StringTime(CountDownData.remainingTime);
        int sLength = g.getFontMetrics().stringWidth(timeStr);
        g.drawString(timeStr, (width / 2) - (sLength / 2), height / 2 + fontSize / 3);

        String realTimeStr = AuxiliaryFunctions.getTimeStamp();
        fontSize = ((int) (((double) smallest / (1.7 * 5))));
        font = new Font("New Courier", Font.BOLD, fontSize);
        g.setFont(font);


        sLength = g.getFontMetrics().stringWidth(realTimeStr);
        g.drawString(realTimeStr, (width / 2) - (sLength / 2), height - fontSize);




    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
