package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;

public class WeatherDay {
    
    // Datos obligatorios según laboratorio
    private String date;
    
    @SerializedName("maxtemp_c")
    private double maxTempC;
    
    @SerializedName("mintemp_c") 
    private double minTempC;
    
    private Condition condition;
    
    // Datos adicionales importantes (sin excederse)
    @SerializedName("avghumidity")
    private int avgHumidity;
    
    @SerializedName("maxwind_kph")
    private double maxWindKph;
    
    @SerializedName("uv")
    private double uv;
    
    // Para API Future/History: datos por horas
    @SerializedName("hour")
    private java.util.List<Hour> hour;
    
    // Constructors
    public WeatherDay() {}
    
    // Getters y Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public double getMaxTempC() { return maxTempC; }
    public void setMaxTempC(double maxTempC) { this.maxTempC = maxTempC; }
    
    public double getMinTempC() { return minTempC; }
    public void setMinTempC(double minTempC) { this.minTempC = minTempC; }
    
    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    
    public int getAvgHumidity() { return avgHumidity; }
    public void setAvgHumidity(int avgHumidity) { this.avgHumidity = avgHumidity; }
    
    public double getMaxWindKph() { return maxWindKph; }
    public void setMaxWindKph(double maxWindKph) { this.maxWindKph = maxWindKph; }
    
    public double getUv() { return uv; }
    public void setUv(double uv) { this.uv = uv; }
    
    public java.util.List<Hour> getHour() { return hour; }
    public void setHour(java.util.List<Hour> hour) { this.hour = hour; }
}