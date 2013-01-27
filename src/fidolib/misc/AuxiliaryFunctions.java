/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.misc;

import fidolib.data.Constants;
import fidolib.data.CountDownData;
import fidolib.data.Position;
import fidolib.data.RocketInfo;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Steen
 */
public class AuxiliaryFunctions {

    /**
     * Self reference
     */
    private static volatile AuxiliaryFunctions aAuxiliaryFunctions = null;
    /**
     * Decimal symbols
     */
    private static DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));

    /**
     * Enum for selecting little/big endian
     */
    public static enum Endian {

        LITTLE, BIG
    };

    /**
     * Constructor
     */
    public AuxiliaryFunctions() {
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');

    }

    /**
     * Get instance
     */
    public static AuxiliaryFunctions getInstance() {

        if (aAuxiliaryFunctions == null) {
            aAuxiliaryFunctions = new AuxiliaryFunctions();
        }
        return aAuxiliaryFunctions;

    }

    /**
     * Is the check sum correct given the packet
     * @param packet the packet
     * @param endian using little or big
     * @return true or false
     */
    public static boolean checkSum(byte[] packet, int csIndex, Endian endian) {
        // unsigned sum af bytes 0..packet.length-4
        int sum = 0;
        if ((packet == null)) {
            return false;
        }
        for (int i = 0; i < csIndex; i++) {
            sum = sum + (int) ((packet[i] & 0xff));

        }
        int checkSum = byteArrayToINT16(packet, csIndex, endian);
        // System.out.println("" + sum + " " + checkSum);
        return (checkSum == sum);
    }

    /**
     * Convert the byte array to an int starting from the given offset using
     * little/big endian. Unsigned INT16
     *
     * @param packet The byte array
     * @param offset The array offset
     * @param endian using little or big
     * @return The int
     */
    public static int byteArrayToUINT16(byte[] packet, int offset, Endian endian) {
        int value;
        if (endian == Endian.LITTLE) {
            value = packet[offset] & 0x000000FF;
            value += (packet[offset + 1] & 0x000000FF) << 8;
            return value;
        } else {
            value = packet[offset + 1] & 0x000000FF;
            value += (packet[offset] & 0x000000FF) << 8;
            return value;
        }

    }

    /**
     * Convert the byte array to an int starting from the given offset using
     * little/big endian. Signed INT16
     *
     * @param packet The byte array
     * @param offset The array offset
     * @param endian using little or big
     * @return The int
     */
    public static int byteArrayToINT16(byte[] packet, int offset, Endian endian) {
        int value;
        if (endian == Endian.LITTLE) {
            value = packet[offset] & 0x000000FF;
            value += (packet[offset + 1] & 0x000000FF) << 8;
            if (value > (Short.MAX_VALUE)) {
                value = value - (Short.MAX_VALUE * 2 + 2);
            }
            return value;
        } else {
            value = packet[offset + 1] & 0x000000FF;
            value += (packet[offset] & 0x000000FF) << 8;
            if (value > (Short.MAX_VALUE)) {
                value = value - (Short.MAX_VALUE * 2 + 2);
            }
            return value;

        }

    }

    /**
     * Convert the byte array to an double starting from the given offset using
     * little/big endian. 
     *
     * @param packet The byte array
     * @param offset The array offset
     * @param endian using little or big
     * @return The double
     */
    public static double byteArrayToDouble(byte[] packet, int offset, Endian endian) {
        int value;
        if (endian == Endian.LITTLE) {
            value = packet[offset] & 0x000000FF;
            value += (packet[offset + 1] & 0x000000FF) << 8;
            value += (packet[offset + 2] & 0x000000FF) << 16;
            value += (packet[offset + 3] & 0x000000FF) << 24;
            return (double) value;
        } else {
            value = packet[offset + 3] & 0x000000FF;
            value += (packet[offset + 2] & 0x000000FF) << 8;
            value += (packet[offset + 1] & 0x000000FF) << 16;
            value += (packet[offset] & 0x000000FF) << 24;
            return (double) value;
        }

    }

    /**
     * Convert the byte array to an INT32 starting from the given offset using
     * little/big endian. 
     *
     * @param packet The byte array
     * @param offset The array offset
     * @param endian using little or big
     * @return The INT32
     */
    public static int byteArrayToINT32(byte[] packet, int offset, Endian endian) {
        int value;
        if (endian == Endian.LITTLE) {
            value = packet[offset] & 0x000000FF;
            value += (packet[offset + 1] & 0x000000FF) << 8;
            value += (packet[offset + 2] & 0x000000FF) << 16;
            value += (packet[offset + 3] & 0x000000FF) << 24;
            return value;
        } else {
            value = packet[offset + 3] & 0x000000FF;
            value += (packet[offset + 2] & 0x000000FF) << 8;
            value += (packet[offset + 1] & 0x000000FF) << 16;
            value += (packet[offset] & 0x000000FF) << 24;
            return value;
        }


    }

    /**
     * Convert the byte array to an int starting from the given offset.
     *
     * @param b The byte array
     * @param offset The array offset
     * @return The integer
     */
    /*public static int byteArrayToInt(byte[] b, int offset, int numBytes) {
    int value = 0;
    for (int i = 0; i < numBytes; i++) {
    int shift = (numBytes - 1 - i) * 8;
    value += (b[i + offset] & 0x000000FF) << shift;
    }
    return value;
    }*/
    /**
     * Convert a 'long' to count down time stamp
     * @param countDownTime
     * @return the count down clock as string
     */
    public static String long2StringTime(long countDownTime) {
        String time;
        // DecimalFormat df = new DecimalFormat("00", decimalSymbols);

        if (countDownTime > 0) {
            time = "T-";
        } else {
            time = "T+";
            // Add a sec in order not to have 2 seconds around T-0
            countDownTime -= 1000;
        }
        long hours = (countDownTime / CountDownData.hour);
        hours = Math.abs(hours);
        if (hours > 0) {
            if (hours <= 9) {
                time += "0" + hours + ":";
            } else {
                time += "" + hours + ":";
            }

        }

        long min = (countDownTime % CountDownData.hour) / CountDownData.minute;
        min = Math.abs(min);
        if (min <= 9) {
            time += "0" + min;
        } else {
            time += "" + min;
        }
        long sec = ((countDownTime % CountDownData.minute) / CountDownData.sec);
        sec = Math.abs(sec);
        if (sec <= 9) {
            time += ":0" + sec;
        } else {
            time += ":" + sec;
        }
        return time;
    }

    /**
     * Return the real time as a string
     * @return the time stamp
     */
    public static String getTimeStamp() {
        String s;
        Format formatter;
        Calendar calender = Calendar.getInstance();
        long now = calender.getTime().getTime();
        formatter = new SimpleDateFormat("HH.mm.ss");
        s = formatter.format(now);
        return s;
    }

    /**
     * Greate circle distance in nautical miles between p1 and p2 using The haversine formula
     * @param p1 the first point
     * @param p2 the second point
     * @return
     */
    public static double disanceNauticalMiles(Position p1, Position p2) {
        if ((p1 == null) || (p2 == null)) {
            return 0.0;
        }
        if ((p1.lat == 0.0) || (p1.lon == 0.0) || (p2.lat == 0.0) || (p2.lon == 0.0)) {
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
    public static int initialBearingImg(Position p1, Position p2) {
        if ((p1 == null) || (p2 == null)) {
            return -1;
        }
        if ((p1.lat == 0.0) || (p1.lon == 0.0) || (p2.lat == 0.0) || (p2.lon == 0.0)) {
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
    public static int initialBearing(Position p1, Position p2) {
        if ((p1 == null) || (p2 == null)) {
            return -1;
        }
        if ((p1.lat == 0.0) || (p1.lon == 0.0) || (p2.lat == 0.0) || (p2.lon == 0.0)) {
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
        int brg = (int) bearing;
        if (brg < 0) {
            brg = 360 + brg;
        }
        return (brg == 360) ? 0 : brg;
    }

    /**
     * Return initial bearing as string
     */
    public static String initialBearingAsString(RocketInfo p1, RocketInfo p2) {
        int brg = initialBearing(p1, p2);
        if (brg < 0) {
            return Constants.naString;
        } else {
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
    public static void latLonRadialDistance(Position p1, double x, double y) {
        double lat;
        double lon;
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
        return (y - x * Math.floor(y / x));
    }

    /**
     * Format degrees
     */
    public String formatDegrees(double degree) {

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
    public static void calcLatLonPixels(Position p, int width, int height) {

        //TODO: remove before flight
 //       p.latPixels = ((int) (((p.lat - 0.5 - Constants.upperLeftCornerLat) / (Constants.lowerRightCornerLat - Constants.upperLeftCornerLat)) * height));
 //       p.lonPixels = ((int) ((p.lon + 3.0 - Constants.upperLeftCornerLon) / (Constants.lowerRightCornerLon - Constants.upperLeftCornerLon) * width));
//
       p.latPixels = ((int) (((p.lat - Constants.upperLeftCornerLat) / (Constants.lowerRightCornerLat - Constants.upperLeftCornerLat)) * height));
        p.lonPixels = ((int) ((p.lon - Constants.upperLeftCornerLon) / (Constants.lowerRightCornerLon - Constants.upperLeftCornerLon) * width));

    }
}
