package com.laoodao.caididi.retrofit.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/17.
 */

public class Today {

    public String city;
    @SerializedName("comfort_index")
    public String comfortIndex;
    @SerializedName("date_y")
    public String dateY;
    @SerializedName("dressing_advice")
    public String dressingAdvice;
    @SerializedName("dressing_index")
    public String dressingIndex;
    @SerializedName("drying_index")
    public String dryingIndex;
    @SerializedName("exercise_index")
    public String exerciseIndex;

    public String temperature;
    @SerializedName("travel_index")
    public String travelIndex;
    @SerializedName("uv_index")
    public String uvIndex;
    @SerializedName("wash_index")
    public String washIndex;
    public String weather;

    @SerializedName("weather_id")
    public WeatherId weatherId;


    @SerializedName("min_temperature")
    public String minTemperature;

    @SerializedName("max_temperature")
    public String maxTemperature;
}
