package com.riddleandcode.nfcbasic.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.activities.TagReaderActivity;
import com.riddleandcode.nfcbasic.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidateFragment extends Fragment {

    private Button mValidateButton;

    public ValidateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_validate, container, false);

        mValidateButton = (Button)rootView.findViewById(R.id.validate_button);
        mValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startValidateProcess();
            }
        });
        return rootView;
    }

    private void startValidateProcess(){
        Intent intent = new Intent(getActivity(), TagReaderActivity.class);
        intent.putExtra(Constants.INTENT_PROCESS_TYPE,Constants.VALIDATION);
        startActivity(intent);
    }
}
