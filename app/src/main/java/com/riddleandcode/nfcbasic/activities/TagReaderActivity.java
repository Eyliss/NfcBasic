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
import java.util.Arrays;

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

                //Hash message and write to the tag
                byte[] message = Util.hashString("Hello world");
                mTagManager.setChallenge(Util.bytesToHex(message));
                mValidationChallenge.setText(mTagManager.getChallenge());

                mTagManager.ntagSectorSelect((byte) 0x00);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,0,4), (byte) 0x04);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,4,8), (byte) 0x05);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,8,12), (byte) 0x06);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,12,16), (byte) 0x07);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,16,20), (byte) 0x08);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,20,24), (byte) 0x09);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,24,28), (byte) 0x0A);
                mTagManager.setNfcATimeout(20);

                mTagManager.ntagWrite(Arrays.copyOfRange(message,28,32), (byte) 0x0B);
                Thread.sleep(1000);

                mTagManager.ntagSectorSelect((byte) 0x00);

                //Read signature from tag
                String signature = "";

                for(int i = 0x10; i < 0x20; i = i+4){
                    mTagManager.ntagRead((byte) i);
                    signature += Util.bytesToHex(mTagManager.ntagGetLastAnswer());
                }

                mTagManager.setSignature(signature);
                mTvResult.setText(signature);

                //Read public key from tag
                String pubKey = "";

                for(int i = 0x20; i < 0x30; i = i+4){
                    mTagManager.ntagRead((byte) i);
                    pubKey += Util.bytesToHex(mTagManager.ntagGetLastAnswer());
                }

                mTagManager.setPublicKey(pubKey);
                mTvResponseDetails.setText(pubKey);

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
}
