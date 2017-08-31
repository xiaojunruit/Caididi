package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/3/15.
 */

public class GreensRecord {
    public int id;
    @SerializedName("purchase_code")
    public String purchaseCode;
    @SerializedName("total_money")
    public String totalMoney;
    @SerializedName("left_money")
    public String leftMoney;
    @SerializedName("order_state")
    public String orderState;
    public String suppler;
    public int status;
    @SerializedName("pay_money")
    public String payMoney;
}
