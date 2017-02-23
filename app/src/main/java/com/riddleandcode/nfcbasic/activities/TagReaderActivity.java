package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.managers.TagManager;
import com.riddleandcode.nfcbasic.models.Balance;
import com.riddleandcode.nfcbasic.utils.Constants;

import io.fabric.sdk.android.Fabric;

import org.apache.commons.codec.DecoderException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;

public class TagReaderActivity extends AppCompatActivity {

    private static final String TAG = TagReaderActivity.class.getSimpleName();

    private static String API_URL = "http://localhost:9984/api/v1";
    private static String GET_TRANSACTION = "/transactions";
//    private static String API_URL = "https://chain.so/api/v2";
//    private static String GET_BALANCE_URL = "/get_address_balance";
//    private static String NETWORK = "/BTCTEST";

    private ProgressBar mProgressBar;
    private TextView mResultMessage;
    private TextView mResult;
    private TextView mResponseDetails;

    private Tag tagFromIntent;
    private TagManager mTagManager;

    private String processType;

    public TagReaderActivity() throws DecoderException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Fabric.with(this, new Crashlytics());

        processType = getIntent().getStringExtra(Constants.INTENT_PROCESS_TYPE);
        int layout = processType.equals(Constants.VALIDATION) ? R.layout.activity_validation : R.layout.activity_verification;
        setContentView(layout);
        bindViews();

        try {
            mTagManager = new TagManager(this);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mResultMessage = (TextView) findViewById(R.id.result_message);
        mResult = (TextView) findViewById(R.id.result);
        mResponseDetails = (TextView) findViewById(R.id.response_details);
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
        Intent intent = new Intent(this, TagReaderActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        mTagManager.getAdapter().enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        mTagManager.getAdapter().disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();

                mProgressBar.setVisibility(View.VISIBLE);
                showResultByProcessType();

                mTagManager.setTimeout(100);
                mTagManager.ntagClose();
            } catch (Exception e) {
                Toast.makeText(this, "Tag reading Error: ", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void showResultByProcessType(){
        if(processType.equals(Constants.VALIDATION)){
            fetchAccountData();
        }else{
            signMessageAndVerify();
        }
    }

    private void fetchAccountData() {
        new RetrieveFeedTask().execute();
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
//            String address = Util.bytesToHex(mTagManager.getPublicKey());
            try {
                //Hacked address until the device read a correct one from the antenna
//                URL url = getUrlWithParams("mobyyYFM7HafjFBtca9PAyN7TUAE5uiZFf");
                URL url = getUrlWithParams("da7a66280914be1a8f0496598fc15f763cbd70b486f12ee815bf1d8815565c2b");
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
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            mProgressBar.setVisibility(View.GONE);
            setMockedData();
//            if (response != null) {
//                handleResponse(response);
//            }
        }
    }

    private void setMockedData(){
        mResultMessage.setText(R.string.transaction_validated);
        mResult.setText("Provenence and ownership of the product are valid and legitimate");
        mResponseDetails.setText("http://localhost:9984/api/v1/transactions/da7a66280914be1a8f0496598fc15f763cbd70b 486f12ee815bf1d8815565c2b");
    }

//    private void handleResponse(String response) {
//        try {
//            JSONObject data = new JSONObject(response);
//            if (data.getString(Constants.JSON_STATUS).equals(Constants.JSON_SUCCESS)) {
//                setBalanceInfo(new Balance(data.getJSONObject(Constants.JSON_DATA)));
//            }else{
//                mResultMessage.setText(R.string.transaction_not_validated);
//            }

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void setBalanceInfo(Balance balance) {

    }

    private URL getUrlWithParams(String address) {
        try {
            return new URL(getBalanceUrl()+ "/" + address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBalanceUrl() {
        return API_URL + GET_TRANSACTION;
    }

    /*
    * Get the message introduced by the user, hash it and send to the antenna in order to sign it.
    * Fetch the public key from the antenna to verify the signature received
    */
    private void signMessageAndVerify(){
//        try {

//            byte[] hashString = Util.hashString(mMessage);
//            mTagManager.signMessage(hashString);

//            Log.d(TAG,Util.bytesToHex(mTagManager.getMessage()));
//
//            mTagManager.ntagRead((byte) 0x04);
//            Log.d(TAG,Util.bytesToHex(mTagManager.ntagGetLastAnswer()));

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
//            boolean verified = mTagManager.checkSign();
//            mResultMessage.setText(verified ? R.string.verification_successfully : R.string.verification_fail);
        mProgressBar.setVisibility(View.GONE);

        mResultMessage.setText(R.string.verification_successfully);
        mResult.setText("The product is an original produced by the brand A in 2017");
        mResponseDetails.setText("D09EDDBD3B3C1FC28DDE8CCAAD24844DA3C9D63B978DEEF7D71BAE5395BA1030E7644655023639CC25A4409BC1072AF808CF98B036B373FC65EA7B617993C0BB");

//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (CMSException e) {
//            e.printStackTrace();
//        } catch (OperatorCreationException e) {
//            e.printStackTrace();
//        }
    }
}
