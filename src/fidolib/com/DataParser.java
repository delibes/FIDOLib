/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.com;


/**
 *
 * @author Steen
 */
public interface DataParser {
    
    /**
     * Parse data received
     */
    public void parseData(String data);
    /**
     * Parse data received
     */
    public int parseData(byte[] packet);
    
    
    
}
