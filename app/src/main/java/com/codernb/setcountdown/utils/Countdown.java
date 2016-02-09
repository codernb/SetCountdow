package com.codernb.setcountdown.utils;

import android.app.Activity;

import com.codernb.setcountdown.R;

/**
 * Created by cyril on 05.02.16.
 */
public class Countdown {

    private int countdownTime;
    private int threshold;
    private long startTime;
    private int sets;
    private int drinkDelay;
    private boolean running;
    private Activity activity;

    private static Countdown instance;

    private Countdown(Activity activity) {
        this.activity = activity;
        load(activity);
    }

    public static Countdown getInstance(Activity activity) {
        if (instance == null)
            instance = new Countdown(activity);
        else
            instance.activity = activity;
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
        drinkDelay = Preferences.load(activity,
                R.string.drink_delay_save_key,
                R.integer.drink_delay_default);
        setCountdownTime(countdownTime);
        setThreshold(thresholdTime);
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
        Preferences.save(activity, R.string.countdown_time_save_key, countdownTime);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        Preferences.save(activity, R.string.threshold_time_save_key, threshold);
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
        Preferences.save(activity, R.string.sets_save_key, sets);
    }

    public int getDrinkDelay() {
        return drinkDelay;
    }

    public void setDrinkDelay(int drinkDelay) {
        this.drinkDelay = drinkDelay;
        Preferences.save(activity, R.string.drink_delay_save_key, drinkDelay);
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
        setSets(getSets() + 1);
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void reset() {
        stop();
        setSets(0);
    }

    public int getTime() {
        if (!running)
            return countdownTime;
        int time = countdownTime - (int) (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(time, 0);
    }

}
