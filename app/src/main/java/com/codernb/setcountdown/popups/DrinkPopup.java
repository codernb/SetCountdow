package com.codernb.setcountdown.popups;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.countdowns.DrinkCountdown;

/**
 * Created by cyril on 09.02.16.
 */
public class DrinkPopup extends Popup {

    private final DrinkCountdown drinkCountdown;

    private EditText drinkDelayView;

    public DrinkPopup(Context context, DrinkCountdown roundCountdown, Callback callback) {
        super(context, callback);
        this.drinkCountdown = roundCountdown;
    }

    @Override
    protected View initializeView() {
        View view = getView(R.layout.drink_popup);
        String drinkDelay = String.format("%d", drinkCountdown.getCountdownTime() / 60);
        drinkDelayView = (EditText) view.findViewById(R.id.set_drink_delay);
        drinkDelayView.setText(drinkDelay);
        drinkDelayView.setSelection(drinkDelay.length());
        return view;
    }

    @Override
    protected void OKClicked() {
        String drinkDelayText = drinkDelayView.getText().toString();
        int drinkDelay = Integer.parseInt(drinkDelayText.length() == 0 ? "0" : drinkDelayText);
        drinkCountdown.setCountdownTime(drinkDelay * 60);
    }

}
