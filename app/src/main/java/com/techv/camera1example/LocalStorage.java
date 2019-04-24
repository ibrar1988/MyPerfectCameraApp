package com.techv.camera1example;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {

    private LocalStorage(){ /* Can not be instantiated */}

    // Get instance of the SharedPreferences object that hold the contents of the preferences

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences("Pref-Values", Context.MODE_PRIVATE);
    }

    // Set string type sharedPreferences

    public static void saveStringPreference(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(key, value).apply();
        editor.commit();
    }

    // Set integer type sharedPreferences

    public static void saveIntPreference(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putInt(key, value).apply();
        editor.commit();
    }

    // Set boolean type sharedPreference

    public static void saveBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putBoolean(key, value).apply();
        editor.commit();
    }

    // Get string type sharedPreferences

    public static String getStringPreference(Context context, String key, String defaultValue) {
        SharedPreferences sharedPref = getPreference(context);
        return sharedPref.getString(key, defaultValue);
    }

    // Get integer type sharedPreferences

    public static int getIntPreference(Context context, String key, int defaultValue) {
        SharedPreferences sharedPref = getPreference(context);
        return sharedPref.getInt(key, defaultValue);
    }

    // Get boolean type sharedPreferences

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPref = getPreference(context);
        return sharedPref.getBoolean(key,defaultValue);
    }

    public static void removePreference(Context context, String key) {
        SharedPreferences.Editor sharedPreferences = getPreference(context).edit();
        sharedPreferences.remove(key);
        sharedPreferences.apply();;
        sharedPreferences.commit();
    }

    public static void clearAllPreferences(Context context) {
        SharedPreferences.Editor sharedPref = getPreference(context).edit();
        sharedPref.clear().apply();
        sharedPref.commit();
    }
}
