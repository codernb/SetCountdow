package com.codernb.setcountdown.utils;

import android.content.res.Resources;

import com.codernb.setcountdown.R;

/**
 * Created by cyril on 05.02.16.
 */
public class Countdown {

    private final Preferences preferences;

    private int countdownTime;
    private int thresholdTime;
    private int drinkDelayTime;
    private int sets;
    private long countdownStartTime;
    private long drinkDelayStartTime;
    private boolean countdownRunning;
    private boolean drinkDelayRunning;

    public Countdown(Preferences preferences) {
        this.preferences = preferences;
        load();
    }

    private void load() {
        countdownTime = preferences.loadInt(
                R.string.countdown_time_save_key,
                R.integer.countdown_time_default);
        thresholdTime = preferences.loadInt(
                R.string.threshold_time_save_key,
                R.integer.threshold_time_default);
        sets = preferences.loadInt(R.string.sets_save_key);
        drinkDelayTime = preferences.loadInt(
                R.string.drink_delay_save_key,
                R.integer.drink_delay_default);
        drinkDelayStartTime = preferences.loadLong(R.string.drink_delay_start_time_save_key);
        countdownStartTime = preferences.loadLong(R.string.countdown_start_time_save_key);
        countdownRunning = preferences.loadBoolean(R.string.countdown_running_save_key);
        drinkDelayRunning = preferences.loadBoolean(R.string.drink_delay_running_save_key);
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
        preferences.save(R.string.countdown_time_save_key, countdownTime);
    }

    public int getThresholdTime() {
        return thresholdTime;
    }

    public void setThresholdTime(int thresholdTime) {
        this.thresholdTime = thresholdTime;
        preferences.save(R.string.threshold_time_save_key, thresholdTime);
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
        preferences.save(R.string.sets_save_key, sets);
    }

    public int getDrinkDelayTime() {
        return drinkDelayTime;
    }

    public void setDrinkDelayTime(int drinkDelayTime) {
        this.drinkDelayTime = drinkDelayTime;
        preferences.save(R.string.drink_delay_save_key, drinkDelayTime);
    }

    public boolean isCountdownRunning() {
        if (!countdownRunning)
            return false;
        countdownRunning = getRemainingCountdownTime() > 0;
        return countdownRunning;
    }

    public boolean isDrinkDelayRunning() {
        if (!drinkDelayRunning)
            return false;
        drinkDelayRunning = getRemainingDrinkDelayTime() > 0;
        return drinkDelayRunning;
    }

    public boolean isInThreshold() {
        return isCountdownRunning() && getRemainingCountdownTime() <= getThresholdTime();
    }

    public void startCountdown() {
        setSets(sets + 1);
        countdownStartTime = System.currentTimeMillis();
        preferences.save(R.string.countdown_start_time_save_key, countdownStartTime);
        countdownRunning = true;
        preferences.save(R.string.countdown_running_save_key, true);
    }

    public void stopCountdown() {
        countdownRunning = false;
        preferences.save(R.string.countdown_running_save_key, false);
    }

    public void startDrinkDelay() {
        drinkDelayStartTime = System.currentTimeMillis();
        preferences.save(R.string.drink_delay_start_time_save_key, drinkDelayStartTime);
        drinkDelayRunning = true;
        preferences.save(R.string.drink_delay_running_save_key, true);
    }

    public void stopDrinkDelay() {
        drinkDelayRunning = false;
        preferences.save(R.string.drink_delay_running_save_key, false);
    }

    public void reset() {
        stopCountdown();
        setSets(0);
    }

    public int getRemainingCountdownTime() {
        if (!countdownRunning)
            return countdownTime;
        int time = countdownTime - (int) (System.currentTimeMillis() - countdownStartTime) / 1000;
        return Math.max(time, 0);
    }

    public int getRemainingDrinkDelayTime() {
        if (!drinkDelayRunning)
            return 0;
        int time = drinkDelayTime - (int) (System.currentTimeMillis() - drinkDelayStartTime) / 1000;
        return Math.max(time, 0);
    }

    public String getDrinkDelayText(Resources resources) {
        int time = getRemainingDrinkDelayTime();
        String timeText;
        if (time >= 120) {
            return String.format("%d\n%s", time / 60, resources.getString(R.string.minutes_short_plural));
        }
        if (time >= 60) {
            return String.format("%d\n%s", time / 60, resources.getString(R.string.minutes_short_singular));
        }
        return String.format("%d%s", time, resources.getString(R.string.seconds_short));
    }

}
