package com.riddleandcode.nfcbasic.activities;

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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Eyliss on 2/21/17.
 */


public class MainScreenSlidePagerActivity extends AppCompatActivity {

    private Tag tagFromIntent;
    private TagManager mTagManager;

    private ProgressBar mProgressBar;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MainScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        try {
            mTagManager = new TagManager(this);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, VerificationActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        mTagManager.getAdapter().enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        mTagManager.getAdapter().disableForegroundDispatch(this);
    }


    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();

//                mMessage = mEtMessage.getText().toString();
//                mMessage = "Hello world";
                showResults();

                mTagManager.setTimeout(100);
                mTagManager.ntagClose();
            } catch (Exception e) {
                Toast.makeText(this, "Tag reading Error: ", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void showResults(){
        mProgressBar.setProgress(View.GONE);
        Intent intent;
        if(mPager.getCurrentItem() == 0){
            intent = new Intent(this,ValidationActivity.class);
        }else{
            intent = new Intent(this,VerificationActivity.class);
        }
        startActivity(intent);
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

    public void showProgress(){
        mProgressBar.setVisibility(View.VISIBLE);
    }
}

