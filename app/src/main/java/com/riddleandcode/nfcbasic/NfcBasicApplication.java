package com.riddleandcode.nfcbasic;

import com.crashlytics.android.Crashlytics;

import android.app.Application;
import android.content.Context;

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
