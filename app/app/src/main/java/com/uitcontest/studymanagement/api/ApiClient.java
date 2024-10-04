package com.uitcontest.studymanagement.api;

import android.util.Log;

import com.uitcontest.studymanagement.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://172.26.137.166:8000/docs/";
    private static Retrofit retrofit = null;

    // Create OkHttpClient
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    // Create Retrofit instance
    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d("ApiClient", "Creating new Retrofit instance");

            // Add headers to OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else {
            // Reuse the existing Retrofit instance
            Log.d("ApiClient", "Reusing existing Retrofit instance");
        }
        return retrofit;
    }

    // Create ApiService instance
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}

