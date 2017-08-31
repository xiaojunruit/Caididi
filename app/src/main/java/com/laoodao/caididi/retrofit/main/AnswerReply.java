package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2016/12/17.
 */

public class AnswerReply {
    public int id;
    public int cid;
    @SerializedName("member_avatar")
    public String memberAvatar;
    @SerializedName("member_nick")
    public String memberNick;
    @SerializedName("add_time")
    public String addTime;
    public String local;
    public String content;
    @SerializedName("praise_name")
    public String praiseName;
    @SerializedName("praise_num")
    public int praiseNum;
    public List<Comment> comment;
    @SerializedName("comment_num")
    public String commentNum;
    public List<DetailImg> img;
    @SerializedName("my_praise")
    public boolean myPraise;
    public class DetailImg{
        public int height;
        public int width;
        public String url;
    }

}
