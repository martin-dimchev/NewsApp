package com.example.news_app;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    @GET("v2/top-headlines")
    Call<NewsResponse> getNews(
            @Query("category") String category,
            @Query("apiKey") String apiKey,
            @Query("language") String language
    );

    public static NewsApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(NewsApiService.class);
    }
}

