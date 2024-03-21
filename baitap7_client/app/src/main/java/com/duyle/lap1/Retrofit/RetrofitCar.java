package com.duyle.lap1.Retrofit;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitCar {
    @GET("car/")
    Call<JsonObject> getAllCar();

    @GET("car/{id}")
    Call<JsonObject> getCarById(@Path("id") String carId );

    @DELETE("car/del/{id}")
    Call<Void> deleteCar(@Path("id") String carId);

    @PUT("car/update/{id}")
    Call<Void> updateCar(@Path("id") String carId, @Body RequestBody requestBody);

    @POST("car/create")
    Call<Void> postCar(@Body RequestBody requestBody);

}
