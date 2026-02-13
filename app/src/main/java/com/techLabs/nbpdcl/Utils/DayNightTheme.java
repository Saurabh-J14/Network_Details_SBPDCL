package com.techLabs.nbpdcl.Utils;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class DayNightTheme extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}

