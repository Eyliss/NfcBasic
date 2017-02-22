package com.riddleandcode.nfcbasic.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riddleandcode.nfcbasic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidateFragment extends Fragment {


    public ValidateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_validate, container, false);
    }

}
