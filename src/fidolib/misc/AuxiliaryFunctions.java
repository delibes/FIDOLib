/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.misc;

import fidolib.data.CountDownData;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Steen
 */
public class AuxiliaryFunctions {
     /**
     * Self reference
     */
    private static AuxiliaryFunctions aAuxiliaryFunctions = null;
    
    /**
     * Constructor
     */
    public AuxiliaryFunctions()
    {
       
    }
    
    /**
     * Get instance
     */
    public static AuxiliaryFunctions getInstance()
    {

        if (aAuxiliaryFunctions == null)
        {
            aAuxiliaryFunctions = new AuxiliaryFunctions();
        }
        return aAuxiliaryFunctions;
        
    }
    
    /**
     * Is the check sum correct given the packet
     * @param packet the packet
     * @return true or false
     */
    public static boolean checkSum(byte[] packet) {
        // unsigned sum af bytes 0..packet.length-4
        int sum = 0;
        if ((packet == null)) {
            return false;
        }
        for (int i = 0; i < packet.length-4; i++) {
            sum = sum + (int)((packet[i] & 0xff));
            
        }
        int checkSum = byteArrayToINT16(packet, packet.length-4);
        return (checkSum == sum);
    }

    /**
     * Convert the byte array to an int starting from the given offset using
     * little endian. Unsigned INT16
     *
     * @param packet The byte array
     * @param offset The array offset
     * @return The int
     */
    public static int byteArrayToUINT16(byte[] packet, int offset) {
        int value = 0;
        value = packet[offset] & 0x000000FF;
        value += (packet[offset + 1] & 0x000000FF) << 8;
        return value;
    }

    /**
     * Convert the byte array to an int starting from the given offset using
     * little endian. Signed INT16
     *
     * @param packet The byte array
     * @param offset The array offset
     * @return The int
     */
    public static int byteArrayToINT16(byte[] packet, int offset) {
        int value = 0;
        // TODO: check for array out of range
        value = packet[offset] & 0x000000FF;
        value += (packet[offset + 1] & 0x000000FF) << 8;
        if (value > (Short.MAX_VALUE)) {
            value = value - (Short.MAX_VALUE * 2 + 2);
        }
        return value;

    }
     /**
     * Convert the byte array to an double starting from the given offset using
     * little endian. 
     *
     * @param packet The byte array
     * @param offset The array offset
     * @return The double
     */
    public static double byteArrayToDouble(byte[] packet, int offset) {
        int value = 0;
        value = packet[offset] & 0x000000FF;
        value += (packet[offset + 1] & 0x000000FF) << 8;
        value += (packet[offset + 2] & 0x000000FF) << 16;
        value += (packet[offset + 3] & 0x000000FF) << 24;
        
        
        return (double)value;

    }
/**
     * Convert the byte array to an INT32 starting from the given offset using
     * little endian. 
     *
     * @param packet The byte array
     * @param offset The array offset
     * @return The INT32
     */
    public static int byteArrayToINT32(byte[] packet, int offset) {
        int value = 0;
        value = packet[offset] & 0x000000FF;
        value += (packet[offset + 1] & 0x000000FF) << 8;
        value += (packet[offset + 2] & 0x000000FF) << 16;
        value += (packet[offset + 3] & 0x000000FF) << 24;
        
        
        return value;

    }

    /**
     * Convert the byte array to an int starting from the given offset.
     *
     * @param b The byte array
     * @param offset The array offset
     * @return The integer
     */
    public static int byteArrayToInt(byte[] b, int offset, int numBytes) {
        int value = 0;
        for (int i = 0; i < numBytes; i++) {
            int shift = (numBytes - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * Convert a 'long' to count down time stamp
     * @param countDownTime
     * @return the count down clock as string
     */
    public static String long2StringTime(long countDownTime) {
        String time = "";


        if (countDownTime > 0) {
            time = "T-";
        } else {
            time = "T+";
            // Add a sec in order not to have 2 seconds around T-0
            countDownTime -= 1000;
        }
        long min = (countDownTime / CountDownData.minute);
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
    
    
}
