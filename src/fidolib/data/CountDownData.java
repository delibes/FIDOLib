/*
 * This class contains information about the count down clock. That is the
 * initial count down time, hold time stamp etc.
 */
package fidolib.data;

/**
 *
 * @author Steen Andersen
 */
public class CountDownData {

    /**
     * One second constant = 1000 msec.
     */
    public static long sec = 1000;
    /**
     * One minute constant = 60 sec.
     */
    public static long minute = 60 * sec;
    /**
     * Count down time
     */
    public static long startCountDown = 1 * minute;
    /**
     * The time the clock should be reset to.
     */
    public static long resetTo = startCountDown;
    /**
     * The time of pressing the hold button
     */
    public static long holdTime = -1;
    /**
     * Is the count down clock running
     */
    public static boolean countDownRunning = false;
    /**
     * Is the count down clock on hold
     */
    public static boolean countDownHold = false;
    /**
     * Time stamp of when start / restart took place
     */
    public static long countDownTimeStamp = 0;
    /**
     * The remaining time of the count down;
     */
    public static long remainingTime = startCountDown;

    public enum PresetComboBox {

        THREESEC(" 3 sec.", 3), ONE(" 1 min.", 60), THREE(" 3 min.", 180),
        TEN(" 10 min.", 600), TWENTY(" 20 min.", 1200), THIRTY(" 30 min.", 1800), FOURTY(" 40 min.", 2400),
        FIFTY(" 50 min.", 3000), SIXTY(" 60 min.", 3600);
        private final String label;
        private final int milliSeconds;

        private PresetComboBox(String label, int milliSeconds) {
            this.label = label;
            this.milliSeconds = milliSeconds;
        }

        public static String[] enumsToStringArray() {
            String[] results = new String[PresetComboBox.values().length];
            int count = 0;
            for (PresetComboBox each : PresetComboBox.values()) {
                results[count] = each.label;
                count++;
            }
            return results;
        }

        public static PresetComboBox findByLabel(String label) {
            for (PresetComboBox value : values()) {
                if (value.label.equals((label))) {
                    return value;
                }
            }
            return null; // or throw exception?
        }

        public int getTime() {
            return milliSeconds * 1000;
        }
    };

    public static String getMinSec(int sec) {
        if (sec >= 0) {
            String secondsStr = String.format("%02d", (sec % 60));
            String minutesStr = String.format("%02d", (sec / 60));
            String minSec = "+" + minutesStr + ":" + secondsStr;
            return minSec;
        } else {
            int secAbs = Math.abs(sec);
            String secondsStr = String.format("%02d", (secAbs % 60));
            String minutesStr = String.format("%02d", (secAbs / 60));
            String minSec = "-" + minutesStr + ":" + secondsStr;
            return minSec;
        }
    }
}
