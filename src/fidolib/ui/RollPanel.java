/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RollPanel.java
 *
 * Created on 31-08-2010, 19:11:55
 */
package fidolib.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.Date;
import fidolib.data.Constants;

/**
 *
 * @author Steen Andersen
 */
public class RollPanel extends javax.swing.JPanel  {

    /**
     * Distance between the border of the circle and the border of the panel.
     */
    private int circleBorderWidth = 40;
    /**
     * Distance between the border of the circle and the border of the panel.
     */
    private int circleBorderHeight = 40;
    /**
     * Number of degrees between the graphical marks.
     */
    private int degreeMark = 45;
   
    /**
     * The degree character
     */
    private char degreeChar = 186;
    
    /**
     * The line thickness
     */
    private int lineThickness = 2;
    /**
     * The origon of the panel
     */
    private Point origon = new Point();
    /**
     * Start point of the degree lines
     */
    private Point lineStartPoint = new Point();
    /**
     * End point of the degree lines
     */
    private Point lineEndPoint = new Point();
    /**
     * Degree number point
     */
    private Point degreeNumberPoint = new Point();
    /**
     * Degrees in 360
     */
    private int rollDegree = 0;
    /**
     * Degrees in 360
     */
    private int rollDegreeOld = 0;
    /**
     * Last measurement of degres
     */
    private Date lastMeasurement = null;
    /**
     * Roll angle velocity per second.
     */
    private double rollAngleVelocity = 0.0;
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
     * Delta degrees between rad and degree
     */
    private int deltaDegree = -90;
    /**
     * Roll angle string
     */
    private String rollAngleStr = " Roll";
    /**
     * Roll angle velocity string
     */
    private String rollAngleVelocityStr = " RAV";
    /**
     * Show 0..360 degrees or -180..180 degrees?
     */
    private boolean degree180 = false;
    private Font boldFont = Constants.boldFont;

    //  private IMUThread aRocketThread = new IMUThread( this);
    /** Creates new form RollPanel */
    public RollPanel() {
        initComponents();


    }

    @Override
    public void paint(Graphics g) {
        paintCompass(g);
        for (int i = 0; i < 4; i++) {
            paintDirection(g, 90 * i);
        }
    }

    public void paintDirection(Graphics g, int degreesShift) {
        int width = this.getWidth();
        int height = this.getHeight();
        int ovalWidth = width - circleBorderWidth * 2;
        int ovalHeight = height - circleBorderHeight * 2;
        double arrowSpike = (rollDegree + degreesShift + deltaDegree) * Math.PI / 180;
        double arrowRight = (((rollDegree + degreesShift + deltaDegree) + 270) % 360) * Math.PI / 180;
        double arrowLeft = (((rollDegree + degreesShift + deltaDegree) + 90) % 360) * Math.PI / 180;
        double arrowBack = (((rollDegree + degreesShift + deltaDegree) + 180) % 360) * Math.PI / 180;

        xPoints[0] = (int) (Math.cos(arrowSpike) * ovalWidth / 2.3) + origon.x;
        yPoints[0] = (int) (Math.sin(arrowSpike) * ovalHeight / 2.3) + origon.y;


        xPoints[1] = (int) (Math.cos(arrowRight) * ovalWidth / arrayWidthFraction) + origon.x;
        yPoints[1] = (int) (Math.sin(arrowRight) * ovalHeight / arrayWidthFraction) + origon.y;

        xPoints[2] = (int) (Math.cos(arrowBack) * ovalWidth / 4) + origon.x;
        yPoints[2] = (int) (Math.sin(arrowBack) * ovalHeight / 4) + origon.y;


        xPoints[3] = (int) (Math.cos(arrowLeft) * ovalWidth / arrayWidthFraction) + origon.x;
        yPoints[3] = (int) (Math.sin(arrowLeft) * ovalHeight / arrayWidthFraction) + origon.y;
        g.setColor(Constants.arrowColor);
        g.fillPolygon(xPoints, yPoints, nPolygon);
        g.setColor(Constants.backGroundColor);

    }

    /**
     * Paint the back ground compass
     * @param g
     */
    public void paintCompass(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(lineThickness));

        int width = this.getWidth();
        int height = this.getHeight();
        circleBorderWidth = width * 7 / 100;
        circleBorderHeight = height * 7 / 100;
        g2.setColor(Constants.backGroundColor);
        g2.fillRect(0, 0, width, height);

        // g.setFont(plainFont);


