package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XScrollView;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.widget.TagGroup;
import com.laoodao.caididi.databinding.ActivityOtherUserInfoBinding;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.retrofit.user.User;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.my.holder.UserDynamicHolder;
import com.laoodao.caididi.ui.widget.xRefreshView.XRefreshViewHeader;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/1/14.
 */

public class OtherUserInfoActivity extends BaseActivity implements View.OnClickListener, XRefreshView.XRefreshViewListener, XScrollView.OnScrollListener {


    private ActivityOtherUserInfoBinding binding;
    private BaseAdapter adapter = new ItemAdapter<>(UserDynamicHolder.class);
    private int cid;
    private int state;

    public static void start(Context context, int cid) {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        ContextUtil.startActivity(context, OtherUserInfoActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user_info);
        StatusBarUtil.setTransparentForImageView(this, binding.refresh);
        cid = getIntent().getIntExtra("cid", 0);
        binding.setOnClick(this);
        binding.list.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.list.setAdapter(adapter);
        binding.refresh.setCustomHeaderView(new XRefreshViewHeader(this));
        binding.refresh.setHeadMoveLargestDistence(Device.dp2px(90));
        binding.refresh.setXRefreshViewListener(this);
        binding.refresh.setPullLoadEnable(true);
        binding.refresh.setPinnedContent(true);
        binding.refresh.setMoveFootWhenDisablePullLoadMore(false);

        binding.tags.setTagViewFactory((group, position, o) -> {
            TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tag, null, false);
            view.setText(((Plants) o).name);
            return view;
        });
        binding.background.setFocusable(true);
        binding.background.setFocusableInTouchMode(true);
        binding.background.requestFocus();
        initListeners();
        onRefresh();
    }

    private float height;

    private void initListeners() {
        ViewTreeObserver vto = binding.topPanel.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                height = binding.topPanel.getHeight();

                binding.scrollView.setOnScrollListener(OtherUserInfoActivity.this);
            }
        });
    }

    int page = 1;

    private void onPage(int page) {
        API.user().dynamic(cid, page).compose(transform()).subscribe(this::onPageLoaded);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_follow:
                if (state == Const.YES_FOLLOW) {
                    followUserInterface(Const.CANCEL_FOLLOW);
                } else if (state == Const.TOGETHER_FOLLOW) {
                    followUserInterface(Const.CANCEL_FOLLOW);
                } else {
                    followUserInterface(Const.FOLLOW);
                }
                break;
            case R.id.ll_ask:
                MyProblemActivity.start(this, cid);
                break;
            case R.id.ll_follow:
                FollowPeopleActivity.start(this, Const.MY_FOLLOW, cid);
                break;
            case R.id.ll_fans:
                FollowPeopleActivity.start(this, Const.FOLLOW_MY, cid);
                break;
            case R.id.ll_answer:
                MyAnswerActivity.start(this, cid);
                break;
        }
    }

    private void followUserInterface(int type) {
        API.main().followUser(cid, type).compose(transform()).subscribe(result -> {
            Global.info().myAttention = Integer.parseInt(result.data.get("my_attention"));
            Global.info().attentionMe = Integer.parseInt(result.data.get("attention_me"));
            initHeader();
        });
    }

    private void initHeader() {
        API.user().user(cid).compose(transform()).subscribe(result -> {
            binding.setItem(result.data);
            state = result.data.state;
            binding.tags.setList(result.data.plantsList);
            binding.executePendingBindings();

        });
    }


    @Override
    public void onRefresh() {
        initHeader();
        page = 1;
        onPage(page);

    }


    @Override
    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(this, result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.refresh.stopRefresh();
                    } else {
                        binding.refresh.stopLoadMore();
                    }
                });
    }


    public <Item> void onPageLoaded(Page<Item> result) {

        if (page < 2) {
            adapter.addAll(result.data.items, true);
        } else {
            int size = adapter.getItemCount();
            adapter.getList().addAll(result.data.items);
            adapter.notifyItemRangeChanged(size + 2
                    , adapter.getList().size() - 1);
        }
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            binding.refresh.setLoadComplete(true);
            binding.refresh.setPullLoadEnable(false);
        }
        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }


    @Override
    public void onLoadMore(boolean isSilence) {
        page++;
        onPage(page);
    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double offset, int offsetY) {

    }

    @Override
    public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {

    }

    @Override
    public void onScroll(int x, int y, int oldx, int oldy) {
        if (y <= 0) {   //设置标题的背景颜色
            binding.toolbar.setBackgroundColor(Color.argb((int) 0, 51, 51, 51));
            binding.txtTitle.setTextColor(Color.argb((int) 0, 255, 255, 255));
        } else if (y > 0 && y <= height) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
            float scale = y / height;
            float alpha = (255 * scale);
            binding.txtTitle.setTextColor(Color.argb((int) alpha, 255, 255, 255));
            binding.toolbar.setBackgroundColor(Color.argb((int) alpha, 51, 51, 51));
        } else {    //滑动到banner下面设置普通颜色

            binding.toolbar.setBackgroundColor(Color.argb((int) 255, 51, 51, 51));
        }
    }
}
