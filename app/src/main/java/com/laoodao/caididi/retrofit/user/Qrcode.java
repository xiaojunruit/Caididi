package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.main.ShareInfo;

/**
 * Created by WORK on 2017/2/28.
 */

public class Qrcode {
    public String avatar;
    public String nickname;
    public String qrcode;
    @SerializedName("share_info")
    public ShareInfo shareInfo;
}
