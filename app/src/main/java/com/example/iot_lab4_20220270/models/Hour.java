package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;

public class Hour {
    @SerializedName("time_epoch")
    private long timeEpoch;
    
    @SerializedName("time")
    private String time;
    
    @SerializedName("temp_c")
    private double tempC;
    
    @SerializedName("condition")
    private Condition condition;
    
    @SerializedName("humidity")
    private int humidity;
    
    @SerializedName("will_it_rain")
    private int willItRain;
    
    @SerializedName("chance_of_rain")
    private int chanceOfRain;
    
    @SerializedName("wind_kph")
    private double windKph;
    
    public Hour() {}
    
    public long getTimeEpoch() { return timeEpoch; }
    public void setTimeEpoch(long timeEpoch) { this.timeEpoch = timeEpoch; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public double getTempC() { return tempC; }
    public void setTempC(double tempC) { this.tempC = tempC; }
    
    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public int getWillItRain() { return willItRain; }
    public void setWillItRain(int willItRain) { this.willItRain = willItRain; }
    
    public int getChanceOfRain() { return chanceOfRain; }
    public void setChanceOfRain(int chanceOfRain) { this.chanceOfRain = chanceOfRain; }
    
    public double getWindKph() { return windKph; }
    public void setWindKph(double windKph) { this.windKph = windKph; }
}