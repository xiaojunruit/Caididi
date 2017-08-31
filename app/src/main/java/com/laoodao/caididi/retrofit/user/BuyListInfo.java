package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/2/13.
 */

public class BuyListInfo {


    @SerializedName("collect_total")
    public int collectTotal;

    @SerializedName("view_total")
    public int viewTotal;

    public String content;

    @SerializedName("expire_time")
    public String expireTime;

    public int id;

    public String local;

    public String price;

    public String title;

    @SerializedName("unit_name")
    public String unitName;

    @SerializedName("is_collect")
    public boolean isCollect;
}
