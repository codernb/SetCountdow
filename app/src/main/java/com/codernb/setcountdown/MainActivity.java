package com.codernb.setcountdown;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by cyril on 04.02.16.
 */
public class MainActivity extends ActionBarActivity {

    public static final int TIME = 60;
    public static final int THRESHOLD = 10;
    public static final int CLOCK_REFRESH_DELAY = 50;

    private static final Countdown COUNTDOWN = Countdown.getInstance();

    private static final Handler HANDLER = new Handler();
    private Vibrator vibrator;
    private static final long[] THRESHOLD_VIBRATE_PATTERN = {0, 500, 200, 500};
    private boolean thresholdReached;

    private static final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);

    private TextView clockView;
    private TextView setsView;
    private Button startButton;
    private Button resetButton;

    private final Runnable countdown = new Runnable() {
        @Override
        public void run() {
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
            HANDLER.postDelayed(this, CLOCK_REFRESH_DELAY);
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
            final View clockSetView = layoutInflater.inflate(R.layout.clock_popup, null);
            final EditText countdownTimeView = (EditText) clockSetView.findViewById(R.id.set_countdown_time);
            final EditText thresholdTimeView = (EditText) clockSetView.findViewById(R.id.set_threshold_time);
            if (COUNTDOWN.getCountdownTime() > 0)
                countdownTimeView.setText(String.format("%d", COUNTDOWN.getCountdownTime()));
            if (COUNTDOWN.getThreshold() > 0)
                thresholdTimeView.setText(String.format("%d", COUNTDOWN.getThreshold()));

            //TODO: Refactor to own class
            alertDialogBuilder.setView(clockSetView)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int countdownTime = Integer.parseInt(countdownTimeView.getText().toString());
                            int thresholdTime = Integer.parseInt(thresholdTimeView.getText().toString());
                            COUNTDOWN.setCountdownTime(countdownTime);
                            COUNTDOWN.setThreshold(thresholdTime);
                            refreshViews();
                        }
                    })
                    .create()
                    .show();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWidgets();
        setStartButtonListener();
        setResetListener();
        setTimeSetListener();

        //TODO: Find better solution
        if (COUNTDOWN.isInThreshold()) {
            thresholdReached = true;
            setBackgroundColor(Color.RED);
        }

        startHandler();
        vibrator = getVibrator();

        //TODO: Refactor to own class
        alertDialogBuilder = new AlertDialog.Builder(this);
        layoutInflater = LayoutInflater.from(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
    }

    private void getWidgets() {
        clockView = (TextView) findViewById(R.id.clock);
        setsView = (TextView) findViewById(R.id.sets);
        startButton = (Button) findViewById(R.id.start_button);
        resetButton = (Button) findViewById(R.id.reset_button);
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
        setStopListener();
        startHandler();
    }

    private void stopCountdown() {
        stopHandler();
        COUNTDOWN.stop();
        setStartListener();
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

    private void setStartButtonListener() {
        if (COUNTDOWN.isRunning())
            setStopListener();
        else
            setStartListener();
    }

    private void setStartListener() {
        startButton.setOnClickListener(startListener);
        refreshViews();
    }

    private void setStopListener() {
        startButton.setOnClickListener(stopListener);
        refreshViews();
    }

    private void setResetListener() {
        resetButton.setOnClickListener(resetListener);
    }

    private void setTimeSetListener() {
        clockView.setOnLongClickListener(timeSetListener);
    }

    private void refreshViews() {
        int time = COUNTDOWN.getTime();
        int sets = COUNTDOWN.getSets();
        int startButtonText = COUNTDOWN.isRunning() ?
                R.string.stop_countdown : R.string.start_countdown;
        clockView.setText(String.format("%ds", time));
        setsView.setText(String.format("%d Sets", sets));
        startButton.setText(startButtonText);
    }

    private void signalCountdownEnd() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        vibrator.vibrate(1000);
    }

    private void signalThresholdReached() {
        setBackgroundColor(Color.RED);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 1000);
        vibrator.vibrate(THRESHOLD_VIBRATE_PATTERN, -1);
    }

    private Vibrator getVibrator() {
        return (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

}
