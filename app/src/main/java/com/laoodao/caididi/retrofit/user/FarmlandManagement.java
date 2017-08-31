package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by XiaoGe on 2017/2/8.
 */

public class FarmlandManagement {
    public int id;
    @SerializedName("crops_name")
    public String cropsName;
    public String acreage;
    public String latestoptime;
    public String latestop;//最后操作内容

    @SerializedName("add_time")
    public String addTime;
    public List<String> imgarr;
    public String local;
}
