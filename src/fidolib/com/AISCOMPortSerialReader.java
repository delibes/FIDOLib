/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;

import fidolib.data.AISData;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 *
 * @author Steen
 */
public class AISCOMPortSerialReader implements Runnable {

    /**
     * Input stream for AISdata serial port
     */
    private InputStream in;
    /**
     * Close the connection
     */
    public static boolean closeConnection = false;
    /**
     * Reference to the AIS data
     * 
     */
    private AISData aAISData = null;

    /**
     * Constructor
     * @param in
     * @param aAISData the AIS data model 
     */
    public AISCOMPortSerialReader(InputStream in, AISData aAISData) {
        this.in = in;
        this.aAISData = aAISData;


    }

    public static void closeConnection() {
        closeConnection = true;
    }

    public void run() {
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
                        aAISData.parseAISData(new String(buffer));
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
}