package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by WORK on 2017/2/15.
 */

public class OpDetail {


    public class CapitalCost {
        public int id;
        @SerializedName("crops_name")
        public String cropsName;
        @SerializedName("crops_money")
        public String cropsMoney;
    }

    public class MachineryCost {
        public int id;
        @SerializedName("machine_name")//machine_name=拖拉机
        public String mathineName;

        @SerializedName("machine_money")
        public String mathineMoney;
    }

    @SerializedName("type_name")
    public String typeName;
    public String remark;
    public String time;
    @SerializedName("capital_cost")
    public List<CapitalCost> capitalCostList;
    @SerializedName("machinery_cost")
    public List<MachineryCost> machineryCostList;
    public String artificial;


}
