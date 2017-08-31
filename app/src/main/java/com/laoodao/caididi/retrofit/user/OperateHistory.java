package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by WORK on 2017/3/10.
 */

public class OperateHistory {
    @SerializedName("order_state")
    public String orderState;
    @SerializedName("purchase_code")
    public String purchaseCode;
    public String lastpaytime;
    public List<OperateList> operatelist;

    public class OperateList {
        public String content;
        @SerializedName("add_time")
        public String addTime;
    }
}
