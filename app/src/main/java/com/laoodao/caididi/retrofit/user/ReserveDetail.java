package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.main.Answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORK on 2017/3/7.
 */

public class ReserveDetail implements Serializable {
    public ArrayList<Answer.Img> imgArr;
    @SerializedName("crops_name")
    public String cropsName;
    public String acreage;
    public String remark;
    @SerializedName("add_time")
    public String addTime;
    @SerializedName("update_time")
    public String updateTime;
    @SerializedName("true_name")
    public String realName;
    public String mobile;
    public int state;
    @SerializedName("state_desc")
    public String stateDesc;
    public SUReserver finish;
}
