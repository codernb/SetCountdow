package com.codernb.setcountdown.utils.signalers;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.Preferences;

/**
 * Created by cyril on 12.02.16.
 */
public class Signaller {

    private final Activity activity;
    private final Preferences preferences;
    private final View rootView;
    private final int volumeSteps;
    private final Vibrator vibrator;
    private final Speaker speaker;

    private int volume;

    public Signaller(Activity activity, Preferences preferences) {
        this.preferences = preferences;
        this.activity = activity;
        Resources resources = activity.getResources();
        rootView = getRootView();
        volume = preferences.load(R.string.volume_save_key, R.integer.volume_default);
        volumeSteps = resources.getInteger(R.integer.volume_steps);
        vibrator = new Vibrator(activity);
        speaker = new Speaker(resources);
    }

    public int getVolume() {
        return volume;
    }

    public void signalCountdownEnd() {
        rootView.setBackgroundColor(Color.WHITE);
        speaker.countdownEnd(volume);
        vibrator.countdownEnd();
    }

    public void signalThresholdReached() {
        getRootView().setBackgroundColor(Color.RED);
        speaker.thresholdReached(volume);
        vibrator.thresholdReached();
    }

    public void increaseVolume() {
        int newVolume = volume + volumeSteps;
        if (newVolume > 100)
            return;
        volume = newVolume;
        preferences.save(R.string.volume_save_key, volume);
    }

    public void decreaseVolume() {
        int newVolume = volume - volumeSteps;
        if (newVolume >= 0)
            volume = newVolume;
        preferences.save(R.string.volume_save_key, volume);
    }

    private View getRootView() {
        return activity.findViewById(android.R.id.content);
    }

}
