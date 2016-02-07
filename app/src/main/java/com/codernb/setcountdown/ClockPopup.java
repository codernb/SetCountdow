package com.codernb.setcountdown;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by cyril on 07.02.16.
 */
public class ClockPopup {

    private final View view;
    private final AlertDialog.Builder builder;
    private final Countdown countdown;

    private final EditText countdownTimeView;
    private final EditText thresholdTimeView;

    public ClockPopup(Context context, Countdown countdown) {
        this.countdown = countdown;
        view = LayoutInflater.from(context).inflate(R.layout.clock_popup, null);
        builder = new AlertDialog.Builder(context);
        countdownTimeView = (EditText) view.findViewById(R.id.set_countdown_time);
        thresholdTimeView = (EditText) view.findViewById(R.id.set_threshold_time);
        if (countdown.getCountdownTime() > 0)
            countdownTimeView.setText(String.format("%d", countdown.getCountdownTime()));
        if (countdown.getThreshold() > 0)
            thresholdTimeView.setText(String.format("%d", countdown.getThreshold()));
    }
}
