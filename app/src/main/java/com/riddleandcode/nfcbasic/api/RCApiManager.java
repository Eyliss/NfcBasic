package com.riddleandcode.nfcbasic.api;

import retrofit2.Call;
import retrofit2.Callback;

public class RCApiManager {

    static RCApiService RCService = ServiceGenerator.createService(RCApiService.class);

    public static void getRng(Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.getRng();
        call.enqueue(callback);
    }

    public static void getPublicKey(Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.getPublicKey();
        call.enqueue(callback);
    }

    public static void sendHashMessage(String message, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.sendHash(message);
        call.enqueue(callback);
    }

    public static void sendSignature(String signature, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCService.sendSignature(signature);
        call.enqueue(callback);
    }

}
