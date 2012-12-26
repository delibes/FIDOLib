/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import fidolib.log.LogFactory;

/**
 *
 * @author Steen
 */
public class DataModelFactory {
    
    /**
     * Model for AIS data
     */
    private static volatile AISData aAISData = null;
    /**
     * Model for flight data
     */
    private static volatile FlightData aFlightData = null;
    
    
    public synchronized static AISData getAISDataInstance()
    {
         if (aAISData == null)
         {
             aAISData =  new AISData(LogFactory.getAISLog());
         }
         return aAISData;
    }
    public static FlightData getFlightDataInstance() {

        if (aFlightData == null) {
            aFlightData = new FlightData(getAISDataInstance(),LogFactory.getDataLogInstance());
        }
        return aFlightData;

    }
    
}
