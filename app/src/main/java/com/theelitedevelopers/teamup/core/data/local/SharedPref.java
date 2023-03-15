package com.theelitedevelopers.teamup.core.data.local;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//
// Created by Victor on 3/04/2023.
// Copyright (c) 2023 TeamUp. All rights reserved.
//
public class SharedPref {

    private static SharedPref sharedPref = new SharedPref();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharedPref(){}

    public static SharedPref getInstance(Context context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharedPref;
    }

    public void saveString(String key, String value){
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }

    public void saveInt(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public void saveBoolean(String key, boolean value){
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void clearAll(){
        editor.clear();
        editor.commit();
    }

    public void removeKeyValue(String key){
        sharedPreferences.edit().remove(key).apply();
    }

}
