package com.laoodao.caididi.retrofit.main;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/27.
 */

public class AnswerDetail extends Answer{

    @SerializedName("is_collected")
    public boolean isCollected;
    @SerializedName("is_parise")
    public boolean isParise;
}
