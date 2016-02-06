package com.codernb.setcountdown;

/**
 * Created by cyril on 05.02.16.
 */
public class Countdown {

    private int countdownTime;
    private int threshold;
    private long startTime;
    private int sets;
    private boolean running;

    private static final Countdown instance = new Countdown();

    private Countdown() {
        ;
    }

    public static Countdown getInstance() {
        return instance;
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
        if (!isRunning())
            return false;
        return getTime() <= getThreshold();
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
