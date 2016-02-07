package com.codernb.setcountdown;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by cyril on 07.02.16.
 */
public class ClockPopup {

    private final AlertDialog.Builder builder;
    private final Context context;
    private final Countdown countdown;
    private final Callback callback;
    private EditText countdownTimeView;
    private EditText thresholdTimeView;
    private AlertDialog alertDialog;

    public ClockPopup(Context context, Countdown countdown, Callback callback) {
        this.context = context;
        this.countdown = countdown;
        this.callback = callback;
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setPositiveButton(R.string.set_time_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OKClicked();
                    }
                })
                .setNegativeButton(R.string.set_time_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }

    private View initializeView() {
        View view = getView();
        countdownTimeView = (EditText) view.findViewById(R.id.set_countdown_time);
        thresholdTimeView = (EditText) view.findViewById(R.id.set_threshold_time);
        countdownTimeView.setText(String.format("%d", countdown.getCountdownTime()));
        thresholdTimeView.setText(String.format("%d", countdown.getThreshold()));
        return view;
    }

    private View getView() {
        return LayoutInflater.from(context).inflate(R.layout.clock_popup, null);
    }

    private void OKClicked() {
        int countdownTime = Integer.parseInt(countdownTimeView.getText().toString());
        int thresholdTime = Integer.parseInt(thresholdTimeView.getText().toString());
        countdown.setCountdownTime(countdownTime);
        countdown.setThreshold(thresholdTime);
        callback.onOK();
    }

    public void show() {
        View view = initializeView();
        alertDialog = builder.setView(view).show();
    }

    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    public interface Callback {

        void onOK();

    }
}
