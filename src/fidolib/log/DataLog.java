package fidolib.log;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Steen Andersen
 */
import fidolib.data.FlightData;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
     * Decimal symbols
     */
    private DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
    /**
     * Formatting  
     */
    private DecimalFormat df = new DecimalFormat("", decimalSymbols);

    /**
     * Constructor
     */
    public DataLog() {
        decimalSymbols.setDecimalSeparator(',');
        decimalSymbols.setGroupingSeparator('.');


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

            String header = "Time; Packet Type; Latitude; Longitude; GPS Altitude; Barometer Altitude;Real Altitude Time;";
            header += "Good Packets; Bad Packets";
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
    public boolean logData(int packetType) {

        try {
            if ((output != null)) {

                String DATE_FORMAT = "HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Calendar c1 = Calendar.getInstance();
                String line = sdf.format(c1.getTime()) + ":" + (c1.getTimeInMillis() % 1000) + ";"
                        + packetType + ";"
                        + df.format(FlightData.getInstance().rocketPosition.lat) + ";"
                        + df.format(FlightData.getInstance().rocketPosition.lon) + ";"
                        + FlightData.getInstance().rocketPosition.GPSAltitude + ";"
                        + FlightData.getInstance().noGoodPackets + ";"
                        + FlightData.getInstance().noBadPackets + ";"
                        + "\n";

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
