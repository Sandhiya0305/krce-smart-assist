package com.krce.mobile.api;

import com.krce.mobile.model.ChatRequest;
import com.krce.mobile.model.ChatResponse;
import com.krce.mobile.model.ContactRequest;
import com.krce.mobile.model.SitePage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KrceApi {
    @GET("api/college/pages")
    Call<List<SitePage>> getPages();

    @GET("api/college/search")
    Call<List<SitePage>> search(@Query("q") String query);

    @POST("api/college/sync")
    Call<SyncResponse> sync();

    @POST("api/chat/ask")
    Call<ChatResponse> ask(@Body ChatRequest request);

    @POST("api/contact")
    Call<SimpleResponse> contact(@Body ContactRequest request);

    class SyncResponse {
        public int saved;
        public String message;
    }

    class SimpleResponse {
        public String message;
    }
}
