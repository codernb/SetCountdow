package com.codernb.setcountdown.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.popups.ClockPopup;
import com.codernb.setcountdown.popups.DrinkPopup;
import com.codernb.setcountdown.popups.Popup.Callback;
import com.codernb.setcountdown.popups.SetsPopup;
import com.codernb.setcountdown.utils.Countdown;
import com.codernb.setcountdown.utils.Preferences;
import com.codernb.setcountdown.utils.signalers.Signaller;

/**
 * Created by cyril on 04.02.16.
 */
public class MainActivity extends ActionBarActivity {

    private static final Handler HANDLER = new Handler();
    private int clockRefreshDelay;

    private boolean thresholdReached;

    private Preferences preferences;
    private Countdown countdown;
    private Resources resources;
    private ClockPopup clockPopup;
    private SetsPopup setsPopup;
    private DrinkPopup drinkPopup;
    private Signaller signaller;

    private TextView timeView;
    private TextView setsView;
    private TextView volumeView;
    private TextView drinkDelayView;
    private Button startButton;
    private Button resetButton;
    private Button volumeUpButton;
    private Button volumeDownButton;
    private ImageButton drinkButton;

    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            runCountdown(this);
        }
    };

    private final Runnable drinkRunnable = new Runnable() {
        @Override
        public void run() {
            runDrinkCountdown(this);
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
            signaller.increaseVolume();
            refreshViews();
        }
    };

    private final OnClickListener volumeDownListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            signaller.decreaseVolume();
            refreshViews();
        }
    };

    private final OnClickListener drinkListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startDrinkCountdown();
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

    private final OnLongClickListener drinkSetListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            drinkPopup.show();
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
        preferences = new Preferences(this);
        countdown = new Countdown(preferences);
        resources = getResources();
        clockPopup = getClockCallback();
        setsPopup = getSetsCallback();
        drinkPopup = getDrinkCallback();
        signaller = new Signaller(this,preferences);
        initializeWidgets();
        initializeButtons();
        initializeValues();
        initializeThresholdReached();
        initializeDrinkButton();
        startHandler();
        startDrinkDelayHandler();
        refreshViews();
    }

    @Override
    public void onBackPressed() {
        ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
        stopDrinkDelayHandler();
        clockPopup.dismiss();
        setsPopup.dismiss();
        drinkPopup.dismiss();
    }

    private void initializeWidgets() {
        timeView = (TextView) findViewById(R.id.clock);
        setsView = (TextView) findViewById(R.id.sets);
        startButton = (Button) findViewById(R.id.start_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        volumeUpButton = (Button) findViewById(R.id.volume_plus_button);
        volumeDownButton = (Button) findViewById(R.id.volume_minus_button);
        volumeView = (TextView) findViewById(R.id.volume_view);
        drinkButton = (ImageButton) findViewById(R.id.drink_button);
        drinkDelayView = (TextView) findViewById(R.id.drink_countdown);
    }

    private void initializeButtons() {
        updateStartButtonListener();
        resetButton.setOnClickListener(resetListener);
        timeView.setOnLongClickListener(timeSetListener);
        setsView.setOnLongClickListener(setsSetListener);
        volumeUpButton.setOnClickListener(volumeUpListener);
        volumeDownButton.setOnClickListener(volumeDownListener);
        drinkButton.setOnClickListener(drinkListener);
        drinkButton.setOnLongClickListener(drinkSetListener);
    }

    private void initializeValues() {
        clockRefreshDelay = resources.getInteger(R.integer.clock_refresh_delay);
    }

    private void initializeThresholdReached() {
        if (countdown.isInThreshold()) {
            thresholdReached = true;
            setBackgroundColor(Color.RED);
        } else {
            setBackgroundColor(Color.WHITE);
        }
    }

    private void initializeDrinkButton() {
        if (countdown.isDrinkDelayRunning()) {
            drinkButton.setVisibility(View.INVISIBLE);
            runDrinkCountdown(drinkRunnable);
        }
    }

    private void startHandler() {
        if (countdown.isRunning())
            HANDLER.postDelayed(countdownRunnable, 0);
    }

    private void stopHandler() {
        HANDLER.removeCallbacks(countdownRunnable);
    }

    private void startDrinkDelayHandler() {
        if (countdown.isDrinkDelayRunning())
            HANDLER.postDelayed(drinkRunnable, 0);
    }

    private void stopDrinkDelayHandler() {
        HANDLER.removeCallbacks(drinkRunnable);
    }

    private void startCountdown() {
        thresholdReached = false;
        countdown.start();
        updateStartButtonListener();
        startHandler();
    }

    private void stopCountdown() {
        stopHandler();
        countdown.stop();
        updateStartButtonListener();
    }

    private void startDrinkCountdown() {
        countdown.startDrinkDelay();
        HANDLER.postDelayed(drinkRunnable, clockRefreshDelay);
        drinkButton.setVisibility(View.INVISIBLE);
    }

    private void stopDrinkDelayCountdown() {
        stopDrinkDelayHandler();
        countdown.stopDrinkDelay();
        drinkDelayView.setText("");
        drinkButton.setVisibility(View.VISIBLE);
    }

    private void setBackgroundColor(int color) {
        timeView.getRootView().setBackgroundColor(color);
    }

    private void resetCountdown() {
        stopCountdown();
        countdown.reset();
    }

    private void updateStartButtonListener() {
        if (countdown.isRunning())

            startButton.setOnClickListener(stopListener);
        else
            startButton.setOnClickListener(startListener);
    }

    private void refreshViews() {
        refreshClockView();
        refreshStartButtonText();
        refreshSetsOn();
        refreshVolumeView();
        refreshDrinkDelayOn();
    }

    private void refreshClockView() {
        int time = countdown.getTime();
        timeView.setText(String.format("%ds", time));
    }

    private void refreshStartButtonText() {
        int startButtonText = countdown.isRunning() ?
                R.string.stop_countdown : R.string.start_countdown;
        startButton.setText(startButtonText);
    }

    private void refreshSetsOn() {
        int sets = countdown.getSets();
        setsView.setText(String.format("%d %s", sets, sets == 1 ?
                resources.getString(R.string.set_singular) :
                resources.getString(R.string.set_plural)));
    }

    private void refreshVolumeView() {
        volumeView.setText(String.format("%d%s", signaller.getVolume(), "%"));
    }

    private void refreshDrinkDelayOn() {
        if (!countdown.isDrinkDelayRunning()) {
            drinkDelayView.setText("");
            return;
        }
        int time = countdown.getDrinkDelayTime();
        String timeText;
        if (time >= 120) {
            time /= 60;
            timeText = '\n' + resources.getString(R.string.minutes_short_plural);
        } else if (time >= 60) {
            time /= 60;
            timeText = '\n' + resources.getString(R.string.minutes_short_singular);
        } else {
            timeText = resources.getString(R.string.seconds_short);
        }
        drinkDelayView.setText(String.format("%d%s", time, timeText));
    }

    private ClockPopup getClockCallback() {
        return new ClockPopup(this, countdown, popupCallback);
    }

    private SetsPopup getSetsCallback() {
        return new SetsPopup(this, countdown, popupCallback);
    }

    private DrinkPopup getDrinkCallback() {
        return new DrinkPopup(this, countdown, popupCallback);
    }

    private void runCountdown(Runnable runnable) {
        if (!countdown.isRunning()) {
            stopCountdown();
            signaller.signalCountdownEnd();
        } else {
            if (!thresholdReached && countdown.isInThreshold()) {
                signaller.signalThresholdReached();
                thresholdReached = true;
            }
            HANDLER.postDelayed(runnable, clockRefreshDelay);
        }
        refreshViews();
    }

    private void runDrinkCountdown(Runnable runnable) {
        if (!countdown.isDrinkDelayRunning())
            stopDrinkDelayCountdown();
        else
            HANDLER.postDelayed(runnable, clockRefreshDelay);
        refreshViews();
    }

}
