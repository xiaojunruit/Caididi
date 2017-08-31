package com.laoodao.caididi.ui.wenda.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.AddCategoryAnimator;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityFollowCropBinding;
import com.laoodao.caididi.event.FollowCropEvent;
import com.laoodao.caididi.retrofit.main.Category;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.ui.wenda.adapter.CategoryAdapter;
import com.laoodao.caididi.ui.wenda.adapter.FollowCropAdapter;
import com.laoodao.caididi.ui.wenda.adapter.PlantsAdapter;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.helper.ItemSelectorSingle;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.UI;

/**
 * Created by WORK on 2017/1/9.
 */

public class FollowCropActivity extends BaseActivity {

    private ActivityFollowCropBinding binding;
    private CategoryAdapter categoryAdapter;
    private PlantsAdapter plantsAdapter;
    private PlantsAdapter searchAdapter;
    private ItemSelectorSingle mItemSelector;
    private FollowCropAdapter followCropAdapter;
    public final static int REQUEST_CODE = 1000;
    public final static String REQUEST_OUTPUT = "outputList";
    private int type;
    public final static int ADD_CROP = 1;

    public final static int CHOOSE_CROP = 2;

    public static void start(Context context, List<Plants> selectPlants) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", ADD_CROP);
        bundle.putSerializable("selectPlants", (ArrayList) selectPlants);
        ContextUtil.startActivity(context, FollowCropActivity.class, bundle);
    }

    public static void startForResult(Activity activity, List<Plants> selectPlants) {
        Intent intent = new Intent(activity, FollowCropActivity.class);
        intent.putExtra("selectPlants", (ArrayList) selectPlants);
        intent.putExtra("type", CHOOSE_CROP);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_complete:
                submit();
                return true;
        }
        return false;
    }

    private void submit() {
        if (type == ADD_CROP) {
            if (followCropAdapter.getItemCount() <= 0) {
                UI.showToast(this, "请先选择需要的分类");
                return;
            }
            String ids = "";
            for (Plants plants : plantsAdapter.getSelectedPlanes()) {
                ids += plants.id + ",";
            }
            API.main().followCrop(ids).compose(transform()).subscribe(result -> {
                Global.info().plantsList.clear();
                Global.info().plantsList.addAll(plantsAdapter.getSelectedPlanes());
                Global.info().attentionCrops = Integer.parseInt(result.data.get("attention_crops"));
                finish();
            });
        } else {
            onSelectDone((ArrayList<Plants>) plantsAdapter.getSelectedPlanes());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_crop);

        categoryAdapter = new CategoryAdapter(this);
        plantsAdapter = new PlantsAdapter(this);
        searchAdapter = new PlantsAdapter(this);
        followCropAdapter = new FollowCropAdapter(this);
        binding.listLeft.setAdapter(categoryAdapter);
        binding.listRight.setAdapter(plantsAdapter);
        binding.listFollow.setAdapter(followCropAdapter);
        binding.listSearchCrop.setAdapter(searchAdapter);
        categoryAdapter.setOnItemClickListener((view, object, position) -> {

            Category category = (Category) object;
            plantsAdapter.bindList(category.plants);
//            mItemSelector.select(position);
            binding.category.setText(category.name);
            binding.listRight.scrollToPosition(0);

        });
        plantsAdapter.setOnPlantsSelectChangedListener(selectedPlants -> {
            followCropAdapter.bindList(selectedPlants);
            searchAdapter.bindSelectedList(selectedPlants);
            binding.listFollow.scrollToPosition(followCropAdapter.getItemCount() - 1);
            binding.count.setText(followCropAdapter.getItemCount() + "");
        });
        followCropAdapter.setOnItemClickListener((view, o, position) -> {
            removeCrop(position);
        });
        plantsAdapter.setOnItemClickListener((view, o, position) -> {
            binding.temp.setText(((Plants) o).name);
            View v = new View(this);

            if (followCropAdapter.getItemCount() == 0) {
                v = binding.temp;
            } else {
                v = binding.listFollow.getChildAt(followCropAdapter.getItemCount() - 1);
            }
            new AddCategoryAnimator().with(this, view).setOnAnimatorEndListener(animator -> {
                followCropAdapter.add((Plants) o);
                searchAdapter.check((Plants) o);
                updateFollowCount();

            }).to(v, followCropAdapter.getItemCount()).start();
        });
        searchAdapter.setOnItemClickListener((view, o, position) -> {
            binding.temp.setText(((Plants) o).name);
            View v = new View(this);

            if (followCropAdapter.getItemCount() == 0) {
                v = binding.temp;
            } else {
                v = binding.listFollow.getChildAt(followCropAdapter.getItemCount() - 1);
            }
            new AddCategoryAnimator().with(this, view).setOnAnimatorEndListener(animator -> {
                followCropAdapter.add((Plants) o);
                plantsAdapter.check((Plants) o);
                updateFollowCount();

            }).to(v, followCropAdapter.getItemCount()).start();
        });
        binding.edittxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.listSearchCrop.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                binding.llCrop.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
                search(s.toString());
            }
        });
        //按下变色悬着器
//        mItemSelector = new ItemSelectorSingle(binding.listLeft);
//        mItemSelector.setIsEnable(true);
        List<Plants> selected = (List<Plants>) getIntent().getSerializableExtra("selectPlants");
        type = getIntent().getIntExtra("type", 0);
        if (selected != null) {
            plantsAdapter.bindSelectedList(selected);
        }
        initData();
    }

    /**
     * 添加作物
     */
    private void addCrop(Plants plants) {


        updateFollowCount();
    }

    /**
     * 移除作物
     */

    private void removeCrop(int position) {
        plantsAdapter.uncheck(position);
        followCropAdapter.remove(position);
        searchAdapter.uncheck(position);
        updateFollowCount();
    }


    /**
     * 更新关注数量
     */
    private void updateFollowCount() {
        binding.listFollow.scrollToPosition(followCropAdapter.getItemCount() - 1);
        binding.count.setText(followCropAdapter.getItemCount() + "");
    }

    /**
     * 搜索
     *
     * @param keyword
     */
    private void search(String keyword) {
        API.main().kindList(keyword).compose(transform()).subscribe(result -> {
            searchAdapter.bindList(result.data);
            searchAdapter.bindSelectedList(plantsAdapter.getSelectedPlanes());
        });
    }

    /**
     * 初始化分类
     */
    private void initData() {
        API.main().followCropList2().compose(transform()).subscribe(result -> {
            categoryAdapter.bindList(result.data);
            //默认选中第一个
            binding.category.setText(result.data.get(0).name);
            plantsAdapter.bindList(result.data.get(0).plants);
//            binding.getRoot().postDelayed(() -> {
//                mItemSelector.select(0);
//            }, 1);
        });
    }

    public void onResult(ArrayList<Plants> plantses) {
        setResult(RESULT_OK, new Intent().putExtra(REQUEST_OUTPUT, plantses));
        finish();
    }

    public void onSelectDone(ArrayList<Plants> plantses) {
        onResult(plantses);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.post(new FollowCropEvent());
    }
}
