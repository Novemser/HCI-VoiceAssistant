package com.novemser.voicetest.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Novemser on 6/3/2016.
 */
public class Global extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getGlobalContext() {
        return context;
    }
}
