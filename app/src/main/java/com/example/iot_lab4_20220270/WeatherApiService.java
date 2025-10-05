package com.example.iot_lab4_20220270;

import com.example.iot_lab4_20220270.models.WeatherResponse;
import com.example.iot_lab4_20220270.models.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    
    String API_KEY = "5586a0acd5a345e0b4361158250210";
    String BASE_URL = "http://api.weatherapi.com/v1/";
    
    @GET("search.json")
    Call<List<Location>> searchLocations(
            @Query("key") String apiKey,
            @Query("q") String query
    );
    
    @GET("forecast.json")
    Call<WeatherResponse> getForecast(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("days") int days
    );
    
    @GET("future.json")
    Call<WeatherResponse> getFutureWeather(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("dt") String date
    );
    
    @GET("history.json")
    Call<WeatherResponse> getHistoryWeather(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("dt") String date
    );
}