package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/22.
 */

public class FollowUser {
    @SerializedName("member_name")
    public String memberName;
    @SerializedName("member_avatar")
    public String memberAvatar;
    @SerializedName("member_id")
    public int memberId;
    @SerializedName("state")
    public int state; //1.已关注，对方没关注我。2：互相关注3：
    public int cid;

    @SerializedName("answer_total")
    public String answerTotal;
    @SerializedName("praise_total")
    public String praiseTotal;
    public String atttotal;
    public int type;
    public boolean is_self;


}
