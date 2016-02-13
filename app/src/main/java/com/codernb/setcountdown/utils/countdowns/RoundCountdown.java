package com.codernb.setcountdown.utils.countdowns;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.Preferences;

/**
 * Created by cyril on 05.02.16.
 */
public class RoundCountdown {

    private final Preferences preferences;

    private int countdownTime;
    private int warningTime;
    private int sets;
    private long startTime;
    private boolean running;
    private boolean repeating;

    public RoundCountdown(Preferences preferences) {
        this.preferences = preferences;
        load();
    }

    private void load() {
        countdownTime = preferences.loadInt(
                R.string.countdown_time_save_key,
                R.integer.countdown_time_default);
        warningTime = preferences.loadInt(
                R.string.threshold_time_save_key,
                R.integer.threshold_time_default);
        sets = preferences.loadInt(R.string.sets_save_key);
        startTime = preferences.loadLong(R.string.countdown_start_time_save_key);
        running = preferences.loadBoolean(R.string.countdown_running_save_key);
        repeating = preferences.loadBoolean(R.string.round_countdown_repeating_save_key);
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
        preferences.save(R.string.countdown_time_save_key, countdownTime);
    }

    public int getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
        preferences.save(R.string.threshold_time_save_key, warningTime);
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
        preferences.save(R.string.sets_save_key, sets);
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
        preferences.save(R.string.round_countdown_repeating_save_key, repeating);
    }

    public boolean isRunning() {
        if (!running)
            return false;
        running = getRemainingTime() > 0;
        return running;
    }

    public boolean isWarning() {
        return isRunning() && getRemainingTime() <= getWarningTime();
    }

    public void start() {
        setSets(sets + 1);
        startTime = System.currentTimeMillis();
        preferences.save(R.string.countdown_start_time_save_key, startTime);
        running = true;
        preferences.save(R.string.countdown_running_save_key, true);
    }

    public void stop() {
        running = false;
        preferences.save(R.string.countdown_running_save_key, false);
    }

    public void reset() {
        stop();
        setSets(0);
    }

    public int getRemainingTime() {
        if (!running)
            return countdownTime;
        int time = countdownTime - (int) (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(time, 0);
    }

}
