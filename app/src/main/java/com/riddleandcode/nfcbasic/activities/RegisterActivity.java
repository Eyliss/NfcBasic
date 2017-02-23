package com.riddleandcode.nfcbasic.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.fragments.LoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener {

    // UI references.
    private Button mStartButton;
    private Button mSignUpButton;
    private RelativeLayout mRegisterContainer;
    private RelativeLayout mLoginContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mStartButton = (Button) findViewById(R.id.start_demo_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mRegisterContainer = (RelativeLayout) findViewById(R.id.register_container);
        mLoginContainer = (RelativeLayout)findViewById(R.id.login_fragment);
        mLoginContainer.setVisibility(View.GONE);

        mStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginContainer.setVisibility(View.VISIBLE);
                mRegisterContainer.setVisibility(View.GONE);
            }
        });
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpScreen();
            }
        });

    }

    private void goToSignUpScreen(){
        Toast.makeText(this,getString(R.string.feature_not_available),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(this,MainScreenSlidePagerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(mLoginContainer.getVisibility() == View.VISIBLE){
            mLoginContainer.setVisibility(View.GONE);
            mRegisterContainer.setVisibility(View.VISIBLE);
        }else{
            finish();
        }
    }
}

