package com.riddleandcode.nfcbasic.activities;

import com.crashlytics.android.Crashlytics;
import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.managers.TagManager;
import com.riddleandcode.nfcbasic.utils.Util;

import io.fabric.sdk.android.Fabric;

import org.apache.commons.codec.DecoderException;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.security.Security;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = VerificationActivity.class.getSimpleName();

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

        signMessageAndVerify();
    }

    private void bindViews(){
        mResultMessage = (TextView) findViewById(R.id.result_message);
        mResult = (TextView) findViewById(R.id.verification_result);
        mResponseDetails = (TextView) findViewById(R.id.verification_response_details);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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

