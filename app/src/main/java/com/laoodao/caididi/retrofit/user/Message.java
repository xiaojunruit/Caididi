package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/1/11.
 */

public class Message {
    public int id;
    public String time;
    public String notice;
    public int num;
    public String img;
    public String title;
    public String content;
    @SerializedName("member_name")
    public String memberName;
    @SerializedName("notice_type")
    public int noticeType;
}
