/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Steen
 */
public class VesselInfo {

    /**
     * Decimal symbols
     */
    private static DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(new Locale("da", "DK"));
    public int nav_status = -1;       // navigational status
    public int rot;               // Rate of turn - right or left, 0 to 720 degrees per minute (input from rate-of-turn indicator)
    public double sog;               // Speed Over Ground Knots
    public int pos_acc;           //  Position Accuracy
    public Position pos = new Position(); // Lat/Long decimal degress
    public int cog;               // Course over Ground
    public int true_heading;      // True heading
    public int utc_sec;           // UTC Seconds
    public String callSign = Constants.naString;
    public String name = Constants.naString;
    public int msgID = 0;
    public int unit_flag = 0;
    public boolean baseStation = false;
    /**
     * MMSI number
     */
    public String MMSI = "";
    /**
     * IMO number
     */
    public String IMO = Constants.naString;
    /**
     * Time stamp for reception of the data
     */
    public long timeStamp = 0;

    /**
     * Is the vessel selected on the image panel
     */
    public boolean isSelected = false;
    
    public String getSOG(boolean knotsStr) {


        DecimalFormat df = new DecimalFormat("0.0", decimalSymbols);
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        if (knotsStr == true) {
            return df.format(sog) + Constants.knotsLabel;
        } else {
            return df.format(sog);
        }

    }

    public String getTH() {


        DecimalFormat df = new DecimalFormat("0", decimalSymbols);
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        if (true_heading == 511) {
            return Constants.naString;
        } else {
            return df.format(true_heading) + Constants.degreeChar;
        }
    }

    @Override
    public String toString() {
        return MMSI;
    }

    public String[][] getVesselInfo() {

        String[][] info = new String[14][2];
        int i = 0;
        info[i][0] = "MMSI";
        info[i++][1] = MMSI;
        info[i][0] = "IMO";
        info[i++][1] = IMO;
        info[i][0] = "Call";
        info[i++][1] = this.callSign;
        info[i][0] = "Name";
        info[i++][1] = this.name;
        info[i][0] = Constants.latStr ;
        info[i++][1] = pos.getLat();
        info[i][0] = Constants.lonStr;
        info[i++][1] = pos.getLon();
        info[i][0] = "SOG";
        info[i++][1] = "" + sog + Constants.knotsLabel;
        info[i][0] = "COG";
        info[i++][1] = "" + cog + Constants.degreeChar;
        info[i][0] = "ROT";
        info[i++][1] = "" + rot + Constants.degreeChar;
        info[i][0] = "TH";
        info[i++][1] = "" + getTH();
        info[i][0] = "Status";
        info[i++][1] = "" + getNavStatus();
        
        info[i][0] = "Class";
        info[i++][1] = "" + getUnit();

        info[i][0] = "MSG";
        info[i++][1] = "" + msgID;


        if (timeStamp > 0) {
            Calendar calender = Calendar.getInstance();
            long now = calender.getTime().getTime();

            info[i][0] = "dT";
            String secondsStr = String.format("%02d", ((now - timeStamp) % 60000 /1000) );
            String minutesStr = String.format("%02d", ((now - timeStamp) / 60000) );
            info[i++][1] = "" + minutesStr + ":" + secondsStr ;
        } else {
            info[i][0] = "dT";
            info[i++][1] = Constants.naString;

        }
        return info;
    }
    public String getNavStatus()
    {
        String navStatus = "";
        switch (this.nav_status)
        {
            case 0:
                navStatus = "U. way (engine)";
                break;
            case 1:
                navStatus = "At anchor";
                break;
            case 2:
                navStatus = "Not u. command";
                break;
            case 3:
                navStatus = "Rstr. man.";
                break;
            case 4:
                navStatus = "Cnstr. Draught";
                break;
            case 5:
                navStatus = "Moored";
                break;
            case 6:
                navStatus = "Aground";
                break;
            case 7:
                navStatus = "Fishing";
                break;
            case 8:
                navStatus = "U. way (Sailing)";
                break;
            default:
             navStatus = "N/A";
                
        }
        return navStatus;
        
    }
    public String getUnit() {
        if (this.unit_flag == 0) {
            return "A";

        } else {
            return "B";
        }
    }
    public void setCallSign(String callSign)
    {
        if (callSign != null)
        {
            int position = callSign.indexOf('@');
            if (position >= 0)
            {
                this.callSign = callSign.substring(0, position);
            }
            else {
                this.callSign = callSign;
            }
          
            
        }
    }
    public String getCallSign()
    {
        return callSign;
    }
    public void setName(String name)
    {
        if (name != null)
        {
            int position = name.indexOf('@');
            if (position >= 0)
            {
                this.name = name.substring(0, position);
            }
            else {
                this.name = name;
            }
            
            
        }
    }
    public String getName()
    {
        return name;
    }
}
