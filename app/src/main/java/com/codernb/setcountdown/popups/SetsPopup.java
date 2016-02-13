package com.codernb.setcountdown.popups;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.countdowns.RoundCountdown;

/**
 * Created by cyril on 07.02.16.
 */
public class SetsPopup extends Popup {

    private final RoundCountdown roundCountdown;

    private EditText setsView;

    public SetsPopup(Context context, RoundCountdown roundCountdown, Callback callback) {
        super(context, callback);
        this.roundCountdown = roundCountdown;
    }

    @Override
    protected View initializeView() {
        View view = getView(R.layout.sets_popup);
        String sets = String.format("%d", roundCountdown.getSets());
        setsView = (EditText) view.findViewById(R.id.set_sets);
        setsView.setText(sets);
        setsView.setSelection(sets.length());
        return view;
    }

    @Override
    protected void OKClicked() {
        String setsText = setsView.getText().toString();
        int sets = Integer.parseInt(setsText.length() == 0 ? "0" : setsText);
        roundCountdown.setSets(sets);
    }
}
