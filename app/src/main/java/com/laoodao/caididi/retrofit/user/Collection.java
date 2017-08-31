package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/3/9.
 */

public class Collection {
    public int id;
    @SerializedName("pay_money")
    public String money;
    public String suppler;
    @SerializedName("pay_date")
    public String payDate;
    public boolean isCheck;
}
