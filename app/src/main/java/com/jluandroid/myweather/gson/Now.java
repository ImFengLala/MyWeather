package com.jluandroid.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("cloud")
    public int cloud;

    @SerializedName("cond_code")
    public int conditionCode;

    @SerializedName("cond_txt")
    public String conditionText;

    @SerializedName("fl")
    public int feltAirTemperature;

    @SerializedName("hum")
    public int relativeHumidity;

    @SerializedName("pcpn")
    public int precipitation;

    @SerializedName("pres")
    public int pressure;

    @SerializedName("tmp")
    public int temperature;

    @SerializedName("vis")
    public int visibility;

    @SerializedName("wind_deg")
    public int windDegree;

    @SerializedName("wind_dir")
    public String windDirection;

    @SerializedName("wind_sc")
    public String windForce;

    @SerializedName("wind_spd")
    public int windSpeed;

}
