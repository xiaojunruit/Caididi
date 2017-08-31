package com.laoodao.caididi.retrofit.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class Future {

    public String date;

    public String temperature;
    public String weather;
    public String week;
    public String wind;

    @SerializedName("weather_id")
    public WeatherId weatherIdList;

}
