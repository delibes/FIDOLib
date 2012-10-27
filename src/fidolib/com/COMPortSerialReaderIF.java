/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;


/**
 *
 * @author Steen
 */
public interface COMPortSerialReaderIF {

    /**
     * Read the data from the com port and parse them
     */
    public void run();
    /**
     * Close the com port
     */
    public void closeConnection();

}
