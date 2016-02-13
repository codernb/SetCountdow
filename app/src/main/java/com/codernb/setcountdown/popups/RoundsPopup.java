package com.codernb.setcountdown.popups;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.countdowns.RoundCountdown;

/**
 * Created by cyril on 07.02.16.
 */
public class RoundsPopup extends Popup {

    private final RoundCountdown roundCountdown;

    private EditText countdownTimeView;
    private EditText warningTimeView;
    private CheckBox countdownRepeatingView;

    public RoundsPopup(Context context, RoundCountdown roundCountdown, Callback callback) {
        super(context, callback);
        this.roundCountdown = roundCountdown;
    }

    @Override
    protected View initializeView() {
        View view = getView(R.layout.rounds_popup);
        String countdownTime = String.format("%d", roundCountdown.getCountdownTime());
        String thresholdTime = String.format("%d", roundCountdown.getWarningTime());
        boolean repeating = roundCountdown.isRepeating();
        countdownTimeView = (EditText) view.findViewById(R.id.set_countdown_time);
        warningTimeView = (EditText) view.findViewById(R.id.set_threshold_time);
        countdownRepeatingView = (CheckBox) view.findViewById(R.id.rounds_countdown_repeat);
        countdownTimeView.setText(countdownTime);
        warningTimeView.setText(thresholdTime);
        countdownRepeatingView.setChecked(repeating);
        return view;
    }

    @Override
    protected void OKClicked() {
        String countdownText = countdownTimeView.getText().toString();
        String thresholdText = warningTimeView.getText().toString();
        int countdownTime = Integer.parseInt(countdownText.length() == 0 ? "0" : countdownText);
        int thresholdTime = Integer.parseInt(thresholdText.length() == 0 ? "0" : thresholdText);
        boolean repeating = countdownRepeatingView.isChecked();
        roundCountdown.setCountdownTime(countdownTime);
        roundCountdown.setWarningTime(thresholdTime);
        roundCountdown.setRepeating(repeating);
    }

}
