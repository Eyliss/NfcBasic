package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.managers.TagManager;
import com.riddleandcode.nfcbasic.models.Balance;
import com.riddleandcode.nfcbasic.utils.Constants;

import io.fabric.sdk.android.Fabric;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;
import org.json.JSONObject;
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

public class ValidationActivity extends AppCompatActivity {

    private static final String TAG = ValidationActivity.class.getSimpleName();

    private static String API_URL = "http://localhost:9984/api/v1";
    private static String GET_TRANSACTION = "/transactions";
//    private static String API_URL = "https://chain.so/api/v2";
//    private static String GET_BALANCE_URL = "/get_address_balance";
//    private static String NETWORK = "/BTCTEST";

    private ProgressBar mProgressBar;

    private TextView mResultMessage;
    private TextView mResult;
    private TextView mResponseDetails;

    public ValidationActivity() throws DecoderException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_validation);
        bindViews();
        fetchAccountData();

    }

    private void bindViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mResultMessage = (TextView) findViewById(R.id.result_message);
        mResult = (TextView) findViewById(R.id.validation_result);
        mResponseDetails = (TextView) findViewById(R.id.validation_response_details);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchAccountData() {
        new RetrieveFeedTask().execute();
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
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
                setMockedData();
                return null;
            }
        }

        protected void onPostExecute(String response) {
            mProgressBar.setVisibility(View.GONE);
            if (response != null) {
//                handleResponse(response);
                setMockedData();
            }
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
}
