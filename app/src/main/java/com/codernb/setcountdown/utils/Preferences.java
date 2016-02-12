package com.codernb.setcountdown.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

/**
 * Created by cyril on 07.02.16.
 */
public class Preferences {

    private final Activity activity;

    public Preferences(Activity activity) {
        this.activity = activity;
    }

    public void save(int keyId, int value) {
        Resources resources = activity.getResources();
        Editor editor = getEditor();
        editor.putInt(resources.getString(keyId), value);
        editor.commit();
    }

    public int load(int keyId) {
        Resources resources = activity.getResources();
        return load(resources, keyId, 0);
    }

    public int load(int keyId, int defaultValueId) {
        Resources resources = activity.getResources();
        return load(resources, keyId, resources.getInteger(defaultValueId));
    }

    private int load(Resources resources, int keyId, int defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt(resources.getString(keyId), defaultValue);
    }

    private Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private SharedPreferences getSharedPreferences() {
        return activity.getPreferences(Context.MODE_PRIVATE);
    }

}
