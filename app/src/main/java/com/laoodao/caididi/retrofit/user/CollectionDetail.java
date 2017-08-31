package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by WORK on 2017/3/10.
 */

public class CollectionDetail {
    @SerializedName("pay_money")
    public String money;
    @SerializedName("pay_date")
    public String addTime;
    public int status;
    public List<CollectionDetailList> list;

    public class CollectionDetailList implements Serializable {
        public int id;
        @SerializedName("purchase_code")
        public String purchaseCode;
        @SerializedName("total_money")
        public String totalMoney;
        @SerializedName("left_money")
        public String surplusMoney;
        @SerializedName("pay_money")
        public String payMoney;
    }

}
