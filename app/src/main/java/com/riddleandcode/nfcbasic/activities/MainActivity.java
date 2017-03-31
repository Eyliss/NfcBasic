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

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

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
        mTvReadTag.setText(mTagManager.isAvailable() ? R.string.tag_available : R.string.tag_unavailable);

//        try {
//            checkSign(message, sign,publicKey);
//        } catch (CertificateException | CMSException | OperatorCreationException e) {
//            e.printStackTrace();
//        }
    }

    private void bindViews(){
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mTvReadTag = (TextView)findViewById(R.id.info_test);
        mInfoLayout = (LinearLayout)findViewById(R.id.info_layout);
        mEtMessage = (EditText) findViewById(R.id.et_message);
        mNetwork = (TextView)findViewById(R.id.tv_network);
        mConfirmedBalance = (TextView)findViewById(R.id.tv_confirmed_balance);
        mUnconfirmedBalance = (TextView)findViewById(R.id.tv_unconfirmed_balance);
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
        tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NFC Intent", Toast.LENGTH_SHORT).show();
            //NfcA nfca;
            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();
//                mTagManager.ntagGetVersion();

                // Wait for hand over from I2C
//                boolean boolVar;
//                do {
//                    boolVar = true;
//                    mTagManager.setNfcATimeout(20);
//                } while(mTagManager.ntagReadBit((byte) 0xD0,0x00, 0x00) != 0x01);

                //Read the public key page by page
                mTagManager.ntagRead((byte) 0x04);
                byte[] page_1 = mTagManager.ntagGetLastAnswer();

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte) 0x08);
                byte[] page_2 = mTagManager.ntagGetLastAnswer();

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte) 0x0C);
                byte[] page_3 = mTagManager.ntagGetLastAnswer();

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte) 0x10);
                byte[] page_4 = mTagManager.ntagGetLastAnswer();

                String publicKey = Util.bytesToHex(page_1)+Util.bytesToHex(page_2)+Util.bytesToHex(page_3)+Util.bytesToHex(page_4);
                Log.d(TAG,"Public key "+publicKey);
                mTvReadTag.setText(publicKey);

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.setNfcATimeout(20);

                byte[] data1 = {(byte) 0x41, (byte) 0x42 ,(byte) 0x43 ,(byte) 0x44};
                mTagManager.ntagWrite(data1, (byte) 0xF0);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(data1, (byte) 0xF1);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(data1, (byte) 0xF2);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(data1, (byte) 0xF3);
                mTagManager.setNfcATimeout(20);

                mTagManager.setNfcATimeout(1000);
                mTagManager.ntagRead((byte) 0xF0);
                page_1 = mTagManager.ntagGetLastAnswer();

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte) 0xF1);
                page_2 = mTagManager.ntagGetLastAnswer();

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte) 0xF2);
                page_3 = mTagManager.ntagGetLastAnswer();

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.ntagRead((byte) 0xF3);
                page_4 = mTagManager.ntagGetLastAnswer();

                String signature = Util.bytesToHex(page_1)+Util.bytesToHex(page_2)+Util.bytesToHex(page_3)+Util.bytesToHex(page_4);
                Log.d(TAG,"Signatrue "+publicKey);
                mTvReadTag.setText(signature);

//                mTagManager.ntagWrite(data1, (byte) 0xF4);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF5);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF6);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF7);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF8);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF9);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xFA);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xFB);
//                mTagManager.setNfcATimeout(20);
////
//                mTagManager.ntagWrite(data1, (byte) 0xFC);
//                mTagManager.setNfcATimeout(20);
////
//                mTagManager.ntagWrite(data1, (byte) 0xFD);
//                mTagManager.setNfcATimeout(20);
////
//                mTagManager.ntagWrite(data1, (byte) 0xFE);
//                mTagManager.setNfcATimeout(20);

//                mTagManager.ntagWrite(data1, (byte) 0xFF);
//                mTagManager.setNfcATimeout(20);

//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.setNfcATimeout(20);
//
//                byte[] clearHandshakeBit = {(byte) 0x00, (byte) 0x00 ,(byte) 0x00 ,(byte) 0x00};
//                mTagManager.ntagWrite(clearHandshakeBit, (byte)0xD0);
//
//                mTagManager.setNfcATimeout(20);

