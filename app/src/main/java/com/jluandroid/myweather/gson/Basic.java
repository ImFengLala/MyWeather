package com.jluandroid.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    @SerializedName("cid")
    public String cityId;

    @SerializedName("location")
    public String whichCounty;

    @SerializedName("parent_city")
    public String whichCity;

    @SerializedName("admin_area")
    public String whichProvince;

    @SerializedName("cnty")
    public String whichCountry;

    @SerializedName("lat")
    public double latitude;

    @SerializedName("lon")
    public double longitude;

    @SerializedName("tz")
    public double timeZone;

}
