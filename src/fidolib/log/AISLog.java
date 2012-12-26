/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.log;

import fidolib.data.Constants;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Steen
 */
public class AISLog {
    /**
     * Log file
     */
    private FileWriter fstream = null;
    /**
     * Buffered writer
     */
    private BufferedWriter output = null;

    /**
     * Get instance
     */
  /*  public static AISLog getInstance() {

        if (aAISLog == null) {
            aAISLog = new AISLog();
        }
        return aAISLog;

    }
    */ 
    /**
     * Open the log file
     *
     * @param fileName
     */
    public synchronized boolean openLog(Calendar aCalendar) {
        try {
            String DATE_FORMAT = "yyyyMMdd HHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String logFileName = "log AIS " + sdf.format(aCalendar.getTime()) + ".csv";

            // Create file
            fstream = new FileWriter(logFileName);
            output = new BufferedWriter(fstream);
            String header = "Time" + Constants.AISColumnSeparator + "AIS msg" + Constants.AISColumnSeparator + "Parser msg" + "\n";
            output.write(header);
            return true;
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }

    }

    /**
     * Save a log entry
     */
    public synchronized boolean logData(String AISString) {

        try {
            if ((output != null)) {

                String DATE_FORMAT = "HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Calendar c1 = Calendar.getInstance();
                String line = sdf.format(c1.getTime()) + ":" + (c1.getTimeInMillis() % 1000) + Constants.AISColumnSeparator
                        + AISString;
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
