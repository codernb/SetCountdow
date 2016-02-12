package com.codernb.setcountdown.utils;

import android.app.Activity;

import com.codernb.setcountdown.R;

/**
 * Created by cyril on 05.02.16.
 */
public class Countdown {

    private final Preferences preferences;

    private int countdownTime;
    private int threshold;
    private long startTime;
    private long drinkStartTime;
    private int sets;
    private int drinkDelay;
    private boolean running;
    private boolean drinkDelayRunning;

    public Countdown(Preferences preferences) {
        this.preferences = preferences;
        load();
    }

    private void load() {
        int countdownTime = preferences.load(
                R.string.countdown_time_save_key,
                R.integer.countdown_time_default);
        int thresholdTime = preferences.load(
                R.string.threshold_time_save_key,
                R.integer.threshold_time_default);
        sets = preferences.load(R.string.sets_save_key);
        drinkDelay = preferences.load(
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
        preferences.save(R.string.countdown_time_save_key, countdownTime);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        preferences.save(R.string.threshold_time_save_key, threshold);
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
        preferences.save(R.string.sets_save_key, sets);
    }

    public int getDrinkDelay() {
        return drinkDelay;
    }

    public void setDrinkDelay(int drinkDelay) {
        this.drinkDelay = drinkDelay;
        preferences.save(R.string.drink_delay_save_key, drinkDelay);
    }

    public boolean isRunning() {
        if (!running)
            return false;
        running = getTime() > 0;
        return running;
    }

    public boolean isDrinkDelayRunning() {
        if (!drinkDelayRunning)
            return false;
        drinkDelayRunning = getDrinkDelayTime() > 0;
        return drinkDelayRunning;
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

    public void startDrinkDelay() {
        drinkStartTime = System.currentTimeMillis();
        drinkDelayRunning = true;
    }

    public void stopDrinkDelay() {
        drinkDelayRunning = false;
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

    public int getDrinkDelayTime() {
        if (!drinkDelayRunning)
            return 0;
        int time = drinkDelay - (int) (System.currentTimeMillis() - drinkStartTime) / 1000;
        return Math.max(time, 0);
    }

}
