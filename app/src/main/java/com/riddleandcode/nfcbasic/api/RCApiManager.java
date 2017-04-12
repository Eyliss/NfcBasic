package com.riddleandcode.nfcbasic.api;

import retrofit2.Call;
import retrofit2.Callback;

public class RCApiManager {

    static RCApiService RCService = ServiceGenerator.createService(RCApiService.class,false);
    static RCApiService RCServiceWithAuth = ServiceGenerator.createService(RCApiService.class,true);

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

    public static void validate(String publicKey, String signature, String challenge, Callback<RCApiResponse> callback){
        Call<RCApiResponse> call = RCServiceWithAuth.validate(publicKey,signature,challenge);
        call.enqueue(callback);
    }

}
