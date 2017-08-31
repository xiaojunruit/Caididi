package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.main.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORK on 2017/3/7.
 */

public class SUReserver {
    public String desc;
    @SerializedName("finish_time")
    public String finishTime;
    public ArrayList<Answer.Img> imgArr;
}
