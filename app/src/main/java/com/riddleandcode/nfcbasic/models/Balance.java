package com.riddleandcode.nfcbasic.models;

import com.riddleandcode.nfcbasic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Eyliss on 1/26/17.
 */

public class Balance {

    private String network;
    private String address;
    private String confirmedBalance;
    private String unconfirmedBalance;

    public Balance(JSONObject balance){
        try {
            if(balance.has(Constants.JSON_NETWORK)){
                network = balance.getString(Constants.JSON_NETWORK);
            }
            if(balance.has(Constants.JSON_ADDRESS)){
                address = balance.getString(Constants.JSON_ADDRESS);
            }
            if(balance.has(Constants.JSON_CONFIRMED_BALANCE)){
                confirmedBalance = balance.getString(Constants.JSON_CONFIRMED_BALANCE);
            }
            if(balance.has(Constants.JSON_UNCONFIRMED_BALANCE)){
                unconfirmedBalance = balance.getString(Constants.JSON_UNCONFIRMED_BALANCE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getNetwork() {
        return network;
    }

    public String getAddress() {
        return address;
    }

    public String getConfirmedBalance() {
        return confirmedBalance;
    }

    public String getUnconfirmedBalance() {
        return unconfirmedBalance;
    }
}
