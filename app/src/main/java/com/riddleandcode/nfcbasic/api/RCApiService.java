package com.riddleandcode.nfcbasic.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
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

    //Authenticate a user
    @GET("rng_get")
    Call<RCApiResponse> getRng();

    //Add a new user
    @GET("pubk_get")
    Call<RCApiResponse> getPublicKey();

    //Send reset password email to user
    @FormUrlEncoded
    @POST("hash")
    Call<RCApiResponse> hash(
          @Field("s") String message
    );

    //Get an address balance
    @POST("sign")
    Call<RCApiResponse> sign(
          @Field("s") String signature
    );
}
