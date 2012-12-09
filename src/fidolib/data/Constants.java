/*
 * Constants used throughout the program for color schemes, text on buttons etc.
 */
package fidolib.data;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Steen Andersen
 */
public class Constants {

    // Colors
    public static Color backGroundColor = new Color(0, 16, 100);
    public static Color blueish = new Color(109,140,169);
    public static Color rollGradientStartColor = Color.darkGray;
    public static Color rollGradientStopColor = Color.BLACK;
    public static Color textColor = Color.white;
    public static Color skyColor = Color.BLUE;
    public static Color rocketColor = Color.white;
    public static Color warningColor = Color.red;
    public static Color angleLinesColor = Color.yellow;
    public static Color progressBarColor = Color.GRAY;
    public static Color flightStatusNOKcolor = Color.GREEN;
    public static Color flightStatusConfirmedColor = Color.RED;
    public static Color ESD139Color = Color.BLACK;
    public static Color mousePositionColor = blueish.darker();
    public static Color sputnikColor = Color.RED;
    public static Color mcColor = Color.DARK_GRAY;
    public static Color smaragdColor = Color.GREEN;
    public static Color otherVesselsColor = Color.YELLOW;
    public static Color otherVesselsNoMovementColor = Color.WHITE;
    public static Color baseStationColor = Color.BLUE;
    public static Color otherVesselsTextColor = Color.BLACK;
    public static Color knotsLabelColor = Color.BLACK;
    public static Color outOfRangeColor = Color.RED;
    public static Color arrowColor = Color.BLUE;
    public static Color inRangeColor = Color.white;
    /**
     * Gradient color start
     */
    public static Color gradientColorStart = blueish;
    /**
     * Gradient color start
     */
    public static Color gradientColorStop = Color.BLACK;
    // Fonts
    public static Font boldFont = new Font("SansSerif", Font.BOLD, 12);
    public static Font plainFont = new Font("SansSerif", Font.PLAIN, 12);
    public static Font warningFont = new Font("SansSerif", Font.BOLD, 16);
    /**
     * Range limit for pitch and yaw
     */
    public static double rangeLimitAlarm = 20;
    /**
     * Range limit for pitch and yaw activated
     */
    public static boolean rangeLimitAlarmActive = true;
    /**
     * Progress bar indication
     */
    public static int progressbarMECO = -1;
    /**
     * Progress bar indication
     */
    public static int progressbarSEP = -1;
    /**
     * Progress bar indication
     */
    public static int progressbar2IGN = -1;
    /**
     * Progress bar indication
     */
    public static int progressbar2MECO = -1;
    /**
     * Text string for the progress bar
     */
    public static String progressbarMECOStr = "1st ECO";
    /**
     * Text string for the progress bar
     */
    public static String progressbar2STGStr = "2nd IGN";
    /**
     * Text string for the progress bar
     */
    public static String progressbar2STGMECOStr = "2nd ECO";
    /**
     * Text string for the progress bar
     */
    public static String progressbarSEPStr = "PL Sep";
    /* 
     * Knots label size
     */
    public static int knotsLabelSize = 16;
    /*
     * Paint data on count down panel
     */
    public static boolean paintData = true;
    /**
     * True headning N/A AIS value
     */
    public static int trueHeadingNA = 511;
    /**
     * Draw the legend on the text panel
     */
    public static boolean drawLegend = false;
    /**
     * Draw the text values on the text panel
     */
    public static boolean drawTextValues = false;
    /**
     * Perform a checksum validation on data
     */
    public static boolean useCheckSum = false;
    /**
     * Configuration file name
     */
    public static String configurationFile = "configuration.txt";
    /**
     *  Update GUI deltaT_GUI every msec.
     */
    public static int deltaT_GUI = 100;
    /**
     *  Trigger the log timer every msec
     */
    public static int triggerLogTimer = 500;
    /**
     *  Write to log file every deltaLog msec in case no data has beeing received
     */
    public static long deltaLogTimer = 1000;
    /**
     * Count down timer delta_T
     */
    public static int countDownTimerDelta_T = 1000;
    /**
     * The maximum degree in the zenith panel
     */
    public static int maxDegreeZenithPanel = 20;
    // Degree sign
    public static char degreeChar = 186;
    // Count down clock start button text
    public static String startButtonTxt = "Start";
    // Count down clock hold button text
    public static String holdButtonTxt = "Hold";
    // Count down clock restart button text
    public static String restartButtonTxt = "Restart";
    // Count down clock stop button text
    public static String stopButtonTxt = "Stop";
    // Count down clock reset button text
    public static String resetButtonTxt = "Reset";
    // LES tower string
    public static String LESTowerStr = "LES Tower";
    // Top domestring
    public static String topDomeStr = "Top Dome";
    // 3 ring parachute
    public static String threeRingParachutesStr = "Chutes Dpl";
    // 3 ring release tower string
    public static String threeRingParachutesReleaseStr = "Chutes Rls";
    // up-righting bags tower string
    public static String upRightingBagsStr = "Bags";
    /**
     * AIS column log separator
     */
    public static String AISColumnSeparator = "\t";
    /**
     * N/A string
     */
    public static String naString = "N/A";
    /**
     * Remove vessel which havent been update in removeVesselMinutes (in minutes)
     */
    public static long removeVesselMinutes = 1000 * 60 * 60;
    //public static long removeVesselMinutes = 3000;
    /**
     * A nautical mile in meters
     */
    public static double nauticalMile = 1852.0;
    /**
     * Knots label
     */
    public static String knotsLabel = " Kt";
    /**
     * Latitude label
     */
    public static String latStr = "Lat";
    /**
     * Longitude label
     */
    public static String lonStr = "Lon";
    /**
     * Progress bar indication
     */
    public static int progressbarASS = -1;
    /**
    /**
     * Progress bar indication
     */
    public static int progressbarSTART = -1;
    /**
     * Progress bar indication
     */
    public static int progressbarEND = -1;
    /**
     * The g force
     */
    public static double g = 9.81;
    /** 
     * Latitude and longitude convertion factor fom long to double
     * 
     */
    public static double latLonConvertionFactor = 1000000.0;
    /**
     * Serial port packet length
     */
    public static int packetLength = 21;
    /**
     * Begin sim after this period (in 1/10 sec)
     */
    public static double sinceLastData = 20;
    /**
     * Map corner coordinate
     */
    public static double upperLeftCornerLat = 0.0;
    /**
     * Map corner coordinate
     */
    public static double upperLeftCornerLon = 0.0;
    /**
     * Map corner coordinate
     */
    public static double lowerRightCornerLat = 0.0;
    /**
     * Map corner coordinate
     */
    public static double lowerRightCornerLon = 0.0;
    /**
     * E139 corner
     */
    public static RocketInfo E139NorthWest = new RocketInfo();
    /**
     * E139 corner
     */
    public static RocketInfo E139NorthEast = new RocketInfo();
    /**
     * E139 corner
     */
    public static RocketInfo E139SouthWest = new RocketInfo();
    /**
     * E139 corner
     */
    public static RocketInfo E139SouthEast = new RocketInfo();
    /**
     * Paint ESD139
     */
    public static boolean showESD139 = false;

    /**
     * Degree format
     */
    public enum DegreeFormat {

        DECIMAL, DECIMALMINUTE, DECIMALMINSEC
    };
    /**
     * Actual degree format
     */
    public static DegreeFormat degreeFormat = DegreeFormat.DECIMAL;
    /**
     * North / South
     *
     */
    public static String northSouth = "N";
    /**
     * East / West
     * 
     */
    public static String eastWest = "E";
}
