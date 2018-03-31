package com.jluandroid.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class LifeStyle {

    @SerializedName("brf")
    public String briefDescription;

    @SerializedName("txt")
    public String detailedDescription;

    @SerializedName("type")
    public String descriptionType;
}
