/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import fidolib.com.DataParser;
import fidolib.log.DataLog;
import fidolib.misc.AuxiliaryFunctions;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Steen Andersen
 */
public class FlightData implements DataParser, GetPosition {

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
     * Number of good packets
     */
    public int noGoodPackets = 0;
    /**
     * Number of bad packets
     */
    public int noBadPackets = 0;
    /**
     * The latitude and longitude position of the rocket
     */
    public RocketInfo rocketPosition = new RocketInfo();
    /**
     * The position if the rocket at lift off
     */
    public RocketInfo liftOffPosition = null;
    /**
     * 
     */
    public ArrayList<RocketInfo> arrayList = new ArrayList<RocketInfo>();
    /**
     * List of positions after lift off
     */
    public List positions = Collections.synchronizedList(arrayList);
    /**
     * Voltage of the power supply of the AAU
     */
    public double AAUVoltage = 0.0;

    /**
     * Indices in the packet received from the AAU
     */
    public enum PacketIndices {

        packetTypeIndex(0),
        voltageIndex(1),
        MCUGPSTimeIndex(2),
        latitudeAsLongIndex(6),
        longitudeAsLongIndex(10),
        altitudeIndex(14),
        GPSFixIndex(16),
        gyroXIndex(17),
        gyroYIndex(21),
        gyroZIndex(25),
        accXIndex(29),
        accYIndex(33),
        accZIndex(37),
        packetNumberIndex(41),
        CSIndex(45),
        packetLength(49);
        private final int index;

