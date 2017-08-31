package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;
import com.laoodao.caididi.retrofit.weather.WeatherId;

import java.util.List;

/**
 * Created by WORK on 2017/2/20.
 */

public class Home {
    @SerializedName("img_arr")
    public List<Link> imgArr;//轮播图片
    public List<Menu> menu;//动态菜单
    public List<FarmingList> news;//最新资讯
    public String local; //地理位置
    @SerializedName("weather_id")
    public WeatherId weatherId; //天气标示
    public String weather; //今日天气
    public String temperature; //温度间隔
    public String humidity; //今日湿度
    public String date; //农历信息
    @SerializedName("mid_menu")
    public List<Menu> midMenu;

}
