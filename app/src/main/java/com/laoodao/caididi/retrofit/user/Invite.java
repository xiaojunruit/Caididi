package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.main.ShareInfo;

/**
 * Created by WORK on 2017/3/28.
 */

public class Invite {
    public String desc;
    @SerializedName("desc_long")
    public String descLong;
    @SerializedName("invite_num")
    public String inviteNum;
    public String laodao;
    @SerializedName("laodao_name")
    public String laodaoName;
    @SerializedName("share_info")
    public ShareInfo shareInfo;
}