        PacketIndices(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * Constructor
     */
    public FlightData() {
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

    /**
     * Parse the data packets
     * @return -1 = error,  1 = new and valid data
     */
    @Override
    public int parseData(byte[] packet) {

        if (packet == null) {
            noBadPackets++;
            return -1;
        }
      //  System.out.println(PacketIndices.CSIndex.getIndex());
      //  System.out.println(PacketIndices.GPSFixIndex.getIndex());
        int packetType = packetType(packet);

       // System.out.println(">" + packetType);
        if (Constants.useCheckSum == true) {
            boolean checkSumValid = AuxiliaryFunctions.getInstance().checkSum(packet,PacketIndices.CSIndex.getIndex());
            if (checkSumValid == false) {
     //   System.out.println(checkSumValid);
                noBadPackets++;
                return -1;
            }
            noGoodPackets++;
        }

       // System.out.println(packetType);
        switch (packetType) {
            case 1:
                Calendar calender = Calendar.getInstance();
                lastValidDataTimeStamp = calender.getTime().getTime();
                extractValues(packet, 1);
                break;

            default:

                return -1;
        }

        return 1;
    }

    private void extractValues(byte[] packet, int packetType) {
        int flyingMask = 0x01;
        if ((packet[0] & flyingMask) == flyingMask) {
            rocketPosition.flying = true;

        }


        int packetNumber;
        
        switch (packetType) {

            case 1:
                AAUVoltage = (double) (packet[PacketIndices.voltageIndex.getIndex()] & 0xff) / 10.0;
                rocketPosition.MCUGPSFixTime = AuxiliaryFunctions.byteArrayToINT32(packet, PacketIndices.MCUGPSTimeIndex.getIndex());
                double lat = AuxiliaryFunctions.byteArrayToDouble(packet, PacketIndices.latitudeAsLongIndex.getIndex());
                double lon = AuxiliaryFunctions.byteArrayToDouble(packet, PacketIndices.longitudeAsLongIndex.getIndex());
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
                rocketPosition.GPSAltitude = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.altitudeIndex.getIndex());
                rocketPosition.GPSFix = (packet[PacketIndices.GPSFixIndex.getIndex()] & 0xff);
               packetNumber = AuxiliaryFunctions.byteArrayToINT32(packet, PacketIndices.packetNumberIndex.getIndex());
                if (positions.size() > 1) {
                    RocketInfo previousPos = (RocketInfo) positions.get(positions.size() - 1);
                    if (previousPos != null) {
                        if ((rocketPosition.lat != previousPos.lat)
                                || (rocketPosition.lon != previousPos.lon)
                                || (rocketPosition.GPSAltitude != previousPos.GPSAltitude)) // Different position
                        {
                            rocketPosition.COG = RocketInfo.initialBearing(previousPos, rocketPosition);

                            rocketPosition.downRange = RocketInfo.disanceNauticalMiles(rocketPosition, liftOffPosition) * Constants.nauticalMile;

                            // System.out.println(rocketPosition.MCUGPSFixTime + " " + previousPos.MCUGPSFixTime);
                            double deltaT = (rocketPosition.MCUGPSFixTime - previousPos.MCUGPSFixTime) / 1000.0;
                            if (deltaT != 0) {
                                rocketPosition.verticalVelocity = (rocketPosition.GPSAltitude - previousPos.GPSAltitude) / deltaT;
                                //System.out.println("v v " +  rocketPosition.GPSAltitude + " " +  previousPos.GPSAltitude + " " +deltaT);
                                rocketPosition.horizontalVelocity = (RocketInfo.disanceNauticalMiles(rocketPosition, previousPos) * Constants.nauticalMile) / deltaT;
                                rocketPosition.velocity = Math.sqrt(Math.pow(rocketPosition.verticalVelocity, 2) + Math.pow(rocketPosition.horizontalVelocity, 2));
                                double avgVSpeed = 0;
                                int count = 0;
                                for (int i = positions.size() - 1; (i >= 0) && (i > positions.size() - 10); i--) {
                                    avgVSpeed += ((RocketInfo) positions.get(i)).verticalVelocity;
                                    count++;
                                }
                                if (count > 0) {
                                    avgVSpeed = avgVSpeed / count;
                                }
                                if (avgVSpeed <= -1.0) {
                                    rocketPosition.ETA = (int) (rocketPosition.GPSAltitude / (avgVSpeed * -1));
                                } else {
                                    rocketPosition.ETA = 0;
                                }


                            }
                            RocketInfo p = new RocketInfo(rocketPosition);
                            positions.add(p);
                            if ((rocketPosition.flying == true) || (rocketPosition.GPSAltitude > 200) && (this.liftOffPosition == null)) {
                                liftOffPosition = new RocketInfo(rocketPosition);
                            }
                            if (positions.size() > 3600) // Approximately 2 hours of GPS positions
                            {
                                positions.remove(0);
                            }
                        }

                    }

                } else { // Add the first one without doing calculations of velocity etc.
                    RocketInfo p = new RocketInfo(rocketPosition);
                    positions.add(p);
                }
                DataLog.getInstance().logData(rocketPosition, AAUVoltage, packetNumber, noGoodPackets, noBadPackets);
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


        return -1;

    }

    public String getData() {
        return data;
    }

    @Override
    public void parseData(String data) {
    }

    @Override
    public double getLatitude() {
        return this.rocketPosition.lat;
    }

    @Override
    public double getLongitude() {
        return this.rocketPosition.lon;
    }

    @Override
    public RocketInfo getPosition() {
        return this.rocketPosition;
    }

    public String getGPSTime() {
        if (rocketPosition.MCUGPSFixTime == 0) {
            return Constants.naString;
        } else {
            String hoursStr = String.format("%02d", (rocketPosition.MCUGPSFixTime / 3600));
            String minutesStr = String.format("%02d", (rocketPosition.MCUGPSFixTime / 60 % 60));
            String secondsStr = String.format("%02d", (rocketPosition.MCUGPSFixTime % 60));
            return hoursStr + ":" + minutesStr + ":" + secondsStr;

        }
    }

    public String getETA() {
        if (rocketPosition.ETA > 0) {
            String hoursStr = String.format("%02d", (rocketPosition.ETA / 3600));
            String minutesStr = String.format("%02d", (rocketPosition.ETA / 60 % 60));
            String secondsStr = String.format("%02d", (rocketPosition.ETA % 60));
            return hoursStr + ":" + minutesStr + ":" + secondsStr;
        } else {
            return Constants.naString;
        }

    }

    public String getMCDistance() {
        VesselInfo mc = AISData.getInstance().getVessel(AISData.getInstance().mcMMSI);
        if (mc != null) {
            double disanceNauticalMiles = RocketInfo.disanceNauticalMiles(aFlightData.rocketPosition, mc.pos);
            double distanceMeters = disanceNauticalMiles * Constants.nauticalMile;

            String distStr = "";
            DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
            decimalSymbols.setDecimalSeparator('.');
            decimalSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
            distStr = "" + df.format(disanceNauticalMiles) + " / " + df.format(distanceMeters / 1000);
            return distStr;
        } else {
            return Constants.naString;
        }

    }

    public String getMCBearing() {
        VesselInfo mc = AISData.getInstance().getVessel(AISData.getInstance().mcMMSI);
        if (mc != null) {
            int brg = RocketInfo.initialBearing(mc.pos, rocketPosition);
            if (brg >= 0) {
                return "" + brg + Constants.degreeChar;
            }
        }

        return Constants.naString;

    }

    public String getAAUVoltage() {
        if (lastValidDataTimeStamp > 0) {
            return "" + AAUVoltage;
        } else {
            return Constants.naString;
        }
    }

    public String getFlying() {
        if (lastValidDataTimeStamp > 0) {
            if (rocketPosition.flying == true) {
                return "Yes";
            } else {
                return "No";
            }
        } else {
            return Constants.naString;
        }
    }
    public int getPacketLength()
    {
        return PacketIndices.packetLength.getIndex(); 
    }
}
