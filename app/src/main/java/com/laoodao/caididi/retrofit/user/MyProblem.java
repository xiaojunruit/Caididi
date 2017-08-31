package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/29.
 */

public class MyProblem {
    public int id;
    public String title;
    public String img;
    public String content;
    public String local;
    @SerializedName("add_time")
    public String addTime;
    @SerializedName("comment_total")
    public String commentTotal;
    @SerializedName("comment")
    public String comment;
    @SerializedName("answer_total")
    public String answer;
    @SerializedName("view_total")
    public String viewTotal;
    @SerializedName("know_total")
    public String know;
}
