package com.laoodao.caididi.ui.weather;

import com.laoodao.caididi.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/20.
 */

public class WeatherUtils {


    private static Map<String, Integer> mWeatherIcon = new HashMap<>();

    static {
        mWeatherIcon.put("00", R.mipmap.d_sunny); //晴天
        mWeatherIcon.put("01", R.mipmap.d_cloudy); // 多云
        mWeatherIcon.put("02", R.mipmap.d_shower); // 阵雨
        mWeatherIcon.put("03", R.mipmap.d_yin); // 阴
        mWeatherIcon.put("04", R.mipmap.d_thunder_shower); //雷阵雨
        mWeatherIcon.put("05", R.mipmap.d_hail); // 雷阵雨伴有冰雹
        mWeatherIcon.put("06", R.mipmap.d_snow_and_rains);//雨夹雪
        mWeatherIcon.put("07", R.mipmap.d_rains);//小雨
        mWeatherIcon.put("08", R.mipmap.d_moderate_rain);//中雨
        mWeatherIcon.put("09", R.mipmap.d_big_rains);//大雨
        mWeatherIcon.put("10", R.mipmap.d_heavy_rains);//暴雨
        mWeatherIcon.put("11", R.mipmap.d_big_heavy_rains);//大暴雨
        mWeatherIcon.put("12", R.mipmap.d_big_big_heavy_rains);//特大暴雨
        mWeatherIcon.put("13", R.mipmap.d_snow_shower);//阵雪
        mWeatherIcon.put("14", R.mipmap.d_small_snow);//小雪
        mWeatherIcon.put("15", R.mipmap.d_moderate_snow);//中雪
        mWeatherIcon.put("16", R.mipmap.d_big_snow);//大雪
        mWeatherIcon.put("17", R.mipmap.d_big_blizzard);//暴雪
        mWeatherIcon.put("18", R.mipmap.d_fog);//雾
        mWeatherIcon.put("19", R.mipmap.d_freezing_rain);//冻雨
        mWeatherIcon.put("20", R.mipmap.d_dust_storms);//沙尘暴

        mWeatherIcon.put("21", R.mipmap.d_rains);//小雨-中雨
        mWeatherIcon.put("22", R.mipmap.d_moderate_rain);//中雨-大雨
        mWeatherIcon.put("23", R.mipmap.d_big_rains);//大雨-暴雨
        mWeatherIcon.put("24", R.mipmap.d_heavy_rains);//暴雨-大暴雨
        mWeatherIcon.put("25", R.mipmap.d_big_heavy_rains);//大暴雨-特大暴雨
        mWeatherIcon.put("26", R.mipmap.d_small_snow);//小小雪-中雪
        mWeatherIcon.put("27", R.mipmap.d_moderate_snow);//中雪-大雪
        mWeatherIcon.put("28", R.mipmap.d_big_snow);//大雪-暴雪
        mWeatherIcon.put("29", R.mipmap.d_fly_ash);//浮尘
        mWeatherIcon.put("30", R.mipmap.d_sand_blowing);//扬沙
        mWeatherIcon.put("31", R.mipmap.d_strong_sandstorms);//强沙尘暴
        mWeatherIcon.put("53", R.mipmap.d_haze);//霾
    }

    public static int getWeatherResId(String id) {


        if (mWeatherIcon.containsKey(id)) {
            return mWeatherIcon.get(id);
        } else {
            return -1;
        }


    }

}
