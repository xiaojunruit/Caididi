package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XiaoGe on 2017/2/4.
 */

public class DynamicMenu {
    public String value;
    @SerializedName("is_new")
    public boolean isNew;
    public String title;
    public String icon;
    public String url;

}
