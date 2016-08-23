package com.codeforgvl.trolleytrackerclient;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by ahodges on 8/25/2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Iconify.with(new FontAwesomeModule()).with((new MaterialModule()));
        JodaTimeAndroid.init(this);
    }
}
