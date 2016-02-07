package com.codernb.setcountdown;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by cyril on 04.02.16.
 */
public class MainActivity extends ActionBarActivity {

    private static final Countdown COUNTDOWN = Countdown.getInstance();
    private static final Handler HANDLER = new Handler();

    private int clockRefreshDelay;
    private int countdownEndToneDuration;
    private int countdownEndVibrationDuration;
    private int thresholdReachedToneDuration;
    private int thresholdReachedVibrationRepeat;
    private long[] thresholdVibratePattern;
    private boolean thresholdReached;

    private ClockPopup clockPopup;

    private Vibrator vibrator;
    private ToneGenerator toneGenerator;
    private Resources resources;

    private TextView clockView;
    private TextView setsView;
    private Button startButton;
    private Button resetButton;

    private final Runnable countdown = new Runnable() {
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
        }
    };

    private final OnClickListener resetListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            resetCountdown();
        }
    };

    AlertDialog.Builder alertDialogBuilder;

    LayoutInflater layoutInflater;
    private final OnLongClickListener timeSetListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            clockPopup.show();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();
        vibrator = getVibrator();
        clockPopup = getClockCallback();
        initializeWidgets();
        initializeButtons();
        initializeValues();
        initializeThresholReached();
        startHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
        clockPopup.dismiss();
    }

    private void initializeWidgets() {
        clockView = (TextView) findViewById(R.id.clock);
        setsView = (TextView) findViewById(R.id.sets);
        startButton = (Button) findViewById(R.id.start_button);
        resetButton = (Button) findViewById(R.id.reset_button);
    }

    private void initializeButtons() {
        updateStartListenerOn(startButton);
        setResetListenerOn(resetButton);
        setTimeSetListenerOn(clockView);
    }

    private void initializeValues() {
        clockRefreshDelay = resources.getInteger(R.integer.clock_refresh_delay);
        countdownEndToneDuration = resources.getInteger(R.integer.countdown_end_tone_duration);
        countdownEndVibrationDuration = resources.getInteger(R.integer.countdown_end_vibration_duration);
        thresholdReachedToneDuration = resources.getInteger(R.integer.threshold_reached_tone_duration);
        thresholdReachedVibrationRepeat = resources.getInteger(R.integer.threshold_reached_vibration_repeat);
        thresholdVibratePattern = getThresholdVibratePattern();
        int volume = resources.getInteger(R.integer.volume);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
    }

    private void initializeThresholReached() {
        if (COUNTDOWN.isInThreshold()) {
            thresholdReached = true;
            setBackgroundColor(Color.RED);
        }
    }

    private void startHandler() {
        if (COUNTDOWN.isRunning())
            HANDLER.postDelayed(countdown, 0);
    }

    private void stopHandler() {
        HANDLER.removeCallbacks(countdown);
    }

    private void startCountdown() {
        thresholdReached = false;
        COUNTDOWN.start();
        setStopListenerOn(startButton);
        startHandler();
    }

    private void stopCountdown() {
        stopHandler();
        COUNTDOWN.stop();
        setStartListenerOn(startButton);
        setBackgroundColor(Color.WHITE);
    }

    private void setBackgroundColor(int color) {
        clockView.getRootView().setBackgroundColor(color);
    }

    private void resetCountdown() {
        stopCountdown();
        COUNTDOWN.reset();
        refreshViews();
    }

    private void updateStartListenerOn(Button button) {
        if (COUNTDOWN.isRunning())
            setStopListenerOn(button);
        else
            setStartListenerOn(button);
    }

    private void setStartListenerOn(Button button) {
        button.setOnClickListener(startListener);
        refreshViews();
    }

    private void setStopListenerOn(Button button) {
        button.setOnClickListener(stopListener);
        refreshViews();
    }

    private void setResetListenerOn(Button button) {
        button.setOnClickListener(resetListener);
    }

    private void setTimeSetListenerOn(View view) {
        view.setOnLongClickListener(timeSetListener);
    }

    private void refreshViews() {
        refreshClockOn(clockView);
        refreshStartTextOn(startButton);
        refreshSetsOn(setsView);
    }

    private void refreshClockOn(TextView textView) {
        int time = COUNTDOWN.getTime();
        textView.setText(String.format("%ds", time));
    }

    private void refreshStartTextOn(Button button) {
        int startButtonText = COUNTDOWN.isRunning() ?
                R.string.stop_countdown : R.string.start_countdown;
        button.setText(startButtonText);
    }

    private void refreshSetsOn(TextView textView) {
        int sets = COUNTDOWN.getSets();
        textView.setText(String.format("%d %s", sets, sets == 1 ?
                resources.getString(R.string.set_singular) :
                resources.getString(R.string.set_plural)));
    }

    private void signalCountdownEnd() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, countdownEndToneDuration);
        vibrator.vibrate(countdownEndVibrationDuration);
    }

    private void signalThresholdReached() {
        setBackgroundColor(Color.RED);
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
        return new ClockPopup(this, COUNTDOWN, new ClockPopup.Callback() {
            @Override
            public void onOK() {
                refreshViews();
            }
        });
    }

    private void runCountdown(Runnable runnable) {
        if (!COUNTDOWN.isRunning()) {
            stopCountdown();
            signalCountdownEnd();
            return;
        }
        if (!thresholdReached && COUNTDOWN.isInThreshold()) {
            signalThresholdReached();
            thresholdReached = true;
        }
        refreshViews();
        HANDLER.postDelayed(runnable, clockRefreshDelay);
    }

}
