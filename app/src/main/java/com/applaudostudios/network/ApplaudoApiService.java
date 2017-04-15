package com.applaudostudios.network;

import com.applaudostudios.models.Stadium;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Tarles on 08/04/2017.
 */

public interface ApplaudoApiService
{
    @GET("applaudo_homework.json")
    public Call<List<Stadium>> getJSON();
}
