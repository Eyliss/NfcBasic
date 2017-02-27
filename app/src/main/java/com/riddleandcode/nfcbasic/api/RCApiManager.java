package com.riddleandcode.nfcbasic.api;

import retrofit2.Call;
import retrofit2.Callback;

public class RCApiManager {

    static RCApiService RCService = ServiceGenerator.createService(RCApiService.class);

    public static void login(String email, String password, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.login(email,password);
        call.enqueue(callback);
    }

    public static void addUser(String username, String email, String password, String confirmation, String firstName, String lastName, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.addUser(username, email,password,confirmation,firstName,lastName);
        call.enqueue(callback);
    }

    public static void recoveryPassword(String email, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.recoveryPassword(email);
        call.enqueue(callback);
    }

    public static void getAddressBalance(String network, String address, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.getAddressBalance(network,address);
        call.enqueue(callback);
    }

    public static void getTransactions(String address, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.getTransactions(address);
        call.enqueue(callback);
    }

}
