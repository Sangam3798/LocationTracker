package com.usertracker.locationtracker;

import android.content.SharedPreferences;

class Constant {
    static  final  int LOCATION_SERVICE_ID = 101;
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    static  final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";
    static  final String TOKEN = "token";

    private static final Constant ourInstance = new Constant();
    public static Constant getInstance() {
        return ourInstance;
    }
    private Constant() { }


    public SharedPreferences sharedPreferences;
}
