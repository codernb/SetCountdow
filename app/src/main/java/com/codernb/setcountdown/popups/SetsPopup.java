package com.codernb.setcountdown.popups;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.codernb.setcountdown.utils.Countdown;
import com.codernb.setcountdown.R;

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
        String sets = String.format("%d", countdown.getSets());
        setsView = (EditText) view.findViewById(R.id.set_sets);
        setsView.setText(sets);
        setsView.setSelection(sets.length());
        return view;
    }

    @Override
    protected void OKClicked() {
        String setsText = setsView.getText().toString();
        int sets = Integer.parseInt(setsText.length() == 0 ? "0" : setsText);
        countdown.setSets(sets);
        callback.onOK();
    }
}
