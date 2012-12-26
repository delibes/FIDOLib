/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import fidolib.log.AISLog;
import fidolib.aisparser.Messages;
import fidolib.aisparser.Message1;
import fidolib.aisparser.Message18;
import fidolib.aisparser.Message2;
import fidolib.aisparser.Message24;
import fidolib.aisparser.Message3;
import fidolib.aisparser.Message4;
import fidolib.aisparser.Message5;
import fidolib.aisparser.Nmea;
import fidolib.aisparser.Vdm;
import fidolib.com.DataParser;
import fidolib.misc.AuxiliaryFunctions;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Steen
 */
public class AISData implements DataParser {

    /**
     * Sputnik info
     */
    public String sputnikMMSI = "";
    /**
     * Mission control info
     */
    public String mcMMSI = "";
    /**
     * Self reference
     */
    private static AISData aAISData = null;
    
    /**
     * Collection of all vessels received by AIS
     */
    public final LinkedList<VesselInfo> allVessels = new LinkedList<VesselInfo>();
    /**
     * Time stamp of last AIS message
     */
    public long AISTimeStamp = 0;

    /**
     * AIS log
     */
    private AISLog aAISLog;
    /**
     * Constructor
     */
    public AISData(AISLog aAISLog) {
       this.aAISLog = aAISLog;
    }

    /**
     * Get instance
     */
/*    public static AISData getInstance() {

        if (aAISData == null) {
            aAISData = new AISData();
        }
        return aAISData;

    }
*/
    /**
     * Parse AIS data receives the data and parses the AIS string
     */
    @Override
    public synchronized void parseData(String data) {
        Nmea nmea_message = new Nmea();
        nmea_message.init(data);

        if (nmea_message.checkChecksum() != 0) {
            data = data.substring(0, data.length() - 2); // remove trailing CR and LF
            aAISLog.logData(data + Constants.AISColumnSeparator + " => Checksum is BAD" + "\n");
            return;

        }

        Vdm vdm_message = new Vdm();
        try {
            int result = vdm_message.add(data);
            switch (vdm_message.msgid()) {
                case 1:
                    Message1 msg1 = new Message1();
                    msg1.parse(vdm_message.sixbit());
                    parseMessage(msg1);
                    break;
                case 2:
                    Message2 msg2 = new Message2();
                    msg2.parse(vdm_message.sixbit());
                    parseMessage(msg2);
                    break;
                case 3:
                    Message3 msg3 = new Message3();
                    msg3.parse(vdm_message.sixbit());
                    parseMessage(msg3);
                    break;
                case 4:
                    Message4 msg4 = new Message4();
                    msg4.parse(vdm_message.sixbit());
                    parseMessage(msg4);
                    break;
                case 5:
                    Message5 msg5 = new Message5();
                    msg5.parse(vdm_message.sixbit());
                    parseMessage(msg5);
                    break;
                case 18:
                    Message18 msg18 = new Message18();
                    msg18.parse(vdm_message.sixbit());
                    parseMessage(msg18);
                    break;
                case 24:
                    Message24 msg24 = new Message24();
                    msg24.parse(vdm_message.sixbit());
                    parseMessage(msg24);
                    break;
                default:

            }

            aAISLog.logData(data);

        } catch (Exception e) {
            data = data.substring(0, data.length() - 2); // remove trailing CR and LF
            aAISLog.logData(data + Constants.AISColumnSeparator + "Exception parsing AIS data. " + e.getMessage() + "\n");
        }


    }

