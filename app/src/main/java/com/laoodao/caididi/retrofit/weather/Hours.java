package com.laoodao.caididi.retrofit.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/17.
 */

public class Hours {

    public String airp;

    public String aqi;

    public String cw;

    public String cwd;
    @SerializedName("is_now")
    public boolean isNow;
    public String rh;
    @SerializedName("show_time")
    public String showTime;
    public String st;
    @SerializedName("tip_aqi")
    public String tipAqi;


    public int tmp;
    public String w;
    public String wd;
    public String wdg;
    public int res;
/*
    "cw": "01",//天气现象编码
            "w": "多云",//天气现象
            "rh": 30,//相对湿度
            "cwd": "07",//风向编码
            "wd": "东风",//风向描述
            "wdg": 0,//风力级别
            "tmp": 3,//预测温度
            "airp": 1019,//气压
            "st": -4//体感温度
            "show_time": "12:00",  //格式化时间
            "is_now": true      //是否现在*/
}
