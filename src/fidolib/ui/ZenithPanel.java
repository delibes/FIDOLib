/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Zenith.java
 *
 * Created on 06-08-2010, 22:03:04
 */

package fidolib.ui;
import fidolib.data.Constants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Steen Andersen
 */
public class ZenithPanel extends javax.swing.JPanel {

    private double borderDistance = 40;
    private double lineThickness = 4;
    private double circleDiameter = 10;
    public double maxDegree = Constants.maxDegreeZenithPanel;
    private int origonX;
    private int origonY;
    private double numberOfDividers = 0;
    private double dividerLength = 10;
    private double xDegree = 0.0;
    private double zDegree = 0.0;
    private double xDegreeOld = 0.0;
    private double zDegreeOld = 0.0;
    private double x = 0; // pixel point for direction
    private double y = 0; // pixel point for direction
    private boolean autoAdjustRange = false;
    private char degreeChar = Constants.degreeChar;
    private double rangeLimitAlarm = Constants.rangeLimitAlarm;
    private boolean rangeLimitAlarmActive = Constants.rangeLimitAlarmActive;
    private Font boldFont = Constants.boldFont;
    private Font warningFont = Constants.warningFont;
    private Color backGroundColor = Constants.backGroundColor;
    private Color axesColor = Color.BLACK;
    private Point hist[] = new Point[40];
    private int histIndex = 0;

    /**
     * Creates new form Zenith
     */
    public ZenithPanel() {
        initComponents();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawCoordinateSystem(g);
        if (rangeLimitAlarmActive == true) {
            drawRangeLimit(g);
        }
        drawFlightDirection(g);




    }

    private void drawRangeLimit(Graphics g) {
        Color old = g.getColor();
        int width = this.getWidth();
        int height = this.getHeight();
        origonX = width / 2;
        origonY = height / 2;
        double xAxesWidth = width - (borderDistance * 2);
        double yAxesWidth = height - (borderDistance * 2);


        double degreeToPixelsX = xAxesWidth / (2 * maxDegree);

        double degreeToPixelsY = yAxesWidth / (2 * maxDegree);


        x = this.origonX + (degreeToPixelsX * zDegree);
        y = this.origonY - (degreeToPixelsY * xDegree);
        if (((xDegree < (rangeLimitAlarm * -1)) || (xDegree > rangeLimitAlarm))
                || ((zDegree < (rangeLimitAlarm * -1)) || (zDegree > rangeLimitAlarm))) {
            g.setColor(Constants.outOfRangeColor);
            Font oldFont = g.getFont();
            g.setFont(warningFont);
            String outOfRangeAlarm = "Out of range: Pitch = " + (int) xDegree + degreeChar + " Yaw = " + (int) zDegree + degreeChar + "";
            int sLength = g.getFontMetrics().stringWidth(outOfRangeAlarm);
            // int sHeight = g.getFontMetrics().getHeight();
            g.drawString(outOfRangeAlarm, (int) (origonX - (sLength / 2)), (this.getHeight() - 4));
            g.setFont(oldFont);

            g.drawRect(((int) (origonX - (rangeLimitAlarm * degreeToPixelsX))), ((int) (origonY - (rangeLimitAlarm * degreeToPixelsY))), (int) (rangeLimitAlarm * 2 * degreeToPixelsX), (int) (rangeLimitAlarm * 2 * degreeToPixelsY));


        } else {
            g.setColor(Constants.skyColor);
            g.drawRect(((int) (origonX - (rangeLimitAlarm * degreeToPixelsX))), ((int) (origonY - (rangeLimitAlarm * degreeToPixelsY))), (int) (rangeLimitAlarm * 2 * degreeToPixelsX), (int) (rangeLimitAlarm * 2 * degreeToPixelsY));

        }

        g.setColor(old);
    }

    private void drawFlightDirection(Graphics g) {
        g.setColor(Constants.arrowColor);
        int width = this.getWidth();
        int height = this.getHeight();
        origonX = width / 2;
        origonY = height / 2;
        double xAxesWidth = width - (borderDistance * 2);
        double yAxesWidth = height - (borderDistance * 2);


        double degreeToPixelsX = xAxesWidth / (2 * maxDegree);

        double degreeToPixelsY = yAxesWidth / (2 * maxDegree);



        x = this.origonX + (degreeToPixelsX * zDegree);
        y = this.origonY - (degreeToPixelsY * xDegree);

        // Redraw due to out of range ?
        if (autoAdjustRange == true) {
            if ((xDegree < (maxDegree * -1)) || (xDegree > maxDegree)) {
                maxDegree = ((((int) xDegree) / 10) + 1) * 10;
                repaint();
                return;
            } else if ((zDegree < (maxDegree * -1)) || (zDegree > maxDegree)) {
                maxDegree = ((((int) zDegree) / 10) + 1) * 10;
                repaint();
                return;
            }

        }




        if ((((xDegree < (rangeLimitAlarm * -1)) || (xDegree > rangeLimitAlarm))
                || ((zDegree < (rangeLimitAlarm * -1)) || (zDegree > rangeLimitAlarm))) && (this.rangeLimitAlarmActive == true)) {
            g.setColor(Constants.outOfRangeColor);
        } else {
            g.setColor(Constants.inRangeColor);
        }
        g.drawLine((int) x, (int) y, (int) width / 2, (int) y);
        g.drawLine((int) x, (int) y, (int) x, (int) height / 2);

        g.fillOval((int) (x - circleDiameter / 2), (int) (y - circleDiameter / 2), (int) circleDiameter, (int) circleDiameter);
        g.setColor(Constants.textColor);
        g.setFont(boldFont);
       
        hist[histIndex] = new Point((int) x, (int) y);
        histIndex = (histIndex + 1) % hist.length;

        g.setColor(Constants.textColor);
        String pitchStr = "Pitch";
        int sLength = g.getFontMetrics().stringWidth(pitchStr);
        int sHeight = g.getFontMetrics().getHeight();
        g.drawString(pitchStr, ((int) origonX - sLength / 2), sHeight + 4);



        String yawStr = "Yaw";
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString(yawStr, 5, ((int) origonY) + (textHeight / 4));



    }

