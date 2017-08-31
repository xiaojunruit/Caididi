package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/2/20.
 */

public class Menu {
    public String icon;
    public String title;
    public String url;
    @SerializedName("is_insect")
    public boolean isInsect;
}
