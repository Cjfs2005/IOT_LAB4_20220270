package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.*;
import java.lang.reflect.Type;

public class WeatherDay {
    private String date;
    
    @SerializedName("maxtemp_c")
    private double maxTempC;
    
    @SerializedName("mintemp_c") 
    private double minTempC;
    
    private Condition condition;
    
    @SerializedName("avghumidity")
    private int avgHumidity;
    
    @SerializedName("maxwind_kph")
    private double maxWindKph;
    
    @SerializedName("uv")
    private double uv;
    
    @SerializedName("hour")
    private java.util.List<Hour> hour;
    
    public WeatherDay() {}
    
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

    public static class Deserializer implements JsonDeserializer<WeatherDay> {
        @Override
        public WeatherDay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            WeatherDay wd = new WeatherDay();
            JsonObject obj = json.getAsJsonObject();
            
            JsonElement dateEl = obj.get("date");
            if (dateEl != null && !dateEl.isJsonNull()) wd.setDate(dateEl.getAsString());
            
            JsonObject dayObj = obj.has("day") && obj.get("day").isJsonObject() ? obj.getAsJsonObject("day") : null;
            if (dayObj != null) {
                if (dayObj.has("maxtemp_c")) wd.setMaxTempC(dayObj.get("maxtemp_c").getAsDouble());
                if (dayObj.has("mintemp_c")) wd.setMinTempC(dayObj.get("mintemp_c").getAsDouble());
                if (dayObj.has("avghumidity")) wd.setAvgHumidity(dayObj.get("avghumidity").getAsInt());
                if (dayObj.has("maxwind_kph")) wd.setMaxWindKph(dayObj.get("maxwind_kph").getAsDouble());
                if (dayObj.has("uv")) wd.setUv(dayObj.get("uv").getAsDouble());
                if (dayObj.has("condition") && dayObj.get("condition").isJsonObject()) {
                    wd.setCondition(context.deserialize(dayObj.get("condition"), Condition.class));
                }
            }
            
            if (obj.has("hour") && obj.get("hour").isJsonArray()) {
                wd.setHour(java.util.Arrays.asList(context.deserialize(obj.get("hour"), Hour[].class)));
            }
            return wd;
        }
    }
}