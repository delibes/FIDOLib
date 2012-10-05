/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import fidolib.misc.AuxiliaryFunctions;
import fidolib.log.DataLog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Steen Andersen
 */
public class FlightData {

    /**
     * Self reference
     */
    private static FlightData aFlightData = null;

    /** 
     * Time stamp of last data package
     */
    public long lastValidDataTimeStamp = 0;
    /**
     * Last data received
     */
    public String data = null;
    
    /**
     * Barometer based altitude
     */
    public int barometerAltitude = 0;
    /** MC time stamp for the barometer altitude calc
     * 
     */
    public int realAltitudeTime = 0;
    /**
     * Number of good packets
     */
    public int noGoodPackets = 0;
    /**
     * Number of bad packets
     */
    public int noBadPackets = 0;
    /**
     * Are Tycho flying?
     */
    public boolean flying = false;
    /**
     * The latitude and longitude position of the rocket
     */
    public Position rocketPosition = new Position();
    /**
     * The position if the rocket at lift off
     */
    public Position liftOffPosition = new Position();
    /**
     * 
     */
    public ArrayList<Position> arrayList = new ArrayList<Position>();
    /**
     * List of positions after lift off
     */
    public List positions = Collections.synchronizedList(arrayList);

    /**
     * Constructor
     */
    public FlightData() {
        Calendar calender = Calendar.getInstance();
        lastValidDataTimeStamp = calender.getTime().getTime();
    }

    /**
     * Get instance
     */
    public static FlightData getInstance() {

        if (aFlightData == null) {
            aFlightData = new FlightData();
        }
        return aFlightData;

    }

    public void setData(String dataArg) {
        data = dataArg;
        Calendar calender = Calendar.getInstance();
        lastValidDataTimeStamp = calender.getTime().getTime();

    }

    public void setData(byte[] buffer, int length) {
        parsePacket(buffer);
        Calendar calender = Calendar.getInstance();
        lastValidDataTimeStamp = calender.getTime().getTime();

    }

    /**
     * Parse the data packets
     * @return -1 = error,  1 = new and valid data
     */
    public int parsePacket(byte[] packet) {

        if (packet == null) {
            noBadPackets++;
            return -1;
        }
        int packetType = packetType(packet);
        if (Constants.useCheckSum == true) {
            boolean checkSumValid = AuxiliaryFunctions.getInstance().checkSum(packet);
            if (checkSumValid == false) {
                noBadPackets++;
                return -1;
            }
            noGoodPackets++;
        }
        switch (packetType) {
            case 1:

                extractValues(packet, 1);
                break;
            case 2:

                extractValues(packet, 2);
                break;
            default:

                return -1;
        }

        return 1;
    }

    private void extractValues(byte[] packet, int packetType) {
        int flyingMask = 0x01;
        if ((packet[0] & flyingMask) == flyingMask) {
            flying = true;
        } else {
            flying = false;
        }
        

        switch (packetType) {

            case 1:
                double lat = AuxiliaryFunctions.byteArrayToDouble(packet, 1);
                double lon = AuxiliaryFunctions.byteArrayToDouble(packet, 5);
                if (lat != 0.0) {
                    rocketPosition.lat = lat / Constants.latLonConvertionFactor;
                    rocketPosition.latitudeGood = true;
                } else {
                    rocketPosition.latitudeGood = false;
                }
                if (lon != 0.0) {
                    rocketPosition.lon = lon / Constants.latLonConvertionFactor;
                    rocketPosition.longitudeGood = true;
                } else {
                    rocketPosition.longitudeGood = false;
                }
                rocketPosition.GPAAltitude = AuxiliaryFunctions.byteArrayToINT16(packet, 5);
                if (flying && rocketPosition.latitudeGood && rocketPosition.longitudeGood)
                {
                    Position p = new Position(rocketPosition.lat, rocketPosition.lon, rocketPosition.GPAAltitude);
                    positions.add(p);
                    if (positions.size()> 7200) // two hours of data
                    {
                        positions.remove(1); // do not remove the first position
                    }
                }
                DataLog.getInstance().logData(1);

                break;
            case 2:

                realAltitudeTime = AuxiliaryFunctions.byteArrayToINT32(packet, 1);
                
                barometerAltitude = AuxiliaryFunctions.byteArrayToINT16(packet, 7);

                DataLog.getInstance().logData(2);
                break;

            default:
            //  System.out.println("Packet no good");
        }
    }

    
    private static int packetType(byte[] packet) {
        int packetType = -1;
        if (packet == null) {
            return -1;
        }
        
        int packetTypeByte = 0;
        byte pType1Mask = 0x02;
        byte pType2Mask = 0x04;

        packetType = packet[packetTypeByte] & pType2Mask;
        if (packetType == pType2Mask) {
            return 2;
        }
        packetType = packet[packetTypeByte] & pType1Mask;
        if (packetType == pType1Mask) {
            return 1;
        }


        return packetType;

    }

    public String getData() {
        return data;
    }
}
