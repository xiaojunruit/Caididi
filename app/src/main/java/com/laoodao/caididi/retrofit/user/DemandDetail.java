package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by XiaoGe on 2017/2/11.
 */

public class DemandDetail{
    public int id;
    public String title;
    public String content;
    public String price;
    @SerializedName("unit_name")
    public String unitName;
    @SerializedName("member_avatar")
    public String memberAvatar;
    @SerializedName("member_name")
    public String memberName;
    public int cid;
    public String contacts;
    public String mobile;
    public String email;
    public String local;
    @SerializedName("dem_num")
    public String demNum;
    @SerializedName("collect_total")
    public String collectTotal;
    @SerializedName("view_total")
    public String viewTotal;
    @SerializedName("img_arr")
    public List<String> imgArr;
    @SerializedName("expire_time")
    public String expireTime;
    @SerializedName("is_collect")
    public boolean isCollect;
}
