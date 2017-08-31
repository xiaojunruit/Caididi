package com.laoodao.caididi.ui.weather.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.andview.refreshview.XRefreshView;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityWeatherBinding;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.retrofit.weather.SK;
import com.laoodao.caididi.retrofit.weather.Weather;
import com.laoodao.caididi.ui.weather.holder.Day7WeatherHolder;
import com.laoodao.caididi.ui.widget.citySelector.db.DatabaseHelper;
import com.laoodao.caididi.ui.widget.xRefreshView.WeatherRefreshHeader;

import java.util.List;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;

/**
 * Created by Administrator on 2017/2/17.
 */

public class WeatherActivity extends BaseActivity implements View.OnClickListener, XRefreshView.XRefreshViewListener {

    private ActivityWeatherBinding binding;
    private ItemAdapter mAdapter = new ItemAdapter(Day7WeatherHolder.class);
    private Drawable mDrawable;
    private City city = new City();
    private String mAreaId, mLon, mLat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        binding.refresh.setCustomHeaderView(new WeatherRefreshHeader(this));
        binding.setOnClick(this);
        StatusBarUtil.setTranslucent(this, 0);
        mAreaId = getIntent().getStringExtra("areaId");
        if (TextUtils.isEmpty(mAreaId)) {
            mLon = Global.getLocation().lon;
            mLat = Global.getLocation().lat;
        }
        initContentTopHeight();
        initDay7WeatherList();
        initData();
        startFanAnim();
        binding.horizontal.setToday24HourView(binding.day24Hour);
        binding.refresh.setXRefreshViewListener(this);
        binding.refresh.setMoveForHorizontal(true);
    }

    public static void start(Context context, String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString("areaId", areaId);
        ContextUtil.startActivity(context, WeatherActivity.class, bundle);

    }

    public static void start(Context context, String lon, String lat) {
        Bundle bundle = new Bundle();
        bundle.putString("lon", lon);
        bundle.putString("lat", lat);
        ContextUtil.startActivity(context, WeatherActivity.class, bundle);

    }


    private void startFanAnim() {
        // 加载动画
        AnimationDrawable bitFanAnim = (AnimationDrawable) binding.bigFan.getDrawable();
        // 默认进入页面就开启动画
        if (!bitFanAnim.isRunning()) {
            bitFanAnim.start();
        }

        // 加载动画
        AnimationDrawable samllFanAnim = (AnimationDrawable) binding.smallFan.getDrawable();
        // 默认进入页面就开启动画
        if (!samllFanAnim.isRunning()) {
            samllFanAnim.start();
        }
    }

    private void initData() {
        API.main().getWeather(mAreaId, mLon, mLat).compose(transform()).subscribe(result -> {
            Weather weather = result.data;
            mAdapter.addAll(result.data.future, true);
            setTemperature(result.data.sk);
            binding.setItem(result.data);
            binding.gpv.setProgress(20);
            //湿度
            String humidity = result.data.sk.humidity;
            binding.gpv.setProgress(Integer.parseInt(humidity.replace("%", "")));
            String maxTem = weather.today.maxTemperature.replace("℃", "");
            String minTem = weather.today.minTemperature.replace("℃", "");
            binding.day24Hour.setData(Integer.parseInt(maxTem), Integer.parseInt(minTem), weather.hours);
            binding.refresh.stopRefresh();
        });
    }

    private void initDay7WeatherList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.day7WeatherList.setLayoutManager(layoutManager);
        binding.day7WeatherList.setAdapter(mAdapter);
    }

    private void initContentTopHeight() {
        //获取toolbar坐标

        ViewTreeObserver vto = binding.title.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(() -> {
            final Rect rect = new Rect();
            binding.title.getWindowVisibleDisplayFrame(rect);

            int[] location = new int[2];
            binding.title.getLocationInWindow(location);

            int y = binding.title.getHeight() + location[1];
            //获取屏幕高度
            int heightPixels = Device.dm.heightPixels;
            binding.contentTop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightPixels - y));
        });
    }

    /**
     * 设置温度（图片代替数字）
     */
    private void setTemperature(SK sk) {
        String temp = sk.temp;
        if (!TextUtils.isEmpty(temp)) {

            //两位数
            if (temp.length() == 2 && !temp.contains("-")) {        //2位正数

                int temp1 = Integer.parseInt(temp.substring(0, 1));
                int temp2 = Integer.parseInt(temp.substring(1));
                binding.temperature1.setImageResource(getMipmapResId(temp1));
                binding.temperature2.setImageResource(getMipmapResId(temp2));
                binding.temperature1.setVisibility(View.VISIBLE);
                binding.temperature2.setVisibility(View.VISIBLE);
            } else if (temp.length() == 1 && !temp.contains("-")) {     //一位正数
                int temp1 = Integer.parseInt(temp);
                binding.temperature1.setImageResource(getMipmapResId(temp1));
                binding.temperature2.setVisibility(View.GONE);
            } else if (temp.length() == 2 && temp.contains("-")) {  // 负数 1wei
                binding.minus.setVisibility(View.VISIBLE);
                int temp1 = Integer.parseInt(temp.substring(1));
                binding.temperature2.setVisibility(View.GONE);
                binding.temperature1.setImageResource(getMipmapResId(temp1));
            } else if (temp.length() == 3 && temp.contains("-")) {
                binding.minus.setVisibility(View.VISIBLE);
                int temp1 = Integer.parseInt(temp.substring(1, 2));
                int temp2 = Integer.parseInt(temp.substring(2));
                binding.temperature1.setImageResource(getMipmapResId(temp1));
                binding.temperature2.setImageResource(getMipmapResId(temp2));
                binding.temperature1.setVisibility(View.VISIBLE);
                binding.temperature2.setVisibility(View.VISIBLE);
            }

        }

    }

    private int getMipmapResId(int temp) {
        return getResources().getIdentifier("number_" + temp, "mipmap", getPackageName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore(boolean isSilence) {

    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }
}
