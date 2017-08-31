package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 2016/12/29.
 */

public class Comment{
    public String id;
    @SerializedName("member_name")
    public String memberName;
    @SerializedName("replay_member_name")
    public String replayMemberName;
    @SerializedName("replay_content")
    public String replayContent;




    public int circlePosition;
    public int commentPosition;
}