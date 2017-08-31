package com.laoodao.caididi.retrofit.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.main.Plants;

import java.util.List;

/**
 * Created by ezy on 15-9-29.
 */
// 抽彩售货基本信息
public class LoginInfo {
    public int cid;             // 用户id
    public String uid;          // 唯一用户id, 例：147****0015@163.com”
    public String nickname;     // 用户昵称
    public String avatar;
    public String email;
    public String IPAddress;
    @SerializedName("member_gc")
    public List<Plants> plantsList;
    public String mobile;
    @SerializedName("previous_login_ip")
    public String previousLoginIp;
    @SerializedName("previous_login_time")
    public String previousLoginTime;
    public String signature;
    public int question;
    public int provide;
    public int buy;
    public int transport;
    @SerializedName("attention_me")
    public int attentionMe;
    @SerializedName("my_attention")
    public int myAttention;
    @SerializedName("attention_crops")
    public int attentionCrops;
    @SerializedName("member_attention")
    public boolean memberAttention;
    @SerializedName("is_has_msg")
    public boolean isHasMsg;
    @SerializedName("is_receive_push")
    public boolean isReceivePush;
    public String qrcode;
    @SerializedName("is_qq_bind")
    public boolean isQqBind;
    @SerializedName("is_wx_bind")
    public boolean isWxBind;
    @SerializedName("qq_bind_name")
    public String qqBindName;
    @SerializedName("wx_bind_name")
    public String wxBindName;
    @SerializedName("invite_num")
    public String invitNum;
    public String number;
    public String emcode;
    public String username;
    public LoginInfo() {
    }


    public LoginInfo(String nickname) {
        this.nickname = nickname;
    }


/*
    "question": 17,	//问题总数
            "provide": 0,	//供货总数
            "buy": 0,　　	/购买需求总数
    "transport": 0  // 货运总数*/


}
