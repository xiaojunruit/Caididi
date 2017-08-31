package com.laoodao.caididi.ui.main.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FragmentAnswerBinding;
import com.laoodao.caididi.event.ProvinceSelectEvent;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.wenda.activity.MyAskActivity;
import com.laoodao.caididi.ui.wenda.activity.SearchActivity;
import com.laoodao.caididi.ui.wenda.fragment.CropsAnswerFragment;
import com.laoodao.caididi.ui.wenda.fragment.NearAnswerFragment;
import com.laoodao.caididi.ui.wenda.fragment.NewAnswerFragment;
import com.laoodao.caididi.ui.wenda.fragment.UserAnswerFragment;
import com.laoodao.caididi.ui.widget.citySelector.db.DatabaseHelper;
import com.laoodao.caididi.utils.PermissionUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.widget.adapter.TabsAdapter;

/**
 * Created by Administrator on 2016/12/7.
 */

public class AnswerFragment extends BindingFragment<FragmentAnswerBinding> implements View.OnClickListener {
    private String local;
    private final Fragment mFragments[] = {new NewAnswerFragment(), new CropsAnswerFragment(), new UserAnswerFragment(), new NearAnswerFragment()};
    private String[] mTitles = new String[]{"最新", "作物", "用户", "附近"};

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setListener(this);
//        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, binding.llTitle);
        getProvince();
        if (!TextUtils.isEmpty(local)) {
            mTitles[3] = local;
        }
        binding.viewpager.setAdapter(new TabsAdapter(getChildFragmentManager(), mFragments));
        binding.tabs.setViewPager(binding.viewpager, mTitles);
        RxBus.ofType(ProvinceSelectEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            getProvince();
            mTitles[3] = local;
            binding.tabs.setViewPager(binding.viewpager, mTitles);
            binding.tabs.setCurrentTab(3);
        });
    }

    private void getProvince() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        List<City> cityList = dbHelper.getHistoryCity(City.LEVEL_PROVINCE);
        if (cityList != null && cityList.size() > 0) {
            local = cityList.get(0).n;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_ask:
                if (Global.session().isLoggedIn()) {
                    ContextUtil.startActivity(getActivity(), MyAskActivity.class);
                }else {
                    PermissionUtil.externalStorage(new PermissionUtil.RequestPermissionListener() {
                        @Override
                        public void success() {
                            ContextUtil.startActivity(getActivity(), LoginPhoneActivity.class);
                        }

                        @Override
                        public void failure() {
                            Toast.makeText(getContext(), "\"请求权限失败,请前往设置开启权限\"", Toast.LENGTH_SHORT).show();
                        }
                    }, new RxPermissions(getActivity()));

                }
                break;
            case R.id.btn_search:
                ContextUtil.startActivity(getActivity(), SearchActivity.class);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_answer;
    }
}
