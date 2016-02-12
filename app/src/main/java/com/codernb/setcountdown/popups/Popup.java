package com.codernb.setcountdown.popups;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.codernb.setcountdown.R;
import com.codernb.setcountdown.utils.Countdown;

/**
 * Created by cyril on 07.02.16.
 */
public abstract class Popup {

    protected final Context context;
    protected final AlertDialog.Builder builder;
    protected final Countdown countdown;
    protected final Callback callback;
    protected AlertDialog alertDialog;

    public Popup(Context context, Countdown countdown, Callback callback) {
        this.context = context;
        this.countdown = countdown;
        this.callback = callback;
        builder = new Builder(context);
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

    public void show() {
        View view = initializeView();
        alertDialog = builder.setView(view).show();
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
