package com.codernb.setcountdown.utils.signalers;

import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;

import com.codernb.setcountdown.R;

/**
 * Created by cyril on 12.02.16.
 */
public class Speaker {

    private final int countdownEndToneDuration;
    private final int thresholdReachedToneDuration;

    public Speaker(Resources resources) {
        countdownEndToneDuration = resources.getInteger(R.integer.countdown_end_tone_duration);
        thresholdReachedToneDuration = resources.getInteger(R.integer.threshold_reached_tone_duration);
    }

    public void countdownEnd(int volume) {
        getToneGenerator(volume).startTone(ToneGenerator.TONE_CDMA_PIP, countdownEndToneDuration);
    }

    public void thresholdReached(int volume) {
        getToneGenerator(volume).startTone(ToneGenerator.TONE_CDMA_PIP, thresholdReachedToneDuration);
    }

    private ToneGenerator getToneGenerator(int volume) {
        return new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
    }

}
