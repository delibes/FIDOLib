/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author Steen Andersen
 */
public class RocketInfo {

    /**
     * The latitude in decimal degrees
     */
    public double lat = 0.0;
    /**
     * The longitude in decimal degrees
     */
    public double lon = 0.0;
    /**
     * GPS altitude in meters
     */
    public int GPSAltitude = 0;
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
     * Good latitude reading?
     */
    public boolean latitudeGood = false;
    /**
     * Good longitude reading?
     */
    public boolean longitudeGood = false;
    /**
     * The latitude in pixels
     */
    public int latPixels = 0;
    /**
     * The longitude in pixels
     */
    public int lonPixels = 0;
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
     * Decimal symbols
     */
    private static DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
    /**
     * Formatting  
     */
    private DecimalFormat df = new DecimalFormat("", decimalSymbols);
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
    public RocketInfo(double lat, double lon, int GPSAltitude) {
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        this.lat = lat;
        this.lon = lon;
        this.GPSAltitude = GPSAltitude;


    }

    /**
     * Constructor
     */
    public RocketInfo(RocketInfo aPosition) {
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        this.lat = aPosition.lat;
        this.lon = aPosition.lon;
        this.latitudeGood = aPosition.latitudeGood;
        this.longitudeGood = aPosition.longitudeGood;
        this.GPSAltitude = aPosition.GPSAltitude;
        this.COG = aPosition.COG;
        this.GPSFix = aPosition.GPSFix;
        this.MCUGPSFixTime = aPosition.MCUGPSFixTime;
        this.downRange = aPosition.downRange;
        this.etaPosition = aPosition.etaPosition;
        this.velocity = aPosition.velocity;
        this.horizontalVelocity = aPosition.horizontalVelocity;
        this.verticalVelocity = aPosition.verticalVelocity;
        this.flying = aPosition.flying;

    }

