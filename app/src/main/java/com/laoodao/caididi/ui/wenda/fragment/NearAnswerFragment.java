package com.laoodao.caididi.ui.wenda.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.AskEvent;
import com.laoodao.caididi.event.NewestInfoEvent;
import com.laoodao.caididi.event.ProvinceSelectEvent;
import com.laoodao.caididi.event.TabAddressEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.retrofit.main.NewestInfo;
import com.laoodao.caididi.ui.wenda.holder.AnswerHolder;
import com.laoodao.caididi.ui.widget.citySelector.activity.CitySelectorActivity;
import com.laoodao.caididi.ui.widget.citySelector.db.DatabaseHelper;
import com.laoodao.caididi.ui.widget.wheelPicker.OptionPicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.Prefs;

/**
 * Created by Administrator on 2016/12/14.
 */

public class NearAnswerFragment extends ListFragment {
    private String cityId;

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(AnswerHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        initData();
        listener();
        RxBus.ofType(NewestInfoEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            API.main().newestInfo(event.id).compose(transform()).subscribe(result -> {
                List<Answer> answers = adapter.getList();
                for (Answer answer : answers) {
                    for (NewestInfo newestInfo : result.data) {
                        if (answer.id == newestInfo.id) {
                            answer.readCount = newestInfo.viewTotal;
                            answer.answerTotal = newestInfo.answerTotal;
                            answer.names = newestInfo.names;
                            answer.namesTotal = newestInfo.namesTotal;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            });
        });

        RxBus.ofType(AskEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            onRefresh();
        });
        RxBus.ofType(ProvinceSelectEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            cityId = event.city.i;
            onRefresh();
        });
    }

    private void listener() {
        binding.loading.setOnCustomListener(() -> {
//            cityDialog();
            CitySelectorActivity.start(getActivity(), City.LEVEL_PROVINCE);
        });
        binding.tvPositioning.setOnClickListener(v -> {
            CitySelectorActivity.start(getActivity(), City.LEVEL_PROVINCE);
        });
    }

    private void initData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        List<City> cityList = dbHelper.getHistoryCity(City.LEVEL_PROVINCE);
        if (cityList != null && cityList.size() > 0) {
            cityId = cityList.get(0).i;
        }
        binding.tvPositioning.setVisibility(View.VISIBLE);
        binding.tvPositioning.setText("切换城市");
        Global.locator.registerLocationListener(mLocationListener);
        Global.locator.start();
        Global.locator.requestLocation();
    }

    BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.e("============>>>>" + location.getCity());

            if (TextUtils.isEmpty(location.getAddress().cityCode)) {
                binding.loading.customLayout(R.layout.widget_no_choose).customText("定位失败").customBtnText("选择城市").showCustom();
            } else {
                Global.setLocation(location);
                Global.locator.stop();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Global.locator.unRegisterLocationListener(mLocationListener);
        Global.locator.stop();
    }

    @Override
    protected void onPage(int page) {
        if (TextUtils.isEmpty(cityId)) {
            API.main().newAnswerList(page, "near", Global.getLocation().lon + "," + Global.getLocation().lat, "", "").compose(transform()).subscribe(this::onPageLoaded);
        } else {
            API.main().newAnswerList(page, "near", "", cityId, "").compose(transform()).subscribe(this::onPageLoaded);
        }
    }

}
