package com.laoodao.caididi.ui.main.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FragmentBuyBinding;
import com.laoodao.caididi.event.BuyEvent;
import com.laoodao.caididi.ui.main.activity.VegetablesPriceActivity;
import com.laoodao.caididi.ui.my.dialog.AbortTimeDialog;
import com.laoodao.caididi.ui.my.dialog.CategoryDialog;
import com.laoodao.caididi.ui.my.dialog.DropdownAreaDialog;
import com.laoodao.caididi.ui.my.holder.BuyListHolder;
import com.laoodao.caididi.ui.qiugou.activity.BuySearchActivity;
import com.laoodao.caididi.ui.widget.wheelPicker.DatePicker;

import java.util.Calendar;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/2/10.
 */

public class BuyFragment extends BindingFragment<FragmentBuyBinding> implements View.OnClickListener, XRecyclerView.LoadingListener {

    private DropdownAreaDialog mAreaDialog;
    private CategoryDialog mCategoryDialog;
    private AbortTimeDialog mAbortTimeDialog;
    private int mProIndex;
    private int mCityIndex;
    private int mCountyIndex;
    private ItemAdapter mAdapter = new ItemAdapter(BuyListHolder.class);
    private String operateTime;
    private int year;
    private int month;
    private int date;
    private String areaId = "0";
    private String expireTimeId = "1";
    private String expireTime = "";
    private String category = "";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_buy;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, binding.flTitle);
        binding.setOnClick(this);
        binding.list.setAdapter(mAdapter);
        binding.list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                if (itemPosition != 0) {
                    outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.gap_2x));
                }
            }
        });
        binding.list.setLoadingListener(this);
        nowDate();
        onRefresh();
        RxBus.ofType(BuyEvent.class).compose(bindToLifecycle()).subscribe(buyEvent -> {
            category = buyEvent.keyword;
            binding.txtCategory.setText(category);
            onRefresh();
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_ask:
                ContextUtil.startActivity(getContext(), VegetablesPriceActivity.class);
                break;
            case R.id.btn_search:
                ContextUtil.startActivity(getContext(), BuySearchActivity.class);
                break;
            case R.id.btn_province:
                binding.btnProvince.setSelected(true);
                mAreaDialog = new DropdownAreaDialog(getContext(), binding.toolMenu, (proIndex, cityIndex, countyIndex, area) -> {
                    mProIndex = proIndex;
                    mCityIndex = cityIndex;
                    mCountyIndex = countyIndex;
                    binding.txtArea.setText(area.n);
                    areaId = area.i;
                    onRefresh();
                });
                mAreaDialog.setOnDismissListener(dialog -> binding.btnProvince.setSelected(false));
                mAreaDialog.show(mProIndex, mCityIndex, mCountyIndex);
                break;
            case R.id.btn_category:
                binding.btnCategory.setSelected(true);
                mCategoryDialog = new CategoryDialog(getContext(), binding.toolMenu, plants -> {
                    binding.txtCategory.setText(plants.name);
                    if (plants.id == CategoryDialog.TYPE_ALL) {
                        category = "";
                    } else {
                        category = plants.name;
                    }
                    onRefresh();
                });
                mCategoryDialog.setOnDismissListener(dialog -> binding.btnCategory.setSelected(false));
                mCategoryDialog.show();

                break;
            case R.id.btn_time:
                binding.btnTime.setSelected(true);
                mAbortTimeDialog = new AbortTimeDialog(getContext(), binding.toolMenu, (at) -> {
                    if (at.id == AbortTimeDialog.TYPE_DIY) {
                        onYearMonthDayPicker();
                        return;
                    }
                    binding.txtTime.setText(at.name);
                    expireTimeId = String.valueOf(at.id);
                    expireTime = "";
                    onRefresh();
                });
                mAbortTimeDialog.setOnDismissListener(dialog -> binding.btnTime.setSelected(false));
                mAbortTimeDialog.show();
                break;
        }
    }


    public void onYearMonthDayPicker() {
        final DatePicker picker = new DatePicker(getActivity());
        picker.layoutCorners();
        picker.setCancelText("×");
        picker.setCancelTextSize(25);
        picker.setLineVisible(false);
        picker.setOffset(1);
        picker.setBackgroundColor(Color.TRANSPARENT);
        picker.setTopBackgroundColor(Color.TRANSPARENT);
        picker.setGravity(Gravity.CENTER);
        picker.setTopPadding(18);//两边边距
        picker.setRangeStart(1990, 1, 1);//开始时间
        picker.setRangeEnd(2111, 12, 12);//结束时间
        picker.setSelectedItem(year, month, date);//打开后当前的时间
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                operateTime = year + "-" + month + "-" + day;
                binding.txtTime.setText(operateTime);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText("选择日期：" + year + "年" + picker.getSelectedMonth() + "月" + picker.getSelectedDay() + "日");
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText("选择日期：" + picker.getSelectedYear() + "年" + month + "月" + picker.getSelectedDay() + "日");
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText("选择日期：" + picker.getSelectedYear() + "年" + picker.getSelectedMonth() + "月" + day + "日");
            }
        });
        picker.show();
    }

    public void nowDate() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        date = c.get(Calendar.DATE);
    }


    protected int cursor = 0;
    int page = 1;

    public <Item> void onPageLoaded(Page<Item> result) {

        mAdapter.addAll(result.data.items, page < 2);
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            binding.list.noMoreLoading();
            binding.list.loadMoreComplete();
        }
        if (result.data.total != 0) {
            binding.loading.showContent();
        } else {
            binding.loading.showEmpty();
        }
        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    binding.loading.showError();
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(getContext(), result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.list.refreshComplete();
                    } else {
                        binding.list.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        binding.list.setIsnomore(false);
        page = 1;
        cursor = 0;
        onPage(page);
    }

    @Override
    public void onLoadMore() {
        page++;
        onPage(page);
    }

    private void onPage(int page) {
        API.user().buyListInfo(page, areaId, category, expireTime, expireTimeId, null).compose(transform()).subscribe(this::onPageLoaded);
    }


}
