package com.codernb.setcountdown;

import android.app.Activity;

/**
 * Created by cyril on 05.02.16.
 */
public class Countdown {

    private int countdownTime;
    private int threshold;
    private long startTime;
    private int sets;
    private boolean running;
    private final Activity activity;

    private static Countdown instance;

    private Countdown(Activity activity) {
        this.activity = activity;
        load(activity);
    }

    public static Countdown getInstance(Activity activity) {
        if (instance == null)
            instance = new Countdown(activity);
        return instance;
    }

    private void load(Activity activity) {
        int countdownTime = Preferences.load(activity,
                R.string.countdown_time_save_key,
                R.integer.countdown_time_default);
        int thresholdTime = Preferences.load(activity,
                R.string.threshold_time_save_key,
                R.integer.threshold_time_default);
        sets = Preferences.load(activity, R.string.sets_save_key);
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
        Preferences.save(activity, R.string.sets_save_key, sets);
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void reset() {
        stop();
        sets = 0;
        Preferences.save(activity, R.string.sets_save_key, sets);
    }

    public int getTime() {
        if (!running)
            return countdownTime;
        int time = countdownTime - (int) (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(time, 0);
    }

}
