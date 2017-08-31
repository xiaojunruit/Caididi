package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/2/20.
 */

public class FarmingList {
    public int id;
    public String title;
    public String image;
    @SerializedName("view_count")
    public int viewCount;
    @SerializedName("add_time")
    public String addTime;
    @SerializedName("gc_name")
    public String gcName;
}
