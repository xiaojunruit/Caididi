package com.laoodao.caididi.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.andview.refreshview.XRefreshView;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FragmentHomeBinding;
import com.laoodao.caididi.event.CitySelectEvent;
import com.laoodao.caididi.event.RedDotEvent;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.ui.main.activity.HomeSearchActivity;
import com.laoodao.caididi.ui.main.holder.HomeGridHolder;
import com.laoodao.caididi.ui.main.holder.HomeLabelHolder;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.MessageActivity;
import com.laoodao.caididi.ui.nongye.holder.FarmingHolder;
import com.laoodao.caididi.ui.weather.activity.WeatherActivity;
import com.laoodao.caididi.ui.widget.SupportGridItemDecoration;
import com.laoodao.caididi.ui.widget.citySelector.activity.CitySelectorActivity;
import com.laoodao.caididi.ui.widget.citySelector.db.DatabaseHelper;
import com.laoodao.caididi.ui.widget.xRefreshView.RefreshHeader;

import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;

/**
 * Created by Administrator on 2016/12/6.
 */

public class HomeFragment extends BindingFragment<FragmentHomeBinding> implements View.OnClickListener, XRefreshView.XRefreshViewListener {

    private BaseAdapter mLabelAdapter = new ItemAdapter<>(HomeLabelHolder.class);

    private BaseAdapter mNewsAdapter = new ItemAdapter<>(FarmingHolder.class);

    private BaseAdapter mHomeGridAdapter=new ItemAdapter<>(HomeGridHolder.class);



    private City city;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        refresh(mAreaId, mPos);
    }

    /*
      未读消息
       */
    void initUnReadDot(boolean hasMessage) {
        if (hasMessage) {
            UnreadMsgUtils.show(binding.msgDot, 0);
            UnreadMsgUtils.setSize(binding.msgDot, Device.dp2px(10f));
        } else {
            binding.msgDot.setVisibility(View.GONE);
        }
    }

    private void weatherAnimation(float from, float to) {
        RotateAnimation animation = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        binding.imgCityArraw.startAnimation(animation);
    }

    private String mAreaId;
    private String mPos;

    private void initViews() {

        binding.loading.post(new Runnable() {
            @Override
            public void run() {
                binding.loading.showLoading();
            }
        });
        binding.loading.setOnRetryListener(() -> refresh(mAreaId, mPos));
        binding.setListener(this);
        binding.expandLayout.collapse();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.labelList.setLayoutManager(manager);
        binding.labelList.setAdapter(mLabelAdapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.list.setAdapter(mNewsAdapter);
        binding.refresh.setXRefreshViewListener(this);
        binding.refresh.setCustomHeaderView(new RefreshHeader(getActivity()));
        binding.refresh.setMoveForHorizontal(true);

        binding.gridList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.gridList.setAdapter(mHomeGridAdapter);
        binding.gridList.addItemDecoration(new SupportGridItemDecoration(getContext()));


        RxBus.ofType(CitySelectEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            mAreaId = event.city.i;
            onRefresh();
        });
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        List<City> cityList = dbHelper.getHistoryCity(City.LEVEL_CITY);
        if (cityList != null && cityList.size() > 0) {
            city = cityList.get(0);
            if (city != null) {
                mAreaId = city.i;
            } else {
                mPos = Global.getLocation().lon + "," + Global.getLocation().lat;
            }
        } else {
            mPos = Global.getLocation().lon + "," + Global.getLocation().lat;
        }

        binding.content.setFocusable(true);
        binding.content.setFocusableInTouchMode(true);
        binding.content.requestFocus();
        initUnReadDot(Global.info().isHasMsg);
        RxBus.ofType(RedDotEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            if (event.checkMessage == null) {
                initUnReadDot(event.isMes);
            } else {
                initUnReadDot(event.checkMessage.answer || event.checkMessage.dynamic || event.checkMessage.notice ? true : false);
            }
        });
    }

    private void refresh(String areaId, String pos) {
        API.main().home(areaId, pos)
                .compose(onErrorTransform((context, throwable) -> {
                    binding.loading.showError();
                    binding.refresh.stopRefresh();
                }))
                .subscribe(result -> {
                    binding.loading.showContent();
                    if (result.data!=null) {
                        binding.setItem(result.data);
                    }
                    if (result.data.menu!=null) {
                        mLabelAdapter.addAll(result.data.menu, true);
                    }
                    if (result.data.news!=null) {
                        mNewsAdapter.addAll(result.data.news, true);
                    }
                    if (result.data.midMenu!=null) {
                        mHomeGridAdapter.addAll(result.data.midMenu, true);
                    }
                    binding.refresh.stopRefresh();
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_expert:
//                ContextUtil.startActivity(getContext(),ExpertActivity.class);
//                break;
            case R.id.btn_weather:
                if (binding.expandLayout.getIsExpand()) {
                    weatherAnimation(90, 0);
                } else {
                    weatherAnimation(0, 90);
                }
                binding.expandLayout.toggleExpand();
                break;
//            case R.id.btn_today_price:
//                ContextUtil.startActivity(getActivity(), VegetablesPriceActivity.class);
//                break;
//            case R.id.btn_q:
//                if (Global.session().isLoggedIn()) {
//                    ContextUtil.startActivity(getContext(), MyAskActivity.class);
//                } else {
//                    ContextUtil.startActivity(getContext(), LoginPhoneActivity.class);
//                }
//                break;

            case R.id.btn_more_weather:

                WeatherActivity.start(getContext(), mAreaId);

                break;

            case R.id.loca:
                CitySelectorActivity.start(getActivity(), City.LEVEL_CITY);
                break;
            case R.id.btn_message:
                if (Global.session().isLoggedIn()) {
                    ContextUtil.startActivity(getContext(), MessageActivity.class);
                } else {
                    ContextUtil.startActivity(getContext(), LoginPhoneActivity.class);
                }
                break;
            case R.id.btn_search:
                ContextUtil.startActivity(getContext(), HomeSearchActivity.class);
                break;

        }
    }

    @Override
    public void onRefresh() {
        refresh(mAreaId, mPos);
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