    /**
     * Parse AIS data receives the data and parses the AIS string
     */
    public synchronized void parseMessage(Messages msg) {
        if (msg == null) {
            return;
        }
        Calendar c1 = Calendar.getInstance();
        AISTimeStamp = c1.getTimeInMillis();
        VesselInfo aVesselInfo = new VesselInfo();
        aVesselInfo.msgID = msg.msgid();
        if (msg instanceof Message1) {
            String msgMMSI = Long.toString(msg.userid());


            aVesselInfo.MMSI = msgMMSI;
            aVesselInfo.nav_status = ((Message1) msg).nav_status();
            aVesselInfo.pos.lat = ((Message1) msg).latitude() / 600000.0;
            aVesselInfo.pos.lon = ((Message1) msg).longitude() / 600000.0;
            aVesselInfo.rot = ((Message1) msg).rot();
            aVesselInfo.cog = ((Message1) msg).cog() / 10;
            aVesselInfo.pos_acc = ((Message1) msg).pos_acc();
            aVesselInfo.sog = ((Message1) msg).sog() / 10.0;
            aVesselInfo.true_heading = ((Message1) msg).true_heading();
            aVesselInfo.utc_sec = ((Message1) msg).utc_sec();
            aVesselInfo.timeStamp = c1.getTimeInMillis();
            updateVessels(aVesselInfo);


        }
        if (msg instanceof Message2) {
            String msgMMSI = Long.toString(msg.userid());
            aVesselInfo.MMSI = msgMMSI;
            aVesselInfo.nav_status = ((Message2) msg).nav_status();
            aVesselInfo.pos.lat = ((Message2) msg).latitude() / 600000.0;
            aVesselInfo.pos.lon = ((Message2) msg).longitude() / 600000.0;
            aVesselInfo.rot = ((Message2) msg).rot();
            aVesselInfo.cog = ((Message2) msg).cog() / 10;
            aVesselInfo.pos_acc = ((Message2) msg).pos_acc();
            aVesselInfo.sog = ((Message2) msg).sog() / 10.0;
            aVesselInfo.true_heading = ((Message2) msg).true_heading();
            aVesselInfo.utc_sec = ((Message2) msg).utc_sec();
            aVesselInfo.timeStamp = c1.getTimeInMillis();
            updateVessels(aVesselInfo);

        } else if (msg instanceof Message3) {
            String msgMMSI = Long.toString(msg.userid());
            aVesselInfo.MMSI = msgMMSI;
            aVesselInfo.nav_status = ((Message3) msg).nav_status();
            aVesselInfo.pos.lat = ((Message3) msg).latitude() / 600000.0;
            aVesselInfo.pos.lon = ((Message3) msg).longitude() / 600000.0;
            aVesselInfo.rot = ((Message3) msg).rot();
            aVesselInfo.cog = ((Message3) msg).cog() / 10;
            aVesselInfo.pos_acc = ((Message3) msg).pos_acc();
            aVesselInfo.sog = ((Message3) msg).sog() / 10.0;
            aVesselInfo.true_heading = ((Message3) msg).true_heading();
            aVesselInfo.utc_sec = ((Message3) msg).utc_sec();
            aVesselInfo.timeStamp = c1.getTimeInMillis();
            updateVessels(aVesselInfo);

        } else if (msg instanceof Message4) {
            String msgMMSI = Long.toString(msg.userid());
            aVesselInfo.baseStation = true;
            aVesselInfo.MMSI = msgMMSI;
            aVesselInfo.pos.lat = ((Message4) msg).latitude() / 600000.0;
            aVesselInfo.pos.lon = ((Message4) msg).longitude() / 600000.0;
            aVesselInfo.name = "Base Station";
            aVesselInfo.timeStamp = c1.getTimeInMillis();

            updateVessels(aVesselInfo);

        } else if (msg instanceof Message5) {
            String msgMMSI = Long.toString(msg.userid());
            aVesselInfo.MMSI = msgMMSI;
            aVesselInfo.IMO = "" + ((Message5) msg).imo();
            aVesselInfo.callSign = "" + ((Message5) msg).callsign();
            aVesselInfo.name = "" + ((Message5) msg).name();

            updateVessels(aVesselInfo);

        } else if (msg instanceof Message18) {
            String msgMMSI = Long.toString(msg.userid());
            aVesselInfo.MMSI = msgMMSI;
            aVesselInfo.pos.lat = ((Message18) msg).latitude() / 600000.0;
            aVesselInfo.pos.lon = ((Message18) msg).longitude() / 600000.0;
            aVesselInfo.cog = ((Message18) msg).cog() / 10;
            aVesselInfo.pos_acc = ((Message18) msg).pos_acc();
            aVesselInfo.sog = ((Message18) msg).sog() / 10.0;
            aVesselInfo.true_heading = ((Message18) msg).true_heading();
            aVesselInfo.utc_sec = ((Message18) msg).utc_sec();
            aVesselInfo.unit_flag = ((Message18) msg).unit_flag();
            aVesselInfo.timeStamp = c1.getTimeInMillis();

            updateVessels(aVesselInfo);

        } else if (msg instanceof Message24) {
            String msgMMSI = Long.toString(msg.userid());
            updateVesselCallSign(msgMMSI, (Message24) msg);
        }

    }

    public synchronized void updateVesselCallSign(String MMSI, Messages msg) {
        synchronized (allVessels) {
            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if ((aVesselInfo != null) && (msg != null)) {
                    if (aVesselInfo.MMSI.equals(MMSI)) {

                        if (msg instanceof Message5) {
                            aVesselInfo.IMO = "" + ((Message5) msg).imo();
                            aVesselInfo.setCallSign(((Message5) msg).callsign());
                            aVesselInfo.setName(((Message5) msg).callsign());
                            aVesselInfo.msgID = 24;
                        } else if (msg instanceof Message24) {
                            aVesselInfo.setCallSign(((Message24) msg).callsign());
                            aVesselInfo.setName(((Message24) msg).callsign());
                            aVesselInfo.msgID = 24;
                        }


                        return;
                    }
                }

            }

        }
        VesselInfo aVesselInfo = new VesselInfo();
        aVesselInfo.MMSI = MMSI;

