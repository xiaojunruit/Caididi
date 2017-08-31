package com.laoodao.caididi.retrofit.main;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */

public class Answer implements Serializable {
    public int id;
    public String title;
    public String content;
    @SerializedName("member_avatar")
    public String memberAvatar;
    @SerializedName("member_name")
    public String memberName;
    @SerializedName("add_time")
    public String addTime;
    public String local; //定位地址
    @SerializedName("view_total")
    public int readCount;//浏览数量
    public String names;//xx想知道答案
    @SerializedName("names_total")
    public int namesTotal;//多少人想知道答案
    public ArrayList<Img> imgArr;
    @SerializedName("img_num")
    public int imgNum;
    @SerializedName("answer_total")
    public int answerTotal;
    public boolean wanttoknow;
    @SerializedName("share_info")
    public ShareInfo shareInfo;

    public int cid;


    public class Img implements Serializable {
        public int height;
        public int width;
        public String url;
    }
}
