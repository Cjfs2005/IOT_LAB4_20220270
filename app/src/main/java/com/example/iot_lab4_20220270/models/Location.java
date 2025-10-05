package com.example.iot_lab4_20220270.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

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