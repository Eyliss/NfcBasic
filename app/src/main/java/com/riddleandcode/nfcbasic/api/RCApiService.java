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
    @FormUrlEncoded
    @POST("users/auth")
    Call<RCApiResponse> login(
          @Field("email") String email,
          @Field("password") String password);

    //Add a new user
    @FormUrlEncoded
    @POST("users")
    Call<RCApiResponse> addUser(
          @Field("username") String username,
          @Field("email") String email,
          @Field("password") String password,
          @Field("password_confirmation") String confirmation,
          @Field("first_name") String firstName,
          @Field("last_name") String lastName
    );

    //Send reset password email to user
    @FormUrlEncoded
    @POST("users/password/email")
    Call<RCApiResponse> recoveryPassword(
          @Field("email") String email
    );

    //Get an address balance
    @GET("get_address_balance/{network}/{address}")
    Call<RCApiResponse> getAddressBalance(
          @Path("network") String network,
          @Path("address") String address
    );

    //Get address transactions
    @GET("transactions/{address}")
    Call<RCApiResponse> getTransactions(
          @Path("address") String address
    );
}
