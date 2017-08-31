package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by WORK on 2017/2/20.
 */

public class FarmingDetail {
    public int id;
    public String title;
    public String content;
    public String source;
    @SerializedName("view_count")
    public String viewCount;
    @SerializedName("add_time")
    public String addTime;
    public List<FarmingList> tj;
    @SerializedName("share_info")
    public ShareInfo shareInfo;

}
