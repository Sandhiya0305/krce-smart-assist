package com.krce.mobile.api;

import com.krce.mobile.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ApiClient() {}

    public static KrceApi service() {
        return RETROFIT.create(KrceApi.class);
    }
}
