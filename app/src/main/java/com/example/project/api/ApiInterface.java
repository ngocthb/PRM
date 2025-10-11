package com.example.project.api;

import com.example.project.model.Currency;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/live")
    Call<Currency> convertToVND(
            @Query("access_key") String access_key,
            @Query("currencies") String currencies,
            @Query("source") String source,
            @Query("format") int format
    );
}
