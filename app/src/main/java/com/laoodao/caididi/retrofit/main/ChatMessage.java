package com.laoodao.caididi.retrofit.main;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2017/2/16.
 */

public class ChatMessage {
    public int id;
    public int cid;
    public String content;
    public int type;
    @SerializedName("member_avatar")
    public String memberAvatar;
    public String time;
    @SerializedName("is_self")
    public boolean isSelf;
    public boolean hasNotice() {
        return !TextUtils.isEmpty(content);
    }
}