        if (msg instanceof Message5) {
            aVesselInfo.IMO = "" + ((Message5) msg).imo();
            aVesselInfo.setCallSign(((Message5) msg).callsign());
            aVesselInfo.setName(((Message5) msg).callsign());
            aVesselInfo.msgID = 5;
        } else if (msg instanceof Message24) {
            aVesselInfo.setCallSign(((Message24) msg).callsign());
            aVesselInfo.setName(((Message24) msg).callsign());
            aVesselInfo.msgID = 24;
        }

        allVessels.add(aVesselInfo);

    }

    public synchronized void updateVessels(VesselInfo aVesselInfoArg) {
        synchronized (allVessels) {
            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if ((aVesselInfo != null) && (aVesselInfoArg != null)) {
                    if (aVesselInfo.MMSI.equals(aVesselInfoArg.MMSI)) {


                        aVesselInfo.pos.lat = aVesselInfoArg.pos.lat;
                        aVesselInfo.pos.lon = aVesselInfoArg.pos.lon;
                        aVesselInfo.cog = aVesselInfoArg.cog;
                        aVesselInfo.pos_acc = aVesselInfoArg.pos_acc;
                        aVesselInfo.sog = aVesselInfoArg.sog;
                        aVesselInfo.rot = aVesselInfoArg.rot;
                        aVesselInfo.true_heading = aVesselInfoArg.true_heading;
                        aVesselInfo.utc_sec = aVesselInfoArg.utc_sec;
                        aVesselInfo.timeStamp = aVesselInfoArg.timeStamp;
                        return;
                    }
                }

            }

            allVessels.add(aVesselInfoArg);

        }


    }

    public synchronized void printAllVessels() {
        synchronized (allVessels) {
            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                System.out.println(aVesselInfo.toString());
            }
        }

    }

    public synchronized VesselInfo getVessel(String MMSI) {
        synchronized (allVessels) {

            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if ((aVesselInfo != null) && (MMSI != null)) {
                    if (aVesselInfo.MMSI.equals(MMSI)) {

                        return aVesselInfo;
                    }
                }

            }
        }
        return null;

    }

    public synchronized LinkedList<VesselInfo> getAllVessels() {
        return allVessels;
    }

    /**
     * Remove vessel which havent been update in Constants.removeVesselMinutes
     */
    public synchronized void removeObsoleteVesselInfo() {
        Calendar c1 = Calendar.getInstance();
        synchronized (allVessels) {

            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if (aVesselInfo != null) {
                    if ((c1.getTimeInMillis() - aVesselInfo.timeStamp) > Constants.removeVesselMinutes) {
                        allVessels.remove(aVesselInfo);
                        return; // Silly, but has to be done (removing one element per call
                    }
                }

            }
        }
    }

    /**
     * Get the nearest ship MMSI according to x,y pos of mouse
     */
    public synchronized String getMMSI(int x, int y, int width, int height) {
        String MMSI = "";
        double minDist = Double.MAX_VALUE;
        synchronized (allVessels) {

            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if (aVesselInfo != null) {
                    AuxiliaryFunctions.calcLatLonPixels(aVesselInfo.pos, width, height);
                    double distance = Math.sqrt(Math.pow(x - aVesselInfo.pos.lonPixels, 2.0) + Math.pow(y - aVesselInfo.pos.latPixels, 2.0));
                    if ((distance < minDist) && (distance < 30)) {
                        minDist = distance;
                        MMSI = aVesselInfo.MMSI;

                    }
                }

            }
        }
        setSelected(MMSI);
        return MMSI;
    }

    public synchronized void setSelected(String MMSI) {
        synchronized (allVessels) {
            Iterator iterator = allVessels.iterator();
            while (iterator.hasNext()) {
                VesselInfo aVesselInfo = (VesselInfo) iterator.next();
                if ((aVesselInfo != null) && (MMSI != null)) {
                    if (aVesselInfo.MMSI.equals(MMSI)) {
                        aVesselInfo.isSelected = true;

                    } else {
                        aVesselInfo.isSelected = false;
                    }
                }

            }
        }
    }

    public synchronized String getAISDeltaT() {
        if (AISTimeStamp != 0) {
            Calendar calender = Calendar.getInstance();
            long now = calender.getTime().getTime();


            String secondsStr = String.format("%02d", ((now - AISTimeStamp) % 60000 / 1000));
            String minutesStr = String.format("%02d", ((now - AISTimeStamp) / 60000));

            return minutesStr + ":" + secondsStr;
        } else {
            return Constants.naString;
        }

    }

    @Override
    public synchronized int parseData(byte[] packet) {
      return -1; 
    }

}
