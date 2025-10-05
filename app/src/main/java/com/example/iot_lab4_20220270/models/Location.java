package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/*
Modelo: GPT-5 (en modo Ask usando Github Copilot para que reciba contexto del proyecto)
Prompt: "En base a las indicaciones del laboratorio, propón y modela los models/beans que serán necesarios para consumir WeatherAPI (search, forecast, future, history). Incluye un modelo Location con id, name, region, country, lat, lon y url usando Gson @SerializedName, implementando Serializable para poder pasarlo en un bundle entre fragments."
Correcciones: "Se aceptó directamente la propuesta. No fue necesario cambiar nombres de campos. Solo se tuvo que asegurar que 'id' se mantenga como int y que el modelo permanezca sin herencias para alinearse con lo visto en clase." 
*/
public class Location implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("region")
    private String region;
    
    @SerializedName("country")
    private String country;
    
    @SerializedName("lat")
    private double lat;
    
    @SerializedName("lon")
    private double lon;
    
    @SerializedName("url")
    private String url;
    
    public Location() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}