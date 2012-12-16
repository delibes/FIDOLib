/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;

import fidolib.data.AISData;
import fidolib.data.Constants;
import fidolib.data.FlightData;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 *
 * @author Steen
 */
public class COMPortSerialReader implements Runnable, COMPortSerialReaderIF {

    /**
     * Input stream for the serial port
     */
    protected InputStream in;
    /**
     * Terminate the connection
     */
    public static boolean closeConnection = false;
    /**
     * Reference to the AIS data
     * 
     */
    protected DataParser aDataParser = null;

    /**
     * Constructor
     * @param in
     * @param aData the data model 
     */
    public COMPortSerialReader(InputStream in, DataParser aDataParser) {
        this.in = in;
        this.aDataParser = aDataParser;


    }

    @Override
    public void run() {
        if (aDataParser instanceof AISData) {
            runAISParser();
        } else if (aDataParser instanceof FlightData) {
            runFlightDataParser();
        }

    }

    /**
     * The run methed for parsing AIS messages
     */
    private void runAISParser() {
        byte[] buffer = new byte[1024];
        LinkedList<Byte> data = new LinkedList<Byte>();

        int len = -1;
        try {
            byte x1 = 0;
            byte x2 = 0;
            while (((len = this.in.read(buffer)) > -1) && (closeConnection == false)) {

                for (int i = 0; (i < len) && (i < buffer.length); i++) {
                    data.add(buffer[i]);

                    x2 = buffer[i];
                    if ((x1 == 0x0D) && (x2 == 0x0A)) {
                        buffer = new byte[data.size()];

                        for (int j = 0; j < buffer.length; j++) {


                            buffer[j] = data.getFirst();
                            data.removeFirst();

                        }
                        aDataParser.parseData(new String(buffer));
                        data.clear();

                    }
                    x1 = x2;
                }

            }

            if (closeConnection == true) {
                in.close();
                closeConnection = false;
            }

        } catch (IOException e) {
        }
    }

    /**
     * The runmethod for parsing the telemetry data
     */
    private void runFlightDataParser() {
        byte[] buffer = new byte[1024];
        LinkedList<Byte> data = new LinkedList<Byte>();

        int len = -1;
        try {
            byte x1 = 0;
            byte x2 = 0;
            
            while (((len = this.in.read(buffer)) > -1) && (closeConnection == false)) {

                
                for (int i = 0; (i < len) && (i < buffer.length); i++) {
                    data.add(buffer[i]);
                    
                    x2 = buffer[i];
                    if ((x1 == 0x0D) && (x2 == 0x0A) && (data.size() >= ((FlightData)aDataParser).getPacketLength())) {
                       byte[] dataBuffer = new byte[((FlightData)aDataParser).getPacketLength()];

                        for (int j = 0; j < dataBuffer.length; j++) {


                            dataBuffer[j] = data.get(data.size() - (((FlightData)aDataParser).getPacketLength()-j));
                            

                        }
                        // System.out.println("Parser " + buffer);
                        aDataParser.parseData(dataBuffer);
                        data.clear();

                    }
                    x1 = x2;
                   // System.out.println("data.size " + data.size());
                
                    if (data.size()> (((FlightData)aDataParser).getPacketLength()*2))
                    {
                        data.clear();
                    }
                }


            }

            if (closeConnection == true) {
                in.close();
                closeConnection = false;
            }

        } catch (IOException e) {
        }
    }

    @Override
    public void closeConnection() {
        closeConnection = true;

    }
}
