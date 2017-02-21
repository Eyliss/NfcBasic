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
import android.widget.ProgressBar;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String API_URL = "https://chain.so/api/v2";
    private static String GET_BALANCE_URL = "/get_address_balance";
    private static String NETWORK = "/BTCTEST";

    private ProgressBar mProgressBar;
    private Tag tagFromIntent;
    private TagManager mTagManager;

    private String mMessage;

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

//        try {
//            checkSign(message, sign,publicKey);
//        } catch (CertificateException | CMSException | OperatorCreationException e) {
//            e.printStackTrace();
//        }
    }

    private void bindViews(){
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
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
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();

//                mMessage = mEtMessage.getText().toString();
                mMessage = "Hello world";
                signMessageAndVerify();

                mTagManager.setTimeout(100);
                mTagManager.ntagClose();
            } catch (Exception e) {
                Toast.makeText(this, "Tag reading Error: ", Toast.LENGTH_SHORT).show();

            }
        }
    }


    /*
     * Get the message introduced by the user, hash it and send to the antenna in order to sign it.
     * Fetch the public key from the antenna to verify the signature received
     */
    private void signMessageAndVerify(){
        try {

            byte[] hashString = Util.hashString(mMessage);
//            mTagManager.signMessage(hashString);

//            Log.d(TAG,Util.bytesToHex(mTagManager.getMessage()));
            byte[] data0 = {(byte) 0x41, (byte) 0x21 ,(byte) 0x31 ,(byte) 0x41};
            byte[] data1 = {(byte) 0x41, (byte) 0x42 ,(byte) 0x43 ,(byte) 0x44};
            byte[] data2 = {(byte) 0x31, (byte) 0x42, (byte) 0x33, (byte) 0x44};
            byte[] data3 = {(byte) 0x31, (byte) 0x42, (byte) 0x33, (byte) 0x44};


            mTagManager.ntagSectorSelect((byte) 0x01);
            mTagManager.ntagWrite(data0, (byte) 0x04);
            mTagManager.ntagWrite(data1, (byte) 0x05);
            mTagManager.ntagWrite(data2, (byte) 0x06);
            mTagManager.ntagWrite(data3, (byte) 0x07);

            mTagManager.ntagRead((byte) 0x04);
            Log.d(TAG,Util.bytesToHex(mTagManager.ntagGetLastAnswer()));

//            //Wait until the tag is ready for be read
//            do {
//                boolean boolVar = true;
//            } while (!mTagManager.ntagReadable());
//
//            mTagManager.parseSignResponse();
//
//            mTagManager.getKey();
//            do {
//                boolean boolVar = true;
//            } while (!mTagManager.ntagReadable());
//
//            mTagManager.parseGetKeyResponse();
//
//            boolean verified = mTagManager.checkSign(hashString);
//            int message = verified ? R.string.verification_success : R.string.verification_fail;
//            Toast.makeText(this,getString(message),Toast.LENGTH_SHORT).show();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (CMSException e) {
//            e.printStackTrace();
//        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (FormatException e) {
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

