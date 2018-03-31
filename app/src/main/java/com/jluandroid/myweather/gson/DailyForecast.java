package com.jluandroid.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class DailyForecast {

    @SerializedName("cond_code_d")
    public int conditionCodeDay;

    @SerializedName("cond_code_n")
    public int conditionCodeNight;

    @SerializedName("cond_txt_d")
    public String conditionTextDay;

    @SerializedName("cond_txt_n")
    public String conditionTextNight;

    @SerializedName("date")
    public String dateToday;

    @SerializedName("hum")
    public int humidity;

    @SerializedName("mr")
    public String moonRiseTime;

    @SerializedName("ms")
    public String moonSetTime;

    @SerializedName("pcpn")
    public int precipitation;

    @SerializedName("pop")
    public int precipitationPossibility;

    @SerializedName("pres")
    public int pressure;

    @SerializedName("sr")
    public String sunRiseTime;

    @SerializedName("ss")
    public String sunSetTime;

    @SerializedName("tmp_max")
    public int temperatureMax;

    @SerializedName("tmp_mix")
    public int temperatureMin;

    @SerializedName("uv_index")
    public int uvIndex;

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
