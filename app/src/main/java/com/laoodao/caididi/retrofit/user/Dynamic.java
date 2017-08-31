package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/1/16.
 */

public class Dynamic {

    public int id;
    public int cid;
    public String notice;
    public String title;
    public String img;
    public String content;
    public String local;
    public int type;//类型  1:回答，2：评论 ，3：回复
    @SerializedName("add_time")
    public String addTime;
    @SerializedName("member_name")
    public String nickname;

    public String getTypeName() {
        switch (type) {
            case 1:
                return "回答了提问";
            case 2:
                return "评论了提问";
            case 3:
                return "回复了提问";
        }
        return "";
    }

}
