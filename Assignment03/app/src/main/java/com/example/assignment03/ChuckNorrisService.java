package com.example.assignment03;

import com.example.assignment03.Model.KeywordResponse;
import com.example.assignment03.Model.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChuckNorrisService {
    @GET("jokes/categories")
    Call<List<String>> getCategory();

    @GET("jokes/random")
    Call<Result> getRandom();

    @GET("jokes/random")
    Call<Result> getByCategory(@Query("category") String category);

    @GET("jokes/search")
    Call<KeywordResponse> getByText(@Query("query") String query);
}
