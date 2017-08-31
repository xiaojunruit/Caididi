package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/6/16.
 */

public class MessageDetail {
    public String title;
    public String content;
    @SerializedName("add_time")
    public String addTime;
}
