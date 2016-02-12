package com.codernb.setcountdown.utils.signalers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.codernb.setcountdown.R;

/**
 * Created by cyril on 12.02.16.
 */
public class Vibrator {

    private final android.os.Vibrator vibrator;
    private final int countdownEndVibrationDuration;
    private final int thresholdReachedVibrationRepeat;
    private final long[] thresholdVibratePattern;

    public Vibrator(Activity activity) {
        Resources resources = activity.getResources();
        countdownEndVibrationDuration = resources.getInteger(R.integer.countdown_end_vibration_duration);
        thresholdReachedVibrationRepeat = resources.getInteger(R.integer.threshold_reached_vibration_repeat);
        thresholdVibratePattern = getThresholdVibratePattern(resources);
        vibrator = getVibrator(activity);
    }

    public void countdownEnd() {
        vibrator.vibrate(countdownEndVibrationDuration);
    }

    public void thresholdReached() {
        vibrator.vibrate(thresholdVibratePattern, thresholdReachedVibrationRepeat);
    }

    private android.os.Vibrator getVibrator(Activity activity) {
        return (android.os.Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private long[] getThresholdVibratePattern(Resources resources) {
        int[] ints = resources.getIntArray(R.array.threshold_reached_vibration_pattern);
        long[] thresholdVibratePattern = new long[ints.length];
        for (int i = 0; i < ints.length; i++)
            thresholdVibratePattern[i] = (long) ints[i];
        return thresholdVibratePattern;
    }

}
