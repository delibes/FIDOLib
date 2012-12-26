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
     * Delta T since last GPS reading
     */
    public int GPSDeltaTIndex = 0;
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
     * The number of flight data bytes received 
     */
    public long bytesReceived = 0;
    /**
     * The latitude and longitude position of the rocket as well as additional infomation.
     */
    public RocketInfo rocketPosition = new RocketInfo();
    /**
     * The position if the rocket at lift off
     */
    public RocketInfo liftOffPosition = null;
    /**
     * List of rocket info objects used for drawing on the altitude panel
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
     * Sd card OK?
     */
    public int SDCardOK = -1;

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
        gyroYIndex(19),
        gyroZIndex(21),
        gyroTimeStampIndex(23),
        accXIndex(27),
        accYIndex(29),
        accZIndex(31),
        accTimeStampIndex(33),
        packetNumberIndex(37),
        SdCardIndex(41),
        GPSDeltaTIndex(42),
        CSIndex(44),
        packetLength(48);
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
        int packetType = packetType(packet);

        if (Constants.useCheckSum == true) {
            boolean checkSumValid = AuxiliaryFunctions.getInstance().checkSum(packet, PacketIndices.CSIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
            if (checkSumValid == false) {
                noBadPackets++;
                return -1;
            }
            noGoodPackets++;
        }

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
                SDCardOK = (int) (packet[PacketIndices.SdCardIndex.getIndex()] & 0xff);
                GPSDeltaTIndex = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.GPSDeltaTIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.MCUGPSFixTime = AuxiliaryFunctions.byteArrayToINT32(packet, PacketIndices.MCUGPSTimeIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.gyroX = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.gyroXIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.gyroY = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.gyroYIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.gyroZ = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.gyroZIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.gyroTime = AuxiliaryFunctions.byteArrayToINT32(packet, PacketIndices.gyroTimeStampIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);

                rocketPosition.accX = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.accXIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.accY = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.accYIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.accZ = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.accZIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.accTime = AuxiliaryFunctions.byteArrayToINT32(packet, PacketIndices.accTimeStampIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);

                double lat = AuxiliaryFunctions.byteArrayToDouble(packet, PacketIndices.latitudeAsLongIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                double lon = AuxiliaryFunctions.byteArrayToDouble(packet, PacketIndices.longitudeAsLongIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
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
                rocketPosition.GPSAltitude = AuxiliaryFunctions.byteArrayToINT16(packet, PacketIndices.altitudeIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                rocketPosition.GPSFix = (packet[PacketIndices.GPSFixIndex.getIndex()] & 0xff);
                packetNumber = AuxiliaryFunctions.byteArrayToINT32(packet, PacketIndices.packetNumberIndex.getIndex(), AuxiliaryFunctions.Endian.BIG);
                if (positions.size() > 1) {
                    RocketInfo previousPos = (RocketInfo) positions.get(positions.size() - 1);
                    if (previousPos != null) {
                        if ((rocketPosition.lat != previousPos.lat)
                                || (rocketPosition.lon != previousPos.lon)
                                || (rocketPosition.GPSAltitude != previousPos.GPSAltitude)) // Different position
                        {
                            rocketPosition.COG = AuxiliaryFunctions.initialBearing(previousPos, rocketPosition);

                            rocketPosition.downRange = AuxiliaryFunctions.disanceNauticalMiles(rocketPosition, liftOffPosition) * Constants.nauticalMile;

                            double deltaT = (rocketPosition.MCUGPSFixTime - previousPos.MCUGPSFixTime) / 1000.0;
                            if (deltaT != 0) {
                                rocketPosition.verticalVelocity = (rocketPosition.GPSAltitude - previousPos.GPSAltitude) / deltaT;
                                rocketPosition.horizontalVelocity = (AuxiliaryFunctions.disanceNauticalMiles(rocketPosition, previousPos) * Constants.nauticalMile) / deltaT;
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
                DataLog.getInstance().logData(rocketPosition, AAUVoltage, packetNumber, noGoodPackets, noBadPackets, bytesReceived);
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
            double disanceNauticalMiles = AuxiliaryFunctions.disanceNauticalMiles(aFlightData.rocketPosition, mc.pos);
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
            int brg = AuxiliaryFunctions.initialBearing(mc.pos, rocketPosition);
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

    public int getPacketLength() {
        return PacketIndices.packetLength.getIndex();
    }

    public String getSDCardOK() {
        if (SDCardOK == -1)
        {
            return Constants.naString;
        }
        else if (SDCardOK == 0){
            return "Not OK";
        }
        else {
            return "OK";
        }
    }

    public String getGPSDeltaTIndex() {
        if (GPSDeltaTIndex == -1)
        {
            return Constants.naString;
        }
        else if (GPSDeltaTIndex > 2) {
            String minutesStr = String.format("%02d", (GPSDeltaTIndex / 60 % 60));
            String secondsStr = String.format("%02d", (GPSDeltaTIndex % 60));
            return minutesStr + ":" + secondsStr;
        } else {
            return "";
        }


    }
}
