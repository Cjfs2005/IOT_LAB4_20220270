package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    
    // Información de la ubicación
    private Location location;
    
    // Pronósticos por días - mapea el "forecast.forecastday" del JSON original
    @SerializedName("forecast")
    private ForecastContainer forecast;
    
    // Constructors
    public WeatherResponse() {}
    
    // Getters y Setters
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public ForecastContainer getForecast() { return forecast; }
    public void setForecast(ForecastContainer forecast) { this.forecast = forecast; }
    
    // Clase interna para mapear la estructura "forecast" del JSON
    public static class ForecastContainer {
        @SerializedName("forecastday")
        private List<WeatherDay> forecastday;
        
        public List<WeatherDay> getForecastday() { return forecastday; }
        public void setForecastday(List<WeatherDay> forecastday) { this.forecastday = forecastday; }
    }
}