        g2.setColor(Constants.textColor);
        origon.x = width / 2;
        origon.y = height / 2;
        int ovalWidth = width - circleBorderWidth * 2;
        int ovalHeight = height - circleBorderHeight * 2;

        int circleDistance = 1;
        // GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(130, 130, 255), new Point(), new Color(130, 255, 255));
        GradientPaint gp = new GradientPaint(width / 2, circleBorderHeight, Constants.gradientStartColor, width / 2, height - circleBorderHeight, Constants.gradientEndColor, true);
        Paint p = g2.getPaint();
        g2.setPaint(gp);
        // g2.setColor(Constants.rollMeterColor);
        g2.fillOval(circleBorderWidth, circleBorderHeight, ovalWidth, ovalHeight);
        g2.setPaint(p);
        g2.setColor(Constants.textColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(circleBorderWidth - circleDistance, circleBorderHeight - circleDistance, ovalWidth + circleDistance * 2, ovalHeight + circleDistance * 2);
        g2.setStroke(new BasicStroke(lineThickness));
        g2.setColor(Constants.textColor);
        g.setFont(boldFont);
        int stringBorderX = 10;
        int stringBorderY = 20;
        if (degree180 == true && (rollDegree > 180)) {
            g2.drawString("" + (rollDegree - 360) + degreeChar + rollAngleStr, stringBorderX, stringBorderY);
        } else {
            g2.drawString("" + rollDegree + degreeChar + rollAngleStr, width / 2, stringBorderY - 5);
        }
        String ravS = "" + Math.abs(((int) (rollAngleVelocity))) + degreeChar + "/s" + rollAngleVelocityStr;
        int sLength = g2.getFontMetrics().stringWidth(ravS);
        g2.drawString(ravS, stringBorderX, height - stringBorderY + 6);

        String ravHz = "" + Math.abs(((int) (rollAngleVelocity * 10 / 360)) / 10.0) + " Hz" + rollAngleVelocityStr;
        sLength = g2.getFontMetrics().stringWidth(ravHz);
        g2.drawString(ravHz, width - sLength - stringBorderX, height - stringBorderY + 6);

        int iStart = 0;
        int iEnd = 360;
        if (degree180 == true) {
            iStart = -270;
            iEnd = 90;
        }
        for (int i = iStart; i < iEnd; i++) {
            if (i % degreeMark == 0) // draw big ticks
            {
                double rad = i * Math.PI / 180;
                g2.setStroke(new BasicStroke(lineThickness));
                lineStartPoint.x = (int) (Math.cos(rad) * ovalWidth / 2.3) + origon.x;
                lineStartPoint.y = (int) (Math.sin(rad) * ovalHeight / 2.3) + origon.y;
                lineEndPoint.x = (int) (Math.cos(rad) * ovalWidth / 2.1) + origon.x;
                lineEndPoint.y = (int) (Math.sin(rad) * ovalHeight / 2.1) + origon.y;
                g2.drawLine(lineStartPoint.x, lineStartPoint.y, lineEndPoint.x, lineEndPoint.y);
                // Draw degree number
                degreeNumberPoint.x = (int) (Math.cos(rad) * ovalWidth / 2.7) + origon.x;
                degreeNumberPoint.y = (int) (Math.sin(rad) * ovalHeight / 2.7) + origon.y;
                String degreeStr = "" + ((i + 90) % 360);
                if (degree180 == true) {
                    if (degreeStr.equals("-180") == true) {
                        degreeStr = "180";
                    }

                }
                int strLength = g.getFontMetrics().stringWidth(degreeStr);
                int strHeight = g.getFontMetrics().getHeight();
                g2.drawString(degreeStr, degreeNumberPoint.x - strLength / 2, degreeNumberPoint.y + strHeight / 4);

            } else if (i % (5) == 0)// draw small ticks
            {
                double rad = i * Math.PI / 180;
                g2.setStroke(new BasicStroke(lineThickness / 2));
                lineStartPoint.x = (int) (Math.cos(rad) * ovalWidth / 2.2) + origon.x;
                lineStartPoint.y = (int) (Math.sin(rad) * ovalHeight / 2.2) + origon.y;
                lineEndPoint.x = (int) (Math.cos(rad) * ovalWidth / 2.1) + origon.x;
                lineEndPoint.y = (int) (Math.sin(rad) * ovalHeight / 2.1) + origon.y;
                g2.drawLine(lineStartPoint.x, lineStartPoint.y, lineEndPoint.x, lineEndPoint.y);

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
