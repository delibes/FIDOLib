package fidolib.log;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Steen Andersen
 */
import fidolib.data.Constants;
import fidolib.data.RocketInfo;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLog {

    /**
     * Self reference
     */
    private static DataLog logRef = null;
    /**
     * Log file
     */
    private FileWriter fstream = null;
    /**
     * Buffered writer
     */
    private BufferedWriter output = null;
   
    /**
     * Constructor
     */
    public DataLog() {
   

    }

    /**
     * Get instance
     */
    public static DataLog getInstance() {

        if (logRef == null) {
            logRef = new DataLog();
        }
        return logRef;

    }

    /**
     * Open the log file
     * @param fileName
     */
    public boolean openLog(Calendar aCalendar) {
        try {


            String DATE_FORMAT = "yyyyMMdd HHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

            String logFileName = "log " + sdf.format(aCalendar.getTime()) + ".csv";

            // Create file
            fstream = new FileWriter(logFileName);
            output = new BufferedWriter(fstream);

            String header = "Time (GMT+1);Time ms;Packet #;Flying;GPS Fix;Latitude " + Constants.northSouth +";Longitude " +Constants.eastWest +";GPS Altitude (m);";
            header += "AAU GPS Fix Time;Velocity (m/s);Vertical velocity (m/s);Horizontal velocity (m/s);";
            header += "COG;ETA (s);Down range (m);Voltage (v);";
            header += "Gyro X;Gyro Y;Gyro Z;Gyro Time;Acc X (m G);Acc Y (m G);Acc Z (m G);Acc Time;";
            header += "Good Packets;Bad Packets";
            synchronized (output) {
                output.write(header + "\n");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }

    }

    public void logData(String data) {
        synchronized (output) {
            try {
                String DATE_FORMAT = "HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Calendar c1 = Calendar.getInstance();
                String line = sdf.format(c1.getTime()) + ":" + (c1.getTimeInMillis() % 1000) + ";";
                line += data;

                output.write(line);
            } catch (IOException ex) {
                Logger.getLogger(DataLog.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
    }

    
    /**
     * Save a log entry
     * @param packetType the packet type triggering the log entry
     * 0..30 are packet types, and -1 is used when it's the timer
     * triggering the log entry.
     */
    public boolean logData(RocketInfo aRocketInfo, double voltage, int packetNumber, int goodPackages, int badPackages) {

        try {
            if ((output != null)) {

                String DATE_FORMAT = "HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Calendar c1 = Calendar.getInstance();
                String line = sdf.format(c1.getTime()) + ";"
                        + c1.getTimeInMillis() + ";"
                        + packetNumber + ";"
                        + aRocketInfo.flying + ";"
                        + aRocketInfo.GPSFix + ";"
                        + aRocketInfo.lat + ";"
                        + aRocketInfo.lon + ";"
                        + aRocketInfo.GPSAltitude + ";"
                        + aRocketInfo.MCUGPSFixTime + ";"
                        + aRocketInfo.velocity + ";"
                        + aRocketInfo.verticalVelocity + ";"
                        + aRocketInfo.horizontalVelocity + ";"
                        + aRocketInfo.COG + ";"
                        + aRocketInfo.ETA + ";"
                        + aRocketInfo.downRange + ";"
                        + voltage + ";"
                        + aRocketInfo.gyroX + ";"
                        + aRocketInfo.gyroY + ";"
                        + aRocketInfo.gyroZ + ";"
                        + aRocketInfo.gyroTime + ";"
                        + aRocketInfo.accX + ";"
                        + aRocketInfo.accY + ";"
                        + aRocketInfo.accZ + ";"
                        + aRocketInfo.accTime + ";"
                        + goodPackages + ";"
                        + badPackages + ";"
                        + "\n";

              //  System.out.println(line);
                synchronized (output) {
                    output.write(line);


                }

            }
            return true;

        } catch (IOException e) {

            return false;
        }
    }

    /**
     * Close the log
     */
    public void closeLog() {

        try {
            synchronized (output) {

                output.close();
            }

        } catch (IOException e) {
        }
    }
}
