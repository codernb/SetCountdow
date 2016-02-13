package com.codernb.setcountdown.utils.countdowns;

import android.content.res.Resources;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.Preferences;

/**
 * Created by cyril on 13.02.16.
 */
public class DrinkCountdown {

    private final Preferences preferences;

    private int countdownTime;
    private long startTime;
    private boolean running;

    public DrinkCountdown(Preferences preferences) {
        this.preferences = preferences;
        load();
    }

    private void load() {
        countdownTime = preferences.loadInt(
                R.string.drink_delay_save_key,
                R.integer.drink_delay_default);
        startTime = preferences.loadLong(R.string.drink_delay_start_time_save_key);
        running = preferences.loadBoolean(R.string.drink_delay_running_save_key);
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
        preferences.save(R.string.drink_delay_save_key, countdownTime);
    }

    public boolean isRunning() {
        if (!running)
            return false;
        running = getRemainingTime() > 0;
        return running;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        preferences.save(R.string.drink_delay_start_time_save_key, startTime);
        running = true;
        preferences.save(R.string.drink_delay_running_save_key, true);
    }

    public void stop() {
        running = false;
        preferences.save(R.string.drink_delay_running_save_key, false);
    }

    public int getRemainingTime() {
        if (!running)
            return 0;
        int time = countdownTime - (int) (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(time, 0);
    }

    public String getTimeText(Resources resources) {
        int time = getRemainingTime();
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
