package com.riddleandcode.nfcbasic.api;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Eyliss on 6/10/16.
 */
public interface RCApiService {

    //Get rng
    @GET("rng_get")
    Call<RCApiResponse> getRng();

    //Get public key
    @GET("pubk_get")
    Call<RCApiResponse> getPublicKey();

    //Send sendHash message
    @POST("hash")
    Call<RCApiResponse> sendHash(
          @Body JsonObject jsonObject
    );

    //Send signature
    @POST("sign")
    Call<RCApiResponse> sendSignature(
          @Body JsonObject jsonObject
    );

    //Send signature
    @POST("validate")
    Call<RCApiResponse> validate(
          @Body JsonObject jsonObject
    );
}
