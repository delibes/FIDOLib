/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import fidolib.misc.AuxiliaryFunctions;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author Steen Andersen
 */
public class RocketInfo extends Position {

    /** 
     * Time stamp of last data package
     */
    public long lastValidDataTimeStamp = 0;
    
    /**
     * Course over ground
     */
    public int COG = 0;
    /**
     * GPS Fix  (1 = no fix, 2 = 2d fix and 3 = 3d fix)
     */
    public int GPSFix = 1;
    /**
     * Down range from launch point in meter
     */
    public double downRange = 0;
    /**
     * Vertical velocity in m/s from privious point 
     */
    public double verticalVelocity = 0;
    /**
     * Horizontal velocity in m/s from privious point 
     */
    public double horizontalVelocity = 0;
    /**
     * Velocity in m/s from privious point
     */
    public double velocity = 0.0;
    /**
     * ETA of impact in seconds calculated if vertical velocity < 0;
     */
    public int ETA = -1;
    /**
     * Are the rocket flying?
     */
    public boolean flying = false;
    /**
     * GPS time stamp at MCU (milli s) 
     */
    public int MCUGPSFixTime = 0;
    /**
     * Acc X
     */
    public int accX = 0;
    /**
     * Acc Y
     */
    public int accY = 0;
    /**
     * Acc Z
     */
    public int accZ = 0;
    /**
     * Acc time stamp
     */
    public int accTime = 0;
    /**
     * Gyro X
     */
    public int gyroX = 0;
    /**
     * Gyro Y
     */
    public int gyroY = 0;
    /**
     * Gyro Z
     */
    public int gyroZ = 0;
    /**
     * Gyro time stamp
     */
    public int gyroTime = 0;
    /**
     * Decimal symbols
     */
    private static DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));

    
    private RocketInfo etaPosition = null;

    /**
     * Constructor
     */
    public RocketInfo() {
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');

    }

    /**
     * Constructor
     */
    public RocketInfo(double lat, double lon, int GPSAltitude, long lastValidDataTimeStamp) {
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        this.lat = lat;
        this.lon = lon;
        this.GPSAltitude = GPSAltitude;
        this.lastValidDataTimeStamp = lastValidDataTimeStamp;


    }

    /**
     * Constructor
     */
    public RocketInfo(RocketInfo aRocketInfo) {
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        this.lat = aRocketInfo.lat;
        this.lon = aRocketInfo.lon;
        this.latitudeGood = aRocketInfo.latitudeGood;
        this.longitudeGood = aRocketInfo.longitudeGood;
        this.GPSAltitude = aRocketInfo.GPSAltitude;
        this.COG = aRocketInfo.COG;
        this.GPSFix = aRocketInfo.GPSFix;
        this.MCUGPSFixTime = aRocketInfo.MCUGPSFixTime;
        this.downRange = aRocketInfo.downRange;
        this.etaPosition = aRocketInfo.etaPosition;
        this.velocity = aRocketInfo.velocity;
        this.horizontalVelocity = aRocketInfo.horizontalVelocity;
        this.verticalVelocity = aRocketInfo.verticalVelocity;
        this.flying = aRocketInfo.flying;
        this.lastValidDataTimeStamp = aRocketInfo.lastValidDataTimeStamp;

    }
    /**
     * Return latitude as string according to the format
     */
    public String getLat() {
        String latStr = Constants.northSouth + " " + AuxiliaryFunctions.getInstance().formatDegrees(lat);
        if (latitudeGood != true) {

            latStr = "(" + latStr +")";
        }
        return latStr;
    }

    /**
     * Return longitude as string according to the format
     */
    public String getLon() {
        String lonStr = Constants.eastWest + " " + AuxiliaryFunctions.getInstance().formatDegrees(lon);
        if (longitudeGood != true) {

            lonStr = "(" + lonStr +")";
        }
        return lonStr; 
    }

    /**
     * Return the GPS fix s string
     */
    public String getGPSFix() {
        switch (GPSFix) {
            case 1:
                return Constants.naString;
            case 2:
                return "2D";
            case 3:
                return "3D";
            default:
                return Constants.naString;
        }
    }
    public String getAltitude() {
        if (lastValidDataTimeStamp > 0) {
          return "" + GPSAltitude + " m";   
        }
        else {
            return Constants.naString;
        }
    }
    public String getDownrange() {
        if (lastValidDataTimeStamp > 0) {
          return "" + ((int)downRange) + " m";   
        }
        else {
            return Constants.naString;
        }
    }
     public String getCOG() {
        if (lastValidDataTimeStamp > 0) {
          return "" + COG + Constants.degreeChar;   
        }
        else {
            return Constants.naString;
        }
    }
      public String getVelocity() {
        if (lastValidDataTimeStamp > 0) {
          return "" + ((int)velocity) + " m/s";   
        }
        else {
            return Constants.naString;
        }
    }
       public String getHorizontalVelocity() {
        if (lastValidDataTimeStamp > 0) {
          return "" + ((int)horizontalVelocity) + " m/s";   
        }
        else {
            return Constants.naString;
        }
    }
       public String getVerticalVelocity() {
        if (lastValidDataTimeStamp > 0) {
          return "" + ((int)verticalVelocity) + " m/s";   
        }
        else {
            return Constants.naString;
        }
    }
       /**
        * Return the acceleration in m/^2
        * @return 
        */
       public String getAccX()
       {
           DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);

           return "" + df.format(((double)accX / 1000)) + " " + Constants.gTerm;
       }
       /**
        * Return the acceleration in m/^2
        * @return 
        */
       public String getAccY()
       {
           DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
           return "" + df.format(((double)accY / 1000)) + " " + Constants.gTerm;
       }
       /**
        * Return the acceleration in m/^2
        * @return 
        */
       public String getAccZ()
       {
           DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
           return "" + df.format(((double)accZ / 1000 )) + " " + Constants.gTerm;
       }
}
