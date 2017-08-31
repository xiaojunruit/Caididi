package com.laoodao.caididi.retrofit.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/17.
 */

public class SK {

    public String humidity;
    public String temp;
    public String time;
    @SerializedName("wind_direction")
    public String windDirection;
    @SerializedName("wind_strength")
    public String windStrength;

}
