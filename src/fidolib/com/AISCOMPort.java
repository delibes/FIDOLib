/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;

import fidolib.data.AISData;
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
public class AISCOMPort {

    /**
     * Self reference
     */
    private static AISCOMPort aCOMPort = null;
    /**
     * COM Port reader
     */
    private AISCOMPortSerialReader aAISCOMPortSerialReader = null;
    /**
     * The COMM Port
     */
    private static CommPort commPort = null;

    /**
     * Reference to the AIS data
     * 
     */
    private AISData aAISData = null;
   /**
     * 
     * @param aAISData 
     */
    public AISCOMPort(AISData aAISData) {
        
        this.aAISData = aAISData;
        
    }
    /**
     * Get instance
     */
    public static AISCOMPort getInstance() {

        if (aCOMPort == null) {
            aCOMPort = new AISCOMPort(AISData.getInstance());
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
                System.out.println("Error: Port is currently in use");
            } else {

                commPort = portIdentifier.open(this.getClass().getName(), 2000);
                
                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);

                    InputStream in = serialPort.getInputStream();
  
                    aAISCOMPortSerialReader = new AISCOMPortSerialReader(in, aAISData);
                    (new Thread(aAISCOMPortSerialReader)).start();
  
                } else {
                    throw new Exception("Error: Only serial ports are handled.");
                }
            }

        } catch (gnu.io.NoSuchPortException nspe) {
            throw new Exception("Error: no such port: " + portName);
        }


    }

    public boolean isCOMPortOpen() {
        if (commPort != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Close the COMM Port connection
     */
    public void closeConnection() throws Exception {
        if (aAISCOMPortSerialReader != null) {
            AISCOMPortSerialReader.closeConnection();
            commPort.close();
            commPort = null;
        } else {
            throw new Exception("Unable to close oonnection");
        }

    }

   
}
