package com.theelitedevelopers.teamup.core.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//
// Created on 7/24/2021 by VICTOR
// Copyright (c) 2023 TeamUp. All rights reserved.
//
public class ServiceGenerator {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private static Retrofit retrofit = null;
    private static ServiceGenerator instance;

    public static synchronized ServiceGenerator getInstance() {
        if (instance == null) {
            instance = new ServiceGenerator();
        }
        return instance;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    Gson gson = new GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create();

    private ServiceGenerator() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }

}
