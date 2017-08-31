package com.laoodao.caididi.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.databinding.FragmentFarmingBinding;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.nongye.activity.FarmingSearchActivity;
import com.laoodao.caididi.ui.nongye.fragment.TabFarmingListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import ezy.widget.adapter.TabsAdapter;

/**
 * Created by WORK on 2017/2/17.
 */

public class FarmingFragment extends BindingFragment<FragmentFarmingBinding> implements View.OnClickListener {

    private String[] mTitle;
    private Fragment fragments[];
    private Map<String, String> page;
    private int pageId;
    private String labelId;
    private List<Plants> plantsList = new ArrayList<>();
    private boolean isInsect;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_farming;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        slidingPage();
        refresh(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            labelId = getActivity().getIntent().getStringExtra("id");
            isInsect = getActivity().getIntent().getBooleanExtra("isInsect", false);
            if (TextUtils.isEmpty(labelId)) {
                return;
            }
            binding.txtTitle.setText(getActivity().getIntent().getStringExtra("title"));
            homeClickMenu();

        }
    }

    private void slidingPage() {
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (plantsList==null){
                    return;
                }
                if (plantsList.get(position).isInsect) {
                    labelId = "0";
                    binding.txtTitle.setText(plantsList.get(position).name);
                    refresh(true);
                } else if (!isInsect) {
                    binding.txtTitle.setText(plantsList.get(position).name);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private void homeClickMenu() {
        if (binding.viewpager.getAdapter()==null){
            UI.showToast(getContext(),"跳转界面为获取到数据，请手动刷新");
            return;
        }
        int count = binding.viewpager.getAdapter().getCount();
        if (count < 0) {
            return;
        }
        refresh(isInsect);
    }


    //病虫害库菜单接口
    private void pestMenu() {
        getActivity().getIntent().removeExtra("id");
        API.main().insecttype()
                .compose(onErrorTransform((context, throwable) -> {
                    binding.loading.showError();
                }))
                .subscribe(result -> {
                    resultSuccess(result.data);
                    plantsList=null;
                });
    }

    //普通菜单接口
    private void menu() {
        API.main().newstype()
                .compose(onErrorTransform((context, throwable) -> {
                    binding.loading.showError();
                }))
                .subscribe(result -> {
                    plantsList = result.data;
                    resultSuccess(result.data);
                });
    }


    private void refresh(boolean isInsect) {
        binding.btnBack.setVisibility(isInsect ? View.VISIBLE : View.GONE);
        this.isInsect = isInsect;
        if (isInsect) {
            pestMenu();
        } else {
            menu();
        }
    }


    private void resultSuccess(List<Plants> result) {
        binding.loading.showContent();
        mTitle = new String[result.size()];
        fragments = new Fragment[result.size()];
        page = new HashMap<String, String>();
        for (int i = 0; i < result.size(); i++) {
            Plants plants = result.get(i);
            mTitle[i] = plants.name;
            fragments[i] = new TabFarmingListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("gcId", plants.id);
            fragments[i].setArguments(bundle);
            page.put(String.valueOf(plants.id), String.valueOf(i));
            if (i == 0&&!isInsect) {
                binding.txtTitle.setText(plants.name);
            }
        }
        binding.viewpager.setAdapter(new TabsAdapter(getChildFragmentManager(), fragments));
        binding.tabs.setViewPager(binding.viewpager, mTitle);
        if (page.get(labelId) == null) {
            pageId = 0;
        } else {
            pageId = Integer.parseInt(page.get(labelId));
        }
        if (fragments.length > 0) {
            binding.tabs.setCurrentTab(1);
        }
        binding.tabs.setCurrentTab(pageId);
        pageId = 0;
    }


    private void initData() {
        binding.setListener(this);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() -> refresh(false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                ContextUtil.startActivity(getContext(), FarmingSearchActivity.class);
                break;
            case R.id.btn_back:
                labelId = "0";
                refresh(false);
                break;
        }
    }
}
