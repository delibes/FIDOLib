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

public class DataLog {

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
     * Open the log file
     *
     * @param fileName
     */
    public synchronized boolean openLog(Calendar aCalendar) {
        try {


            String DATE_FORMAT = "yyyyMMdd HHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String logFileName = "log Flight Data " + sdf.format(aCalendar.getTime()) + ".csv";
            // Create file
            fstream = new FileWriter(logFileName);
            output = new BufferedWriter(fstream);

            String header = "Time (GMT+1);Time ms;Packet #;Flying;GPS Fix;Latitude " + Constants.northSouth + ";Longitude " + Constants.eastWest + ";GPS Altitude (m);";
            header += "AAU GPS Fix Time;Velocity (m/s);Vertical velocity (m/s);Horizontal velocity (m/s);";
            header += "COG;ETA (s);Down range (m);Voltage (v);";
            header += "Gyro X;Gyro Y;Gyro Z;Gyro Time;Acc X (m G);Acc Y (m G);Acc Z (m G);Acc Time;";
            header += "Good Packets;Bad Packets;Bytes received";
            output.write(header + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }

    }

    /**
     * Save a log entry
     *
     * @param packetType the packet type triggering the log entry 0..30 are
     * packet types, and -1 is used when it's the timer triggering the log
     * entry.
     */
    public synchronized boolean logData(RocketInfo aRocketInfo, double voltage, int packetNumber, int goodPackages, int badPackages, long bytesReceived) {

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
                        + bytesReceived + ";"
                        + "\n";

                output.write(line);
            }
            return true;

        } catch (IOException e) {

            System.out.println("Exception: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close the log
     */
    public synchronized void closeLog() {

        if (output != null) {
            try {
                output.close();

            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage());
            }

        }
    }
}
