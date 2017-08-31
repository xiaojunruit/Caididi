package com.laoodao.caididi.retrofit.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class ExperList implements Serializable{


    public String id;
    public String cid;
    public String username;
    public String mobile;
    @SerializedName("member_avatar")
    public String memberAvatar;
    public String special;
    public List<String> crops;
    public String introduce;
    @SerializedName("member_nick")
    public String memberNick;
    public String truename;

}
