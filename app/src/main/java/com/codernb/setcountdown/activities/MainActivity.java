package com.codernb.setcountdown.activities;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.popups.DrinkPopup;
import com.codernb.setcountdown.popups.Popup.Callback;
import com.codernb.setcountdown.popups.RoundsPopup;
import com.codernb.setcountdown.popups.SetsPopup;
import com.codernb.setcountdown.utils.Preferences;
import com.codernb.setcountdown.utils.countdowns.DrinkCountdown;
import com.codernb.setcountdown.utils.countdowns.RoundCountdown;
import com.codernb.setcountdown.utils.signalers.Signaller;

/**
 * Created by cyril on 04.02.16.
 */
public class MainActivity extends ActionBarActivity {

    private static final Handler HANDLER = new Handler();
    private int clockRefreshDelay;

    private boolean thresholdReached;

    private RoundCountdown roundCountdown;
    private DrinkCountdown drinkCountdown;
    private Resources resources;
    private RoundsPopup roundsPopup;
    private SetsPopup setsPopup;
    private DrinkPopup drinkPopup;
    private Signaller signaller;

    private TextView timeView;
    private TextView setsView;
    private TextView volumeView;
    private TextView drinkDelayView;
    private ImageView roundsCountdownRepeatView;
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
            startRoundCountdown();
            refreshViews();
        }
    };

    private final OnClickListener stopListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            stopRoundCountdown();
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
            roundsPopup.show();
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

    private final OnLongClickListener drinkStopListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            stopDrinkDelayCountdown();
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
        resources = getResources();
        Preferences preferences = new Preferences(resources, getPreferences(MODE_PRIVATE));
        roundCountdown = new RoundCountdown(preferences);
        drinkCountdown = new DrinkCountdown(preferences);
        roundsPopup = getClockCallback();
        setsPopup = getSetsCallback();
        drinkPopup = getDrinkCallback();
        signaller = new Signaller(this, preferences);
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
        roundsPopup.dismiss();
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
        roundsCountdownRepeatView = (ImageView) findViewById(R.id.rounds_countdown_repeat_view);
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
        drinkDelayView.setOnLongClickListener(drinkStopListener);
    }

    private void initializeValues() {
        clockRefreshDelay = resources.getInteger(R.integer.clock_refresh_delay);
    }

    private void initializeThresholdReached() {
        if (roundCountdown.isWarning()) {
            thresholdReached = true;
            setBackgroundColor(Color.RED);
        } else {
            setBackgroundColor(Color.WHITE);
        }
    }

    private void initializeDrinkButton() {
        if (drinkCountdown.isRunning()) {
            drinkButton.setVisibility(View.INVISIBLE);
            runDrinkCountdown(drinkRunnable);
        }
    }

    private void startHandler() {
        if (roundCountdown.isRunning())
            HANDLER.postDelayed(countdownRunnable, 0);
    }

    private void stopHandler() {
        HANDLER.removeCallbacks(countdownRunnable);
    }

    private void startDrinkDelayHandler() {
        if (drinkCountdown.isRunning())
            HANDLER.postDelayed(drinkRunnable, 0);
    }

    private void stopDrinkDelayHandler() {
        HANDLER.removeCallbacks(drinkRunnable);
    }

    private void startRoundCountdown() {
        thresholdReached = false;
        roundCountdown.start();
        updateStartButtonListener();
        startHandler();
    }

    private void stopRoundCountdown() {
        stopHandler();
        roundCountdown.stop();
        updateStartButtonListener();
    }

    private void startDrinkCountdown() {
        drinkCountdown.start();
        HANDLER.postDelayed(drinkRunnable, clockRefreshDelay);
    }

    private void stopDrinkDelayCountdown() {
        stopDrinkDelayHandler();
        drinkCountdown.stop();
        refreshDrinkDelay();
    }

    private void setBackgroundColor(int color) {
        timeView.getRootView().setBackgroundColor(color);
    }

    private void resetCountdown() {
        stopRoundCountdown();
        roundCountdown.reset();
    }

    private void updateStartButtonListener() {
        if (roundCountdown.isRunning())

            startButton.setOnClickListener(stopListener);
        else
            startButton.setOnClickListener(startListener);
    }

    private void refreshViews() {
        refreshRoundCountdownView();
        refreshStartButtonText();
        refreshSetsOn();
        refreshVolumeView();
        refreshDrinkDelay();
    }

    private void refreshRoundCountdownView() {
        int time = roundCountdown.getRemainingTime();
        timeView.setText(String.format("%ds", time));
        if (roundCountdown.isRepeating())
            roundsCountdownRepeatView.setVisibility(View.VISIBLE);
        else
            roundsCountdownRepeatView.setVisibility(View.GONE);
    }

    private void refreshStartButtonText() {
        int startButtonText = roundCountdown.isRunning() ?
                R.string.stop_countdown : R.string.start_countdown;
        startButton.setText(startButtonText);
    }

    private void refreshSetsOn() {
        int sets = roundCountdown.getSets();
        setsView.setText(String.format("%d %s", sets, sets == 1 ?
                resources.getString(R.string.set_singular) :
                resources.getString(R.string.set_plural)));
    }

    private void refreshVolumeView() {
        volumeView.setText(String.format("%d%s", signaller.getVolume(), "%"));
    }

    private void refreshDrinkDelay() {
        if (drinkCountdown.isRunning()) {
            drinkDelayView.setVisibility(View.VISIBLE);
            drinkButton.setVisibility(View.INVISIBLE);
            drinkDelayView.setText(String.format("%s", drinkCountdown.getTimeText(resources)));
        } else {
            drinkDelayView.setVisibility(View.INVISIBLE);
            drinkButton.setVisibility(View.VISIBLE);
        }
    }

    private RoundsPopup getClockCallback() {
        return new RoundsPopup(this, roundCountdown, popupCallback);
    }

    private SetsPopup getSetsCallback() {
        return new SetsPopup(this, roundCountdown, popupCallback);
    }

    private DrinkPopup getDrinkCallback() {
        return new DrinkPopup(this, drinkCountdown, popupCallback);
    }

    private void runCountdown(Runnable runnable) {
        if (!roundCountdown.isRunning()) {
            stopRoundCountdown();
            signaller.signalCountdownEnd();
            if (roundCountdown.isRepeating())
                startRoundCountdown();
        } else {
            if (!thresholdReached && roundCountdown.isWarning()) {
                signaller.signalThresholdReached();
                thresholdReached = true;
            }
            HANDLER.postDelayed(runnable, clockRefreshDelay);
        }
        refreshViews();
    }

    private void runDrinkCountdown(Runnable runnable) {
        if (!drinkCountdown.isRunning())
            stopDrinkDelayCountdown();
        else
            HANDLER.postDelayed(runnable, clockRefreshDelay);
        refreshViews();
    }

}
