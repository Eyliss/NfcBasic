package com.riddleandcode.nfcbasic.activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.fragments.LoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener {

    private static final String LOGIN_FRAGMENT_TAG = "login_fragment";

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
                showLoginFragment();
            }
        });
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpScreen();
            }
        });

    }

    private void showLoginFragment(){
        mRegisterContainer.setVisibility(View.GONE);
        mLoginContainer.setVisibility(View.VISIBLE);
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
        if(mRegisterContainer.getVisibility() == View.VISIBLE){
            finish();
        }else{
            mRegisterContainer.setVisibility(View.VISIBLE);
            mLoginContainer.setVisibility(View.GONE);
        }
    }
}

