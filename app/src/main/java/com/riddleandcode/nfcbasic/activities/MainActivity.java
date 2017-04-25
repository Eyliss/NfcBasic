package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.managers.TagManager;
import com.riddleandcode.nfcbasic.models.Balance;
import com.riddleandcode.nfcbasic.utils.Constants;
import com.riddleandcode.nfcbasic.utils.Util;

import io.fabric.sdk.android.Fabric;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.OperatorCreationException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String API_URL = "https://chain.so/api/v2";
    private static String GET_BALANCE_URL = "/get_address_balance";
    private static String NETWORK = "/BTCTEST";

    private TextView mTvReadTag;
    private LinearLayout mInfoLayout;
    private TextView mNetwork;
    private TextView mConfirmedBalance;
    private TextView mUnconfirmedBalance;
    private EditText mEtMessage;

    private ProgressBar mProgressBar;
    private Tag tagFromIntent;
    private TagManager mTagManager;

    public MainActivity() throws DecoderException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        bindViews();

        try {
            mTagManager = new TagManager(this);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    private void bindViews(){
        mTvReadTag = (TextView)findViewById(R.id.info_test);
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
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        mTagManager.getAdapter().enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        //do something with tagFromIntent

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NFC Intent", Toast.LENGTH_SHORT).show();

            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();

                mTagManager.ntagSectorSelect((byte) 0x00);
                byte[] data_hs_start = {(byte) 0xE1, (byte) 0xA1, (byte) 0xA1, (byte) 0xA1};
                mTagManager.ntagWrite(data_hs_start, (byte) 0x04);


                Thread.sleep(1000);

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte)0x10);
                mTvReadTag.setText( Util.bytesToHex(mTagManager.ntagGetLastAnswer()));

                Toast.makeText(this, "Ended to read from SRAM", Toast.LENGTH_SHORT).show();

                mTagManager.setNfcATimeout(100);
                mTagManager.ntagClose();

            } catch (Exception e) {
                Toast.makeText(this, "Tag reading Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disableForegroundDispatchSystem() {
        mTagManager.getAdapter().disableForegroundDispatch(this);
    }

}

