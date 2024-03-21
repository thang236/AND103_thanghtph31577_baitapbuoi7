package com.duyle.lap1.Retrofit;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitCategory {

    @GET("api/category/")
    Call<JSONObject> getAllCategory();
}
