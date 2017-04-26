package com.riddleandcode.nfcbasic.adapters;

import com.riddleandcode.nfcbasic.fragments.ValidateFragment;
import com.riddleandcode.nfcbasic.fragments.VerifyFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class MainScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private VerifyFragment mVerifyFragment;
    private ValidateFragment mValidateFragment;

    public MainScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        mVerifyFragment = new VerifyFragment();
        mValidateFragment = new ValidateFragment();
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position){
//            case 0:
//                return mVerifyFragment;
//            case 1:
//                return mValidateFragment;
//            default:
//                return mVerifyFragment;
//        }
        return mValidateFragment;

    }

    @Override
    public int getCount() {
        return 2;
    }
}