    /**
     * Greate circle distance in nautical miles between p1 and p2 using The haversine formula
     * @param p1 the first point
     * @param p2 the second point
     * @return
     */
    public static double disanceNauticalMiles(RocketInfo p1, RocketInfo p2) {
        if ((p1 == null) || (p2 == null)) {
            return 0.0;
        }
        if ((p1.lat == 0.0) || (p1.lon == 0.0) || (p2.lat == 0.0) || (p2.lat == 0.0)) {
            return 0.0;
        }
        // Use radians
        double lat1 = p1.lat * Math.PI / 180;
        double lon1 = p1.lon * Math.PI / 180;
        double lat2 = p2.lat * Math.PI / 180;
        double lon2 = p2.lon * Math.PI / 180;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow((Math.sin((lat1 - lat2) / 2)), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow((Math.sin((lon1 - lon2) / 2)), 2))); // Distance in radians
        distance = distance * 180 * 60 / Math.PI;
        return distance;
    }

    /**
     * Calculate the initial bearing from point p1 to p2 along the greate circle path used in the image
     */
    public static int initialBearingImg(RocketInfo p1, RocketInfo p2) {
        if ((p1 == null) || (p2 == null)) {
            return -1;
        }
        if ((p1.lat == 0.0) || (p1.lon == 0.0) || (p2.lat == 0.0) || (p2.lat == 0.0)) {
            return -1;
        }
        // Use radians
        double lat1 = p1.lat * Math.PI / 180;
        double lon1 = p1.lon * Math.PI / 180;
        double lat2 = p2.lat * Math.PI / 180;
        double lon2 = p2.lon * Math.PI / 180;
        // double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2)
                - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.atan2(y, x) / Math.PI * 180;
        int brg = (360 - (int) (bearing + 90 + 360) % 360);
        return (brg == 360) ? 0 : brg;
        
    }

    /**
     * Calculate the initial bearing from point p1 to p2 along the greate circle path 
     */
    public static int initialBearing(RocketInfo p1, RocketInfo p2) {
        if ((p1 == null) || (p2 == null)) {
            return -1;
        }
        if ((p1.lat == 0.0) || (p1.lon == 0.0) || (p2.lat == 0.0) || (p2.lat == 0.0)) {
            return -1;
        }
        // Use radians
        double lat1 = p1.lat * Math.PI / 180;
        double lon1 = p1.lon * Math.PI / 180;
        double lat2 = p2.lat * Math.PI / 180;
        double lon2 = p2.lon * Math.PI / 180;
        // double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2)
                - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.atan2(y, x) / Math.PI * 180;
        int brg = (int)bearing;
        if (brg < 0)
        {
            brg = 360 + brg; 
        }
        return (brg == 360) ? 0 : brg;
    }
    /**
     * Return initial bearing as string
     */
     public static String initialBearingAsString(RocketInfo p1, RocketInfo p2) {
         int brg = initialBearing(p1,p2);
         if (brg < 0)
         {
             return Constants.naString;
         }
         else {
             return "" + brg;
         }
         
     }
    
    /**
     * Caluculate the new position given x and y meters from the original
     * including the MLP heading
     * @param p the position to be adjusted
     * @param x the x distance in meters
     * @param y the y distance in meters
     */
    public static void latLonRadialDistance(RocketInfo p1, double x, double y) {
        double lat, lon = 0.0;
        double lat1 = p1.lat * Math.PI / 180;
        double lon1 = p1.lon * Math.PI / 180;

        double tc = (Math.atan2(-1.0 * x, y)) % (2.0 * Math.PI); // True course
        double d = Math.sqrt((x * x) + (y * y)) / Constants.nauticalMile; // distance in nautical miles
        d = d * Math.PI / (180.0 * 60.0); // Distance in radians
        lat = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(tc));
        if (Math.cos(lat) == 0) {
            lon = p1.lon;
        } else {
            lon = mod(lon1 - Math.asin(Math.sin(tc) * Math.sin(d) / Math.cos(lat)) + Math.PI, 2 * Math.PI) - Math.PI;
        }
        p1.lat = (180 / Math.PI) * lat;
        p1.lon = (180 / Math.PI) * lon * -1;

    }

    /**
     * The modulus function with the sign following the divisor
     */
    public static double mod(double x, double y) {
        double mod = 0.0;
        mod = y - x * Math.floor(y / x);

        return mod;
    }

    /**
     * Format degrees
     */
    public static String formatDegrees(double degree) {

        if (Constants.degreeFormat == Constants.DegreeFormat.DECIMAL) {
            DecimalFormat df = new DecimalFormat("00.000000", decimalSymbols);
            return df.format(degree) + Constants.degreeChar;
        } else if (Constants.degreeFormat == Constants.DegreeFormat.DECIMALMINUTE) {
            int d = (int) degree;
            double m = (degree - d) * 60;
            DecimalFormat df = new DecimalFormat("00.0000", decimalSymbols);
            return "" + d + Constants.degreeChar + " " + df.format(m) + "'";
        } else if (Constants.degreeFormat == Constants.DegreeFormat.DECIMALMINSEC) {
            int d = (int) degree;
            double m = (degree - d) * 60;
            double s = (m - Math.floor(m)) * 60;
            DecimalFormat dfmin = new DecimalFormat("00", decimalSymbols);
            DecimalFormat df = new DecimalFormat("00.000", decimalSymbols);

            return "" + d + Constants.degreeChar + " " + dfmin.format(m) + "' " + df.format(s) + "\"";
        } else {
            return "";
        }
    }

    /**
     * Calculate the latitude in pixels
     * @param p the position p in lat/lon
     */
    public static void calcLatLonPixels(RocketInfo p, int width, int height) {

        //TODO: remove before flight
       p.latPixels = ((int) (((p.lat - 0.5 - Constants.upperLeftCornerLat) / (Constants.lowerRightCornerLat - Constants.upperLeftCornerLat)) * height));
       p.lonPixels = ((int) ((p.lon + 3.0 - Constants.upperLeftCornerLon) / (Constants.lowerRightCornerLon - Constants.upperLeftCornerLon) * width));
//
//        p.latPixels = ((int) (((p.lat - Constants.upperLeftCornerLat) / (Constants.lowerRightCornerLat - Constants.upperLeftCornerLat)) * height));
//        p.lonPixels = ((int) ((p.lon - Constants.upperLeftCornerLon) / (Constants.lowerRightCornerLon - Constants.upperLeftCornerLon) * width));

    }

    /**
     * Return latitude as string according to the format
     */
    public String getLat() {
        String latStr = Constants.northSouth + " " + formatDegrees(lat);
        if (latitudeGood != true) {

            latStr = "(" + latStr +")";
        }
        return latStr;
    }

    /**
     * Return longitude as string according to the format
     */
    public String getLon() {
        String lonStr = Constants.eastWest + " " + formatDegrees(lon);
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
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
          return "" + GPSAltitude + " m";   
        }
        else {
            return Constants.naString;
        }
    }
    public String getDownrange() {
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
          return "" + ((int)downRange) + " m";   
        }
        else {
            return Constants.naString;
        }
    }
     public String getCOG() {
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
          return "" + COG + Constants.degreeChar;   
        }
        else {
            return Constants.naString;
        }
    }
      public String getVelocity() {
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
          return "" + ((int)velocity) + " m/s";   
        }
        else {
            return Constants.naString;
        }
    }
       public String getHorizontalVelocity() {
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
          return "" + ((int)horizontalVelocity) + " m/s";   
        }
        else {
            return Constants.naString;
        }
    }
       public String getVerticalVelocity() {
        if (FlightData.getInstance().lastValidDataTimeStamp > 0) {
          return "" + ((int)verticalVelocity) + " m/s";   
        }
        else {
            return Constants.naString;
        }
    }
}
