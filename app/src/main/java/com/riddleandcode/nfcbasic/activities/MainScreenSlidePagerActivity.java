package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.adapters.MainScreenSlidePagerAdapter;
import com.riddleandcode.nfcbasic.managers.TagManager;

import org.apache.commons.codec.DecoderException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.Security;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Eyliss on 2/21/17.
 */


public class MainScreenSlidePagerActivity extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Fabric.with(this, new Crashlytics());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MainScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
}

