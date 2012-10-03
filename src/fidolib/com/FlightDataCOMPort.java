/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steen Andersen
 */
public class FlightDataCOMPort {

    /**
     * Self reference
     */
    private static FlightDataCOMPort aCOMPort = null;
    /**
     * COM Port reader
     */
    private static FlightDataCOMSerialReader aSerialReader = null;
    /**
     * The COMM Port
     */
    private static CommPort commPort = null;

    /**
     * Get instance
     */
    public static FlightDataCOMPort getInstance() {

        if (aCOMPort == null) {
            aCOMPort = new FlightDataCOMPort();
        }
        return aCOMPort;

    }

    /**
     * Return available ports
     * @return 
     */
    public static List listPorts() {
        List list = new LinkedList();
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            list.add(portIdentifier.getName());
            
        }
        return list;
    }

    public static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    /*
     * Connect to serial port
     * @param portName The name of the port
     * @param baudRate The port baud rate
     * @param dataBits the data bits
     * @param stopBits the stop bits
     * @param parity the parity of the port connect.
     */

    public void connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
//                   JOptionPane.showMessageDialog(FIDOSmaragdApp.getApplication().getMainFrame(),
//                    "Error: Port is currently in use",
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
               
            } else {

                commPort = portIdentifier.open(this.getClass().getName(), 2000);
                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);

                    InputStream in = serialPort.getInputStream();
                    

                    FlightDataCOMPort.aSerialReader = new FlightDataCOMSerialReader(in);
                    (new Thread(aSerialReader)).start();
                    

                } else {
                    throw new Exception("Error: Only serial ports are handled.");
                }
            }

        } catch (gnu.io.NoSuchPortException nspe) {
            throw new Exception("Error: no such port: " + portName);
        }


    }

    public static boolean isCOMPortOpen() {
        if (commPort != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Close the COMM Port connection
     */
    public static void closeConnection() throws Exception {
        if (aSerialReader != null) {
            FlightDataCOMSerialReader.closeConnection();
            commPort.close();
            commPort = null;
        } else {
            throw new Exception("Unable to close oonnection");
        }

    }

   
}
