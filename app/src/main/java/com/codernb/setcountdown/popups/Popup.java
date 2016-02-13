package com.codernb.setcountdown.popups;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.Countdown;

/**
 * Created by cyril on 07.02.16.
 */
public abstract class Popup {

    protected final Context context;
    protected final AlertDialog.Builder builder;
    protected final Countdown countdown;
    protected AlertDialog alertDialog;

    public Popup(Context context, Countdown countdown, final Callback callback) {
        this.context = context;
        this.countdown = countdown;
        builder = new Builder(context);
        builder.setCancelable(false)
                .setPositiveButton(R.string.set_time_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OKClicked();
                        callback.onOK();
                    }
                })
                .setNegativeButton(R.string.set_time_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != 66 || event.getAction() != KeyEvent.ACTION_UP)
                    return false;
                dismiss();
                OKClicked();
                callback.onOK();
                return true;
            }
        });
    }

    public void show() {
        View view = initializeView();
        alertDialog = builder.setView(view).show();
        alertDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    protected abstract View initializeView();

    protected abstract void OKClicked();

    protected View getView(int id) {
        return LayoutInflater.from(context).inflate(id, null);
    }

    public interface Callback {

        void onOK();

    }
}
