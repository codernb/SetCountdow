package com.codernb.setcountdown.popups;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.Countdown;

/**
 * Created by cyril on 09.02.16.
 */
public class DrinkPopup extends Popup {

    private EditText drinkDelayView;

    public DrinkPopup(Context context, Countdown countdown, Callback callback) {
        super(context, countdown, callback);
    }

    @Override
    protected View initializeView() {
        View view = getView(R.layout.drink_popup);
        String drinkDelay = String.format("%d", countdown.getDrinkDelay() / 60);
        drinkDelayView = (EditText) view.findViewById(R.id.set_drink_delay);
        drinkDelayView.setText(drinkDelay);
        drinkDelayView.setSelection(drinkDelay.length());
        return view;
    }

    @Override
    protected void OKClicked() {
        String drinkDelayText = drinkDelayView.getText().toString();
        int drinkDelay = Integer.parseInt(drinkDelayText.length() == 0 ? "0" : drinkDelayText);
        countdown.setDrinkDelay(drinkDelay * 60);
        callback.onOK();
    }

}
