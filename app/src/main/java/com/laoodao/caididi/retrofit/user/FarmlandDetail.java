package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.main.City;

import java.io.Serializable;

/**
 * Created by WORK on 2017/2/9.
 */

public class FarmlandDetail implements Serializable {


    public int id;

    public String acreage;

    @SerializedName("crops_name")
    public String cropName;
    @SerializedName("count_money")
    public String money;

    public String address;

    @SerializedName("area_1")
    public City  province;
    @SerializedName("area_2")
    public City city;

    @SerializedName("area_3")
    public City county;
    @SerializedName("area_4")
    public City township;

    @SerializedName("fulladdress")
    public String fullAddress;

}
