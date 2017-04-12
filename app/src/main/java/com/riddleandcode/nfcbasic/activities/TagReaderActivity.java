package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.api.RCApiManager;
import com.riddleandcode.nfcbasic.api.RCApiResponse;
import com.riddleandcode.nfcbasic.managers.TagManager;
import com.riddleandcode.nfcbasic.models.Balance;
import com.riddleandcode.nfcbasic.utils.Constants;
import com.riddleandcode.nfcbasic.utils.Util;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.OperatorCreationException;
import org.w3c.dom.Text;

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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.security.cert.CertificateException;

public class TagReaderActivity extends AppCompatActivity {

    private static final String TAG = TagReaderActivity.class.getSimpleName();

    private static String API_URL = "https://chain.so/api/v2";
    private static String GET_BALANCE_URL = "/get_address_balance";
    private static String NETWORK = "/BTCTEST";

    private TextView mValidationChallenge;
    private TextView mTvResponseDetails;
    private TextView mTvResult;

    private LinearLayout mInfoLayout;
    private TextView mNetwork;
    private TextView mConfirmedBalance;
    private TextView mUnconfirmedBalance;
    private EditText mEtMessage;

    private ProgressBar mProgressBar;
    private Tag tagFromIntent;
    private TagManager mTagManager;
    private String process;

    public TagReaderActivity() throws DecoderException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Fabric.with(this, new Crashlytics());

        try {
            mTagManager = new TagManager(this);
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        process = getIntent().getStringExtra(Constants.INTENT_PROCESS_TYPE);

        if(process.equals(Constants.VALIDATION)){
            setContentView(R.layout.activity_validation);
            mValidationChallenge = (TextView)findViewById(R.id.validation_challenge);
            mValidationChallenge.setText(mTagManager.getChallenge());
            mTvResult = (TextView) findViewById(R.id.result);
            mTvResponseDetails = (TextView) findViewById(R.id.response_details);
        }else{
            setContentView(R.layout.activity_verification);
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
        Intent intent = new Intent(this, TagReaderActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        mTagManager.getAdapter().enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            //NfcA nfca;
            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();
//                mTagManager.ntagGetVersion();

                //Read the public key page by page
//                mTagManager.ntagRead((byte) 0x04);
//                byte[] page_1 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x08);
//                byte[] page_2 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x0C);
//                byte[] page_3 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0x10);
//                byte[] page_4 = mTagManager.ntagGetLastAnswer();
//
//                String publicKey = Util.bytesToHex(page_1)+Util.bytesToHex(page_2)+Util.bytesToHex(page_3)+Util.bytesToHex(page_4);
//                mTagManager.setSignature(publicKey);


//                sendSignatureToServer();
//                sendHash();
//                getPublicKey();
//                getRng();

//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.setNfcATimeout(20);
//
//                byte[] data1 = {(byte) 0x41, (byte) 0x42 ,(byte) 0x43 ,(byte) 0x44};
//                mTagManager.ntagWrite(data1, (byte) 0xF0);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF1);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF2);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.ntagWrite(data1, (byte) 0xF3);
//                mTagManager.setNfcATimeout(20);
//
//                mTagManager.setNfcATimeout(1000);
//                mTagManager.ntagRead((byte) 0xF0);
//                page_1 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0xF1);
//                page_2 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0xF2);
//                page_3 = mTagManager.ntagGetLastAnswer();
//
//                mTagManager.ntagSectorSelect((byte) 0x00);
//                mTagManager.ntagRead((byte) 0xF3);
//                page_4 = mTagManager.ntagGetLastAnswer();
//
//                String signature = Util.bytesToHex(page_1)+Util.bytesToHex(page_2)+Util.bytesToHex(page_3)+Util.bytesToHex(page_4);
//                Log.d(TAG,"Signatrue "+signature);
//                mTvReadTag.setText(signature);


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
                if(process.equals(Constants.VALIDATION)) {
                    validate();
                }
                mTagManager.setNfcATimeout(100);
                mTagManager.ntagClose();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private void validate(){
        RCApiManager.validate(mTagManager.getPublicKey(),mTagManager.getSignature(),mTagManager.getChallenge(), new Callback<RCApiResponse>() {
            @Override
            public void onResponse(Call<RCApiResponse> call, Response<RCApiResponse> response) {
                RCApiResponse kabinettApiResponse = response.body();
                mTvResult.setText(kabinettApiResponse.getStatus());
                if(kabinettApiResponse.getData() != null){
                    mTvResponseDetails.setText((String)kabinettApiResponse.getData());
                }
            }

            @Override
            public void onFailure(Call<RCApiResponse> call, Throwable t) {

            }
        });
    }

     /* Get the message introduced by the user, sendHash it and send to the antenna in order to sendSignature it.
     * Fetch the public key from the antenna to verify the signature received
     */
    private void sendSignatureToServer(){
        RCApiManager.sendSignature(mTagManager.getSignature(), new Callback<RCApiResponse>() {
            @Override
            public void onResponse(Call<RCApiResponse> call, Response<RCApiResponse> response) {
                RCApiResponse kabinettApiResponse = response.body();
                Log.d(TAG,"Send signature to server "+kabinettApiResponse.getData());
            }

            @Override
            public void onFailure(Call<RCApiResponse> call, Throwable t) {

            }
        });
    }

    private void sendHash(){
        RCApiManager.sendHashMessage("Hello world", new Callback<RCApiResponse>() {
            @Override
            public void onResponse(Call<RCApiResponse> call, Response<RCApiResponse> response) {
                RCApiResponse kabinettApiResponse = response.body();
                Log.d(TAG,"Send hash to server "+kabinettApiResponse.getData());
            }

            @Override
            public void onFailure(Call<RCApiResponse> call, Throwable t) {

            }
        });
    }

    private void getRng(){
        RCApiManager.getRng(new Callback<RCApiResponse>() {
            @Override
            public void onResponse(Call<RCApiResponse> call, Response<RCApiResponse> response) {
                RCApiResponse kabinettApiResponse = response.body();
                Log.d(TAG,"Get rng "+kabinettApiResponse.getData());

            }

            @Override
            public void onFailure(Call<RCApiResponse> call, Throwable t) {

            }
        });
    }

    private void getPublicKey(){
        RCApiManager.getPublicKey(new Callback<RCApiResponse>() {
            @Override
            public void onResponse(Call<RCApiResponse> call, Response<RCApiResponse> response) {
                RCApiResponse kabinettApiResponse = response.body();
                Log.d(TAG,"Get public key "+kabinettApiResponse.getData());

            }

            @Override
            public void onFailure(Call<RCApiResponse> call, Throwable t) {

            }
        });
    }

    private void disableForegroundDispatchSystem() {
        mTagManager.getAdapter().disableForegroundDispatch(this);
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
        mEtMessage.setVisibility(View.GONE);
        mInfoLayout.setVisibility(View.VISIBLE);
    }
}
