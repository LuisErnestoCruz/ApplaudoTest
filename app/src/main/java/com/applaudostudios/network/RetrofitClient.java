package com.applaudostudios.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tarles on 08/04/2017.
 */

public class RetrofitClient {

    private static final String URL_ROOT = "http://applaudostudios.com/external/";

    private static Retrofit getRetrofitInstance()
    {
        Retrofit retrofit = null;
        Retrofit.Builder retroBuilder = new Retrofit.Builder();
        retroBuilder.baseUrl(URL_ROOT);
        retroBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofit = retroBuilder.build();
        return retrofit;
    }

    public static ApplaudoApiService getApiService()
    {
        Retrofit retrofitInstance = getRetrofitInstance();
        return retrofitInstance.create(ApplaudoApiService.class);
    }
}
