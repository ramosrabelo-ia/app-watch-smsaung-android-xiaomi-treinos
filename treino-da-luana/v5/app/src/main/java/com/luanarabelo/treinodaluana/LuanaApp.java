package com.luanarabelo.treinodaluana;

import android.app.Application;

public class LuanaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Diagnostics.saveCrash(getApplicationContext(), throwable);
            if (previous != null) {
                previous.uncaughtException(thread, throwable);
            }
        });
    }
}
