package com.example.online_food.Class;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/?depth=3")
    Call<List<Province>> getProvinces();
}
