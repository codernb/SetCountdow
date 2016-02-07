package com.codernb.setcountdown;

import android.app.Activity;
import android.content.res.Resources;

/**
 * Created by cyril on 05.02.16.
 */
public class Countdown {

    private int countdownTime;
    private int threshold;
    private long startTime;
    private int sets;
    private boolean running;

    private static Countdown instance;

    private Countdown() {
        ;
    }

    public static Countdown getInstance(Activity activity) {
        if (instance == null) {
            instance = new Countdown();
            instance.load(activity);
        }
        return instance;
    }

    private void load(Activity activity) {
        int countdownTime = Preferences.load(activity,
                R.string.countdown_time_save_key,
                R.integer.countdown_time_default);
        int thresholdTime = Preferences.load(activity,
                R.string.default_threshold_time,
                R.integer.threshold_time_default);
        setCountdownTime(countdownTime);
        setThreshold(thresholdTime);
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getSets() {
        return sets;
    }

    public boolean isRunning() {
        if (!running)
            return false;
        running = getTime() > 0;
        return running;
    }

    public boolean isInThreshold() {
        return isRunning() && getTime() <= getThreshold();
    }

    public void start() {
        sets++;
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void reset() {
        stop();
        sets = 0;
    }

    public int getTime() {
        if (!running)
            return countdownTime;
        int time = countdownTime - (int) (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(time, 0);
    }

}
