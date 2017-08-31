package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by WORK on 2017/3/10.
 */

public class OrderDetail {
    public int id;
    @SerializedName("order_state")
    public String orderState;
    public int status;
    public String lastoperate;
    @SerializedName("purchase_code")
    public String purchaseCode;
    @SerializedName("total_money")
    public String totalMoney;
    @SerializedName("left_money")
    public String surplusMoney;
    public String lastoperatetime;
    @SerializedName("detial_list")
    public List<DetailList> detailList;
    @SerializedName("pic_prefere")
    public String picPrefere;

    public class DetailList {
        @SerializedName("product_name")
        public String productName;
        @SerializedName("gross_weight")
        public String grossWeight;
        @SerializedName("other_weight")
        public String otherWeight;
        @SerializedName("net_weight")
        public String netWeight;
        public String money;
        public String price;
        @SerializedName("money_spec")
        public String moneySpec;
        public String spec;
        @SerializedName("package_weight")
        public String packageWeight;
        @SerializedName("defective_goods")
        public String defectiveGoods;
        public String content;
    }
}
