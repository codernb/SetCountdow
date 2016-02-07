package com.codernb.setcountdown;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by cyril on 07.02.16.
 */
public class SetsPopup extends Popup {

    private EditText setsView;

    public SetsPopup(Activity context, Countdown countdown, Callback callback) {
        super(context, countdown, callback);
    }

    @Override
    protected View initializeView() {
        View view = getView(R.layout.sets_popup);
        setsView = (EditText) view.findViewById(R.id.set_sets);
        setsView.setText(String.format("%d", countdown.getSets()));
        return view;
    }

    @Override
    protected void OKClicked() {
        String setsText = setsView.getText().toString();
        int sets = Integer.parseInt(setsText.length() == 0 ? "0" : setsText);
        countdown.setSets(sets);
        Preferences.save(context, R.string.sets_save_key, sets);
        callback.onOK();
    }
}
