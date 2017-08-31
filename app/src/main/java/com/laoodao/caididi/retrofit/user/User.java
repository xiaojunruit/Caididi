package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/1/14.
 */

public class User extends LoginInfo{
    @SerializedName("answer_total")
    public int answerTotal;
    public int state;
}
