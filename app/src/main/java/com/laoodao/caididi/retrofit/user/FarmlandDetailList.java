package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by WORK on 2017/2/8.
 */

public class FarmlandDetailList {

    public String time;
    @SerializedName("type_name")
    public String typeName;
    @SerializedName("count_money")
    public String money;
    public int id;
    public String desc;
    public List<String> imgarr;
}
