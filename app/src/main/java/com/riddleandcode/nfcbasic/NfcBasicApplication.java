package com.riddleandcode.nfcbasic;

import com.crashlytics.android.Crashlytics;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Eyliss on 2/20/17.
 */

public class NfcBasicApplication extends Application {

    /**
     * A singleton instance of the application class for easy access in other places.
     */
    private static NfcBasicApplication sInstance;
    private static Context sContext;

    public void onCreate(){
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
              .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
              .setFontAttrId(R.attr.fontPath)
              .build()
        );

        // Initialize application singleton instance
        sInstance = this;
        sContext = this.getApplicationContext();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public String getUserAgent(){
        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "Riddle&Code|"+version+"|Android|"+android.os.Build.VERSION.SDK_INT+"|"+ getDeviceName();
    }

    // Get the full device name compound by manufacturer and model in a readable string format
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * @return FanstasticApplication singleton instance.
     */
    public static synchronized NfcBasicApplication getInstance() {
        return sInstance;
    }

    public static synchronized Context getContext() {
        return sContext;
    }
}
