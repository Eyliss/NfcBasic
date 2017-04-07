package com.riddleandcode.nfcbasic.api;

import com.riddleandcode.nfcbasic.NfcBasicApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Eyliss on 6/10/16.
 */
public class ServiceGenerator {

    public static String API_BASE_URL = "http://ec2-34-251-133-19.eu-west-1.compute.amazonaws.com:8000/v1/";
//    public static String AUTH_TOKEN = "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0ODg5Njc0MzAsImlhdCI6MTQ4ODg4MTAzMCwibmJmIjoxNDg4ODgxMDMwLCJpZGVudGl0eSI6MTIzNH0.TbokF4ZoripQHeNwHfQdnDnmAMRHa5NqmL4hqvjxxLE";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
          new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit() {
        OkHttpClient client = httpClient.build();
        return builder.client(client).build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                      .header("Content-Type", "application/json")
                      .method(original.method(), original.body());
//                      .header("Authorization",AUTH_TOKEN)


                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}