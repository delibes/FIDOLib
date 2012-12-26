/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.log;

/**
 *
 * @author Steen
 */
public class LogFactory {
    
    /**
     * Log for AIS messages
     */
     private static volatile AISLog aAISLog;
     /**
      * Log for flight data
      */
     private static volatile DataLog aDataLog; 

     public static AISLog getAISLog() {
         if (aAISLog == null)
         {
              aAISLog = new AISLog();
         }
         return aAISLog;
     }
     public static DataLog getDataLogInstance() {

        if (aDataLog == null) {
            aDataLog = new DataLog();
        }
        return aDataLog;

    }
    
}
