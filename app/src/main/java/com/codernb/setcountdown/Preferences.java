package com.codernb.setcountdown;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

/**
 * Created by cyril on 07.02.16.
 */
public class Preferences {

    private Preferences() {
        ;
    }

    public static void save(Activity activity, int id, int value) {
        Resources resources = activity.getResources();
        Editor editor = getEditorFrom(activity);
        editor.putInt(resources.getString(id), value);
        editor.commit();
    }

    public static int load(Activity activity, int id, int defaultValueId) {
        Resources resources = activity.getResources();
        SharedPreferences sharedPreferences = getSharedPreferencesFrom(activity);
        return sharedPreferences.getInt(resources.getString(id), resources.getInteger(defaultValueId));
    }

    private static Editor getEditorFrom(Activity activity) {
        return getSharedPreferencesFrom(activity).edit();
    }

    private static SharedPreferences getSharedPreferencesFrom(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE);
    }

}