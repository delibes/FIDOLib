/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;

import fidolib.data.Constants;
import fidolib.data.FlightData;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

class FlightDataCOMSerialReader implements Runnable {

    /**
     * Input stream for the serial port
     */
    private InputStream in;
    /**
     * Terminate the connection
     */
    public static boolean closeConnection = false;

    /**
     * Constructor
     * @param in Input stream for the serial port
     */
    public FlightDataCOMSerialReader(InputStream in) {
        this.in = in;

    }

    public static void closeConnection() {
        closeConnection = true;
    }

    @Override
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
                    if ((data.size() >= Constants.packetLength)
                            && (x1 == 0x0D) && (x2 == 0x0A)) {
                        for (int j = 0; j < Constants.packetLength; j++) {


                            buffer[(Constants.packetLength - j) - 1] = data.getLast();
                            data.removeLast();

                        }
                        FlightData.getInstance().setData(buffer, len);
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