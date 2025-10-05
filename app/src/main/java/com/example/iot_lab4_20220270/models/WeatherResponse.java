package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    private Location location;
    @SerializedName("forecast")
    private ForecastContainer forecast;
    public WeatherResponse() {}

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public ForecastContainer getForecast() { return forecast; }
    public void setForecast(ForecastContainer forecast) { this.forecast = forecast; }
    
    public static class ForecastContainer {
        @SerializedName("forecastday")
        private List<WeatherDay> forecastday;
        
        public List<WeatherDay> getForecastday() { return forecastday; }
        public void setForecastday(List<WeatherDay> forecastday) { this.forecastday = forecastday; }
    }
}