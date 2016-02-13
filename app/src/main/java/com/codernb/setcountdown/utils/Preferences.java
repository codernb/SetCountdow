package com.codernb.setcountdown.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

/**
 * Created by cyril on 07.02.16.
 */
public class Preferences {

    private final Resources resources;
    private final SharedPreferences sharedPreferences;

    public Preferences(Resources resources, SharedPreferences sharedPreferences) {
        this.resources = resources;
        this.sharedPreferences = sharedPreferences;
    }

    public void save(int keyId, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(resources.getString(keyId), value);
        editor.commit();
    }

    public void save(int keyId, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(resources.getString(keyId), value);
        editor.commit();
    }

    public void save(int keyId, boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(resources.getString(keyId), value);
        editor.commit();
    }

    public int loadInt(int keyId) {
        return loadInt(resources, keyId, 0);
    }

    public long loadLong(int keyId) {
        return loadLong(resources, keyId, 0);
    }

    public int loadInt(int keyId, int defaultValueId) {
        return loadInt(resources, keyId, resources.getInteger(defaultValueId));
    }

    public boolean loadBoolean(int keyId) {
        return sharedPreferences.getBoolean(resources.getString(keyId), false);
    }

    private int loadInt(Resources resources, int keyId, int defaultValue) {
        return sharedPreferences.getInt(resources.getString(keyId), defaultValue);
    }

    private long loadLong(Resources resources, int keyId, long defaultValue) {
        return sharedPreferences.getLong(resources.getString(keyId), defaultValue);
    }

}
