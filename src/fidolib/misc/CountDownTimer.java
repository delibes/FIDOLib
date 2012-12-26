/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.misc;

import fidolib.data.Constants;
import fidolib.data.CountDownData;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Steen Andersen
 */
public class CountDownTimer {

    /**
     * Timer delay
     */
    private int delay = 0;
    /**
     * Timer period for GUI updates
     */
    private int period = 100;
    /**
     * Last time click
     */
    private long lastTimerClick = 0;
    /**
     * The timer it self
     */
    private static Timer timer = new Timer();

    /**
     * Constructor 
     */
    public CountDownTimer() {
    }

    /**
     * Start the timer
     */
    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                long now = (new Date()).getTime() / Constants.countDownTimerDelta_T;
                if ((now - lastTimerClick) >=  1)
                {
                   todo();
                   lastTimerClick = (new Date()).getTime() / Constants.countDownTimerDelta_T;
                }
                
            }
        }, delay, period);
    }

    /**
     * What to do when there is a timer click
     */
    public void todo() {
        updateCountDown();


    }

    /**
     * Update the count down clock
     */
    public void updateCountDown() {
        if (CountDownData.countDownRunning == true) {

            Calendar calender = Calendar.getInstance();
            long now = calender.getTime().getTime();
            long remainingTime = CountDownData.startCountDown - (now - CountDownData.countDownTimeStamp);
            CountDownData.remainingTime = remainingTime;

            


        }
    }
}
