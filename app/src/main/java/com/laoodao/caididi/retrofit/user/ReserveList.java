package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/3/7.
 */

public class ReserveList {
    public String id;
    @SerializedName("crops_name")
    public String cropsName;
    public String name;
    public String remark;
    public String mobile;
    @SerializedName("add_time")
    public String addTime;
    public int state;
    @SerializedName("state_desc")
    public String stateDesc;
}