//                do {
//                    boolVar = true;
//                    mTagManager.setNfcATimeout(20);
//                } while(mTagManager.ntagReadBit((byte) 0xD0,0x00, 0x00) != 0x01);
//
//                //Read the signature page by page
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x14);
//                page_1 = mTagManager.ntagGetLastAnswer();
//                //Toast.makeText(this, "Tag response: "+ bytesToHex(answer), Toast.LENGTH_SHORT).show();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x18);
//                page_2 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x1C);
//                page_3 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x20);
//                page_4 = mTagManager.ntagGetLastAnswer();
//
//                String signature = Util.bytesToHex(page_1)+Util.bytesToHex(page_2)+Util.bytesToHex(page_3)+Util.bytesToHex(page_4);
//                Log.d(TAG,"Signature "+signature);
//                mTvReadTag.setText(signature);
//
//                // Write data back to NFC
//                mTagManager.ntagSectorSelect((byte) 0x00);
//
//                byte[] dataNull = {(byte) 0x01, (byte) 0x01 ,(byte) 0x01 ,(byte) 0x01};
//                byte[] data1 = {(byte) 0x41, (byte) 0x42 ,(byte) 0x43 ,(byte) 0x44};
//                byte[] data2 = {(byte) 0x31, (byte) 0x42, (byte) 0x33, (byte) 0x44};
//                byte[] data3 = {(byte) 0x31, (byte) 0x42, (byte) 0x33, (byte) 0x44};
//
//                mTagManager.ntagWrite(data1, (byte) 0x05);
//                mTagManager.ntagWrite(data2, (byte) 0x06);
//                mTagManager.ntagWrite(data3, (byte) 0x07);
//
//                mTagManager.ntagWrite(dataNull, (byte) 0x04);


 /*              Hand command back to MCU over I2C

                byte[] dataCtrlFree = {(byte) 0x00, (byte) 0x00 ,(byte) 0x00 ,(byte) 0x00};
                ntagWrite( dataCtrlFree, (byte) 0x04);
                //TimeUnit.MILLISECONDS.sleep(1);

*/
//                boolean verified = mTagManager.checkSign(mTagManager.getHashMessage());
//                int message = verified ? R.string.verification_success : R.string.verification_fail;
//                Toast.makeText(this,getString(message),Toast.LENGTH_SHORT).show();

//                signMessageAndVerify();
                mTagManager.setNfcATimeout(100);
                mTagManager.ntagClose();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Log.d(TAG,"Tag reading Error: "+e.getMessage());
                    readFromTag();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (FormatException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void readFromTag() throws IOException, FormatException {

    }
    /*
     * Get the message introduced by the user, hash it and send to the antenna in order to sign it.
     * Fetch the public key from the antenna to verify the signature received
     */
    private void signMessageAndVerify(){
        try {

//            byte[] hashString = Util.hashString("");

            boolean verified = mTagManager.checkSign(mTagManager.getHashMessage());
            int message = verified ? R.string.verification_success : R.string.verification_fail;
            Toast.makeText(this,getString(message),Toast.LENGTH_SHORT).show();

            fetchAccountData();
        } catch (CertificateException | CMSException | OperatorCreationException e) {
            e.printStackTrace();
        }
    }

    private void fetchAccountData(){
        new RetrieveFeedTask().execute();
    }

    private void disableForegroundDispatchSystem() {
        mTagManager.getAdapter().disableForegroundDispatch(this);
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            String address = Util.bytesToHex(mTagManager.getPublicKey());
            try {
                //Hacked address until the device read a correct one from the antenna
                URL url = getUrlWithParams(address);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            mProgressBar.setVisibility(View.GONE);
            if(response != null){
                handleResponse(response);
            }
        }
    }

    private void handleResponse(String response){
        try {
            JSONObject data = new JSONObject(response);
            if(data.getString(Constants.JSON_STATUS).equals(Constants.JSON_SUCCESS)){
                Balance balance = new Balance(data.getJSONObject(Constants.JSON_DATA));
                setBalanceInfo(balance);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBalanceInfo(Balance balance){
        mTvReadTag.setVisibility(View.GONE);
        mEtMessage.setVisibility(View.GONE);
        mInfoLayout.setVisibility(View.VISIBLE);
        mNetwork.setText(getString(R.string.network,balance.getNetwork()));
        mConfirmedBalance.setText(getString(R.string.confirmed_balance,balance.getConfirmedBalance()));
        mUnconfirmedBalance.setText(getString(R.string.unconfirmed_balance,balance.getUnconfirmedBalance()));
    }

    private URL getUrlWithParams(String address){
        try {
            return new URL(getBalanceUrl() + NETWORK + "/" + address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBalanceUrl(){
        return API_URL + GET_BALANCE_URL;
    }

}

