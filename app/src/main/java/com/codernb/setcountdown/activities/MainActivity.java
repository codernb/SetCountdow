package com.codernb.setcountdown.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.popups.ClockPopup;
import com.codernb.setcountdown.popups.Popup.Callback;
import com.codernb.setcountdown.popups.SetsPopup;
import com.codernb.setcountdown.utils.Countdown;
import com.codernb.setcountdown.utils.Preferences;

/**
 * Created by cyril on 04.02.16.
 */
public class MainActivity extends ActionBarActivity {

    private Countdown countdown;
    private static final Handler HANDLER = new Handler();

    private int clockRefreshDelay;
    private int countdownEndToneDuration;
    private int countdownEndVibrationDuration;
    private int thresholdReachedToneDuration;
    private int thresholdReachedVibrationRepeat;
    private long[] thresholdVibratePattern;
    private boolean thresholdReached;
    private int volume;
    private int volumeSteps;

    private ClockPopup clockPopup;
    private SetsPopup setsPopup;

    private Vibrator vibrator;
    private Resources resources;

    private TextView clockView;
    private TextView setsView;
    private TextView volumeView;
    private Button startButton;
    private Button resetButton;
    private Button volumePlusButton;
    private Button volumeMinusButton;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            runCountdown(this);
        }
    };

    private final OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startCountdown();
        }
    };

    private final OnClickListener stopListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            stopCountdown();
            refreshViews();
        }
    };

    private final OnClickListener resetListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            resetCountdown();
            refreshViews();
        }
    };

    private final OnClickListener volumeUpListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            increaseVolume();
            refreshViews();
        }
    };

    private final OnClickListener volumeDownListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            decreaseVolume();
            refreshViews();
        }
    };

    private final OnLongClickListener timeSetListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            clockPopup.show();
            return true;
        }
    };

    private final OnLongClickListener setsSetListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            setsPopup.show();
            return true;
        }
    };

    private final Callback popupCallback = new Callback() {
        @Override
        public void onOK() {
            refreshViews();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countdown = Countdown.getInstance(this);
        resources = getResources();
        vibrator = getVibrator();
        clockPopup = getClockCallback();
        setsPopup = getSetsCallback();
        initializeWidgets();
        initializeButtons();
        initializeValues();
        initializeThresholReached();
        startHandler();
        refreshViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
        clockPopup.dismiss();
        setsPopup.dismiss();
    }

    private void initializeWidgets() {
        clockView = (TextView) findViewById(R.id.clock);
        setsView = (TextView) findViewById(R.id.sets);
        startButton = (Button) findViewById(R.id.start_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        volumePlusButton = (Button) findViewById(R.id.volume_plus_button);
        volumeMinusButton = (Button) findViewById(R.id.volume_minus_button);
        volumeView = (TextView) findViewById(R.id.volume_view);
    }

    private void initializeButtons() {
        updateStartListenerOn(startButton);
        setResetListenerOn(resetButton);
        setTimeSetListenerOn(clockView);
        setSetsSetListenerOn(setsView);
        setVolumeUpListenerOn(volumePlusButton);
        setVolumeDownListenerOn(volumeMinusButton);
    }

    private void initializeValues() {
        clockRefreshDelay = resources.getInteger(R.integer.clock_refresh_delay);
        countdownEndToneDuration = resources.getInteger(R.integer.countdown_end_tone_duration);
        countdownEndVibrationDuration = resources.getInteger(R.integer.countdown_end_vibration_duration);
        thresholdReachedToneDuration = resources.getInteger(R.integer.threshold_reached_tone_duration);
        thresholdReachedVibrationRepeat = resources.getInteger(R.integer.threshold_reached_vibration_repeat);
        thresholdVibratePattern = getThresholdVibratePattern();
        volume = Preferences.load(this, R.string.volume_save_key, R.integer.volume_default);
        volumeSteps = resources.getInteger(R.integer.volume_steps);
    }

    private void initializeThresholReached() {
        if (countdown.isInThreshold()) {
            thresholdReached = true;
            setBackgroundColor(Color.RED);
        } else {
            setBackgroundColor(Color.WHITE);
        }
    }

    private void startHandler() {
        if (countdown.isRunning())
            HANDLER.postDelayed(runnable, 0);
    }

    private void stopHandler() {
        HANDLER.removeCallbacks(runnable);
    }

    private void startCountdown() {
        thresholdReached = false;
        countdown.start();
        setStopListenerOn(startButton);
        startHandler();
    }

    private void stopCountdown() {
        stopHandler();
        countdown.stop();
        setStartListenerOn(startButton);
        setBackgroundColor(Color.WHITE);
    }

    private void increaseVolume() {
        int newVolume = volume + volumeSteps;
        if (newVolume <= 100)
            volume = newVolume;
    }

    private void decreaseVolume() {
        int newVolume = volume - volumeSteps;
        if (newVolume >= 0)
            volume = newVolume;
    }

    private void setBackgroundColor(int color) {
        clockView.getRootView().setBackgroundColor(color);
    }

    private void resetCountdown() {
        stopCountdown();
        countdown.reset();
    }

    private void updateStartListenerOn(Button button) {
        if (countdown.isRunning())
            setStopListenerOn(button);
        else
            setStartListenerOn(button);
    }

    private void setStartListenerOn(Button button) {
        button.setOnClickListener(startListener);
    }

    private void setStopListenerOn(Button button) {
        button.setOnClickListener(stopListener);
    }

    private void setResetListenerOn(Button button) {
        button.setOnClickListener(resetListener);
    }

    private void setTimeSetListenerOn(View view) {
        view.setOnLongClickListener(timeSetListener);
    }

    private void setSetsSetListenerOn(View view) {
        view.setOnLongClickListener(setsSetListener);
    }

    private void setVolumeUpListenerOn(Button button) {
        button.setOnClickListener(volumeUpListener);
    }

    private void setVolumeDownListenerOn(Button button) {
        button.setOnClickListener(volumeDownListener);
    }

    private void refreshViews() {
        refreshClockOn(clockView);
        refreshStartTextOn(startButton);
        refreshSetsOn(setsView);
        refreshVolumeOn(volumeView);
    }

    private void refreshClockOn(TextView textView) {
        int time = countdown.getTime();
        textView.setText(String.format("%ds", time));
    }

    private void refreshStartTextOn(Button button) {
        int startButtonText = countdown.isRunning() ?
                R.string.stop_countdown : R.string.start_countdown;
        button.setText(startButtonText);
    }

    private void refreshSetsOn(TextView textView) {
        int sets = countdown.getSets();
        textView.setText(String.format("%d %s", sets, sets == 1 ?
                resources.getString(R.string.set_singular) :
                resources.getString(R.string.set_plural)));
    }

    private void refreshVolumeOn(TextView textView) {
        textView.setText(String.format("%d%s", volume, "%"));
    }

    private void signalCountdownEnd() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, countdownEndToneDuration);
        vibrator.vibrate(countdownEndVibrationDuration);
    }

    private void signalThresholdReached() {
        setBackgroundColor(Color.RED);
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, thresholdReachedToneDuration);
        vibrator.vibrate(thresholdVibratePattern, thresholdReachedVibrationRepeat);
    }

    private Vibrator getVibrator() {
        return (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private long[] getThresholdVibratePattern() {
        int[] ints = resources.getIntArray(R.array.threshold_reached_vibration_pattern);
        long[] thresholdVibratePattern = new long[ints.length];
        for (int i = 0; i < ints.length; i++)
            thresholdVibratePattern[i] = (long) ints[i];
        return thresholdVibratePattern;
    }

    private ClockPopup getClockCallback() {
        return new ClockPopup(this, countdown, popupCallback);
    }

    private SetsPopup getSetsCallback() {
        return new SetsPopup(this, countdown, popupCallback);
    }

    private void runCountdown(Runnable runnable) {
        if (!countdown.isRunning()) {
            stopCountdown();
            signalCountdownEnd();
            return;
        }
        if (!thresholdReached && countdown.isInThreshold()) {
            signalThresholdReached();
            thresholdReached = true;
        }
        refreshViews();
        HANDLER.postDelayed(runnable, clockRefreshDelay);
    }

}