    private void drawCoordinateSystem(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        g.setColor(backGroundColor);
        g.fillRect(0, 0, (int) width, (int) height);
        g.setColor(Constants.textColor);
        numberOfDividers = maxDegree / 10 * 2;

        double xAxisWidth = width - (borderDistance * 2);
        double yAxisWidth = height - (borderDistance * 2);

        double xAxisDeltaPixel = xAxisWidth / numberOfDividers / 2;
        double yAxisDeltaPixel = yAxisWidth / numberOfDividers / 2;
        origonX = width / 2;
        origonY = height / 2;

        g.fillRect((int) borderDistance, (int) height / 2 - ((int) lineThickness / 2), (int) xAxisWidth, (int) lineThickness);

        // X
        double xPos = this.origonX + xAxisDeltaPixel;
        for (int i = (int) (maxDegree / numberOfDividers); i <= maxDegree; i += maxDegree / numberOfDividers) {
            g.fillRect((int) xPos, (int) height / 2 - ((int) dividerLength / 2), (int) lineThickness / 2, (int) dividerLength);
            String annotation = "" + i + degreeChar;
            int sLength = g.getFontMetrics().stringWidth(annotation);
            g.drawString(annotation, ((int) xPos) - sLength / 2, (int) height / 2 - ((int) dividerLength));
            xPos += xAxisDeltaPixel;


        }
        xPos = this.origonX - xAxisDeltaPixel;
        for (int i = (int) (maxDegree / numberOfDividers); i <= maxDegree; i += maxDegree / numberOfDividers) {
            g.fillRect((int) xPos, (int) height / 2 - ((int) dividerLength / 2), (int) lineThickness / 2, (int) dividerLength);
            String annotation = "" + (i * -1) + degreeChar;
            int sLength = g.getFontMetrics().stringWidth(annotation);
            g.drawString(annotation, ((int) xPos) - sLength / 2, (int) height / 2 - ((int) dividerLength));
            xPos -= xAxisDeltaPixel;
        }
        // Y
        g.fillRect((int) width / 2 - ((int) lineThickness / 2), (int) borderDistance, (int) lineThickness, (int) yAxisWidth);
        double yPos = this.origonY + yAxisDeltaPixel;
        for (int i = (int) (maxDegree / numberOfDividers); i <= maxDegree; i += maxDegree / numberOfDividers) {
            g.fillRect((int) origonX - ((int) dividerLength / 2), (int) yPos, (int) dividerLength, (int) lineThickness / 2);
            String annotation = "" + (i * -1) + degreeChar;
            int sLength = g.getFontMetrics().stringWidth(annotation);
            g.drawString(annotation, ((int) origonX) - (sLength + (int) dividerLength), (int) yPos + (g.getFontMetrics().getHeight() / 3));
            yPos += yAxisDeltaPixel;


        }

        yPos = this.origonY - yAxisDeltaPixel - lineThickness / 2;
        for (int i = (int) (maxDegree / numberOfDividers); i <= maxDegree; i += maxDegree / numberOfDividers) {
            g.fillRect((int) origonX - ((int) dividerLength / 2), (int) yPos, (int) dividerLength, (int) lineThickness / 2);
            String annotation = "" + i + degreeChar;
            int sLength = g.getFontMetrics().stringWidth(annotation);
            g.drawString(annotation, ((int) origonX) - (sLength + (int) dividerLength), (int) yPos + (g.getFontMetrics().getHeight() / 3));
            yPos -= yAxisDeltaPixel;
        }


    }

    public void setMaxDegree(int d) {
        maxDegree = d;
        repaint();
    }

    public double getMaxDegree() {
        return maxDegree;
    }

    public void setRangeLimitAlarm(int r) {
        rangeLimitAlarm = r;
        repaint();
    }

    public double getRangeLimitAlarm() {
        return rangeLimitAlarm;
    }

    public void setRangeLimitAlarmActive(boolean r) {
        rangeLimitAlarmActive = r;

    }

    public boolean getRangeLimitAlarmActive() {
        return rangeLimitAlarmActive;
    }

    public void setAutoAdjustRange(boolean a) {
        autoAdjustRange = a;

    }

    public boolean getAutoAdjustRange() {
        return autoAdjustRange;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setName("Form"); // NOI18N
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    }//GEN-LAST:event_formMouseClicked
    private boolean startRecording = false;

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
    }//GEN-LAST:event_formMouseMoved
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
