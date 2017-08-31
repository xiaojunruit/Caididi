package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/28.
 */

public class NewestInfo {
    public int id;
    @SerializedName("view_total")
    public int viewTotal;
    @SerializedName("answer_total")
    public int answerTotal;
    public String names;
    @SerializedName("names_total")
    public int namesTotal;


}
