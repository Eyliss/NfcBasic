package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.managers.TagManager;
import com.riddleandcode.nfcbasic.utils.Util;

import io.fabric.sdk.android.Fabric;

import org.apache.commons.codec.DecoderException;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.OperatorCreationException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Security;
import java.security.cert.CertificateException;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = VerificationActivity.class.getSimpleName();

    private ProgressBar mProgressBar;
    private Tag tagFromIntent;
    private TagManager mTagManager;

    private TextView mResultMessage;
    private TextView mResult;
    private TextView mResponseDetails;

    private String mMessage;

    public VerificationActivity() throws DecoderException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_verification);
        bindViews();

        try {
            mTagManager = new TagManager(this);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    private void bindViews(){
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mResultMessage = (TextView) findViewById(R.id.result_message);
        mResult = (TextView) findViewById(R.id.verification_result);
        mResponseDetails = (TextView) findViewById(R.id.verification_response_details);
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

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            try {
                mTagManager.ntagInit(tagFromIntent);
                mTagManager.ntagConnect();

//                mMessage = mEtMessage.getText().toString();
//                mMessage = "Hello world";
                mProgressBar.setProgress(View.VISIBLE);
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
            boolean verified = mTagManager.checkSign();
            mProgressBar.setProgress(View.GONE);

            mResultMessage.setText(verified ? R.string.verification_successfully : R.string.verification_fail);
            mResult.setText("The product is an original produced by the brand A in 2017");
            mResponseDetails.setText(Util.bytesToHex(mTagManager.getSignature()));

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CMSException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }
    }

    private void disableForegroundDispatchSystem() {
        mTagManager.getAdapter().disableForegroundDispatch(this);
    }

}

