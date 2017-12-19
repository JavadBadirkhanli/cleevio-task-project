package com.javadbadirkhanly.cleeviotaskproject.java;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by javadbadirkhanly on 12/18/17.
 */

public class SharedPreference {

    private SharedPreferences sharedPreferences;

    public SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PROJECT_NAME, Context.MODE_PRIVATE);
    }

    public void saveData(String key){
        sharedPreferences.edit().putString(key, "").apply();
    }

    public void saveData(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void saveData(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void saveData(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void saveData(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public void saveData(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public String getData(String key) {
        String defaultValue = "";
        return getData(key, defaultValue);
    }

    public String getData(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getData(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean getData(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public float getData(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public long getData(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

}
