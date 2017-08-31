package com.laoodao.caididi.ui.nongye.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.andview.refreshview.XRefreshView;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityFarmingDetailBinding;
import com.laoodao.caididi.retrofit.main.ShareInfo;
import com.laoodao.caididi.ui.nongye.holder.FarmingHolder;
import com.laoodao.caididi.ui.widget.xRefreshView.RefreshHeader;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.zzhoujay.richtext.RichText;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/2/18.
 */

public class FarmingDetailActivity extends BaseActivity implements View.OnClickListener, XRefreshView.XRefreshViewListener {
    private ActivityFarmingDetailBinding mBinding;
    private int id;
    private ShareInfo info;
    private BaseAdapter adapter = new ItemAdapter<>(FarmingHolder.class);
    private String title;

    public static void start(Context context, int id, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        ContextUtil.startActivity(context, FarmingDetailActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_farming_detail);
        id = getIntent().getIntExtra("id", 0);
        title = getIntent().getStringExtra("title");
        initData();
        refresh();
    }

    private void initData() {
        setTitle(title);
        mBinding.loading.showLoading();
        mBinding.loading.setOnRetryListener(() -> refresh());
        mBinding.setListener(this);
        mBinding.list.setHasFixedSize(true);
        mBinding.list.setNestedScrollingEnabled(false);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mBinding.list.setAdapter(adapter);
        mBinding.refresh.setXRefreshViewListener(this);
        mBinding.refresh.setCustomHeaderView(new RefreshHeader(this));
        mBinding.refresh.setMoveForHorizontal(true);
        mBinding.refresh.setMoveFootWhenDisablePullLoadMore(false);
    }

    private void refresh() {
        mBinding.txtExpand.setEtvPrompt("阅读全文");
        mBinding.txtExpand.setExpand(false);
        API.main()
                .ndetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .filter(result -> API.doCheck(this, result))
                .onErrorResumeNext(throwable -> {
                    mBinding.refresh.stopRefresh();
                    mBinding.loading.showError();
                    return Observable.empty();
                })
                .doAfterTerminate(() -> {
                    mBinding.refresh.stopRefresh();
                })
                .subscribe(result -> {
                    mBinding.loading.showContent();
                    mBinding.refresh.stopRefresh();
                    RichText.fromHtml(result.data.content).into(mBinding.txtExpand.getTextContent());
                    mBinding.setItem(result.data);
                    adapter.addAll(result.data.tj, true);
                    mBinding.txtExpand.postText();
                    info = result.data.shareInfo;
                    mBinding.executePendingBindings();
                });

    }

    WechatSdk mWechatSdk;
    QQSdk mQQSdk;

    void onShare(int id) {
        if (info == null) {
            return;
        }
        switch (id) {
            case R.id.btn_qq:
            case R.id.btn_qzone:
                mQQSdk = new QQSdk(this, BuildConfig.APP_ID_QQ);
                mQQSdk.shareToQQ(info.title, info.content, info.img, info.url, id == R.id.btn_qq);
                break;
            case R.id.btn_friend:
            case R.id.btn_wx:
                int scene = id == R.id.btn_friend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                mWechatSdk = new WechatSdk(this, BuildConfig.APP_ID_WECHAT);
                mWechatSdk.share(info.title, info.content, info.img, info.url, scene);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_qq:
            case R.id.btn_qzone:
            case R.id.btn_friend:
            case R.id.btn_wx:
                onShare(v.getId());
                break;

        }
    }

    @Override
    public void onRefresh() {
        refresh();
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
