package com.riddleandcode.nfcbasic.api;

import com.google.gson.JsonObject;

import org.json.JSONObject;

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
        JsonObject object = new JsonObject();
        object.addProperty("s",message);
        Call<RCApiResponse> call = RCService.sendHash(object);
        call.enqueue(callback);
    }

    public static void sendSignature(String signature, Callback<RCApiResponse> callback){
        JsonObject object = new JsonObject();
        object.addProperty("s",signature);
        Call<RCApiResponse> call = RCService.sendSignature(object);
        call.enqueue(callback);
    }

    public static void validate(String publicKey, String signature, String challenge, Callback<RCApiResponse> callback){
        JsonObject object = new JsonObject();
        object.addProperty("public_key",publicKey);
        object.addProperty("signature",signature);
        object.addProperty("challenge",challenge);

        Call<RCApiResponse> call = RCServiceWithAuth.validate(object);
        call.enqueue(callback);
    }

}
