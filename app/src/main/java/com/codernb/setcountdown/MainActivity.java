package com.codernb.setcountdown;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by cyril on 04.02.16.
 */
public class MainActivity extends ActionBarActivity {

    public static final int TIME = 12;
    public static final int THRESHOLD = 10;
    public static final int DELAY = 50;

    private static final Countdown COUNTDOWN = Countdown.getInstance();

    private final Handler handler = new Handler();

    private TextView clockView;
    private TextView setsView;
    private Button startButton;
    private Button resetButton;

    private final Runnable countdown = new Runnable() {
        @Override
        public void run() {
            if (!COUNTDOWN.isRunning()) {
                stopCountdown();
                return;
            }
            if (COUNTDOWN.isInThreshold()) {
                clockView.getRootView().setBackgroundColor(Color.RED);
            }
            refreshViews();
            handler.postDelayed(this, DELAY);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWidgeds();
        setStartButtonListener();
        setResetListener();
        startHandler();
    }

    private void getWidgeds() {
        clockView = (TextView) findViewById(R.id.clock);
        setsView = (TextView) findViewById(R.id.sets);
        startButton = (Button) findViewById(R.id.startButton);
        resetButton = (Button) findViewById(R.id.resetButton);
    }

    private void startHandler() {
        if (COUNTDOWN.isRunning())
            handler.postDelayed(countdown, 0);
    }

    private void stopHandler() {
        handler.removeCallbacks(countdown);
    }

    private void startCountdown() {
        COUNTDOWN.setCountdownTime(TIME);
        COUNTDOWN.setThreshold(THRESHOLD);
        COUNTDOWN.start();
        setStopListener();
        startHandler();
    }

    private void stopCountdown() {
        stopHandler();
        COUNTDOWN.stop();
        setStartListener();
        clockView.getRootView().setBackgroundColor(Color.WHITE);
    }

    private void resetCountdown() {
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

    private void refreshViews() {
        int time = COUNTDOWN.getTime();
        int sets = COUNTDOWN.getSets();
        int startButtonText = COUNTDOWN.isRunning() ?
                R.string.stop_countdown : R.string.start_countdown;
        clockView.setText(String.format("%ds", time));
        setsView.setText(String.format("%d Sets", sets));
        startButton.setText(startButtonText);
    }

}
