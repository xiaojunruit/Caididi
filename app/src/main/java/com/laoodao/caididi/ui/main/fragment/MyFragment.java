package com.laoodao.caididi.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.andview.refreshview.XRefreshView;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FragmentMyBinding;
import com.laoodao.caididi.event.HuanXinCountEvent;
import com.laoodao.caididi.event.LoginInfoChangedEvent;
import com.laoodao.caididi.event.RedDotEvent;
import com.laoodao.caididi.retrofit.user.DynamicMenu;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.ui.dialog.FollowCropsDialog;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.main.user.RegisterActivity;
import com.laoodao.caididi.ui.my.activity.ContactsListActivity;
import com.laoodao.caididi.ui.my.activity.MessageActivity;
import com.laoodao.caididi.ui.my.activity.MyQrcodeActivity;
import com.laoodao.caididi.ui.my.activity.SettingActivity;
import com.laoodao.caididi.ui.my.activity.UserInfoActivity;
import com.laoodao.caididi.ui.my.holder.DynamicMenuHolder;
import com.laoodao.caididi.ui.my.holder.EntryDividerHolder;
import com.laoodao.caididi.ui.widget.xRefreshView.RefreshHeader;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.itemholder.adapter.ItemTypedAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by Administrator on 2017/2/20.
 */

public class MyFragment extends BindingFragment<FragmentMyBinding> implements View.OnClickListener, XRefreshView.XRefreshViewListener {
    private boolean isFirst;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my;
    }

    private BaseAdapter mAdapter = new ItemTypedAdapter(new Class[]{DynamicMenuHolder.class, EntryDividerHolder.class}) {
        @Override
        public int getItemViewType(int position) {
            DynamicMenu item = (DynamicMenu) getItem(position);
            if ("divider".equals(item.url)) {
                return R.layout.item_entry_divider;
            }
            return R.layout.item_dynamic_menu;
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.list.setHasFixedSize(true);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.setOnClick(this);
        binding.list.setAdapter(mAdapter);
        RxBus.ofType(LoginInfoChangedEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            isFirst = true;
            if (Global.session().isLoggedIn()) {
                loginHuanXin();
            }
        });

        binding.refresh.setXRefreshViewListener(this);
        binding.refresh.setCustomHeaderView(new RefreshHeader(getActivity()));
        binding.refresh.setMoveForHorizontal(true);
        binding.refresh.setMoveFootWhenDisablePullLoadMore(false);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() -> refresh());
    }


    public void loginHuanXin() {
        if (!EMClient.getInstance().isLoggedInBefore()) {
            LoginInfo info = Global.info();
            if (!TextUtils.isEmpty(info.emcode)&&!TextUtils.isEmpty(info.username)) {
                EMClient.getInstance().login(info.username, info.emcode, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.e("main", "登录聊天服务器成功！");
                        RxBus.post(new HuanXinCountEvent());
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.e("main", "登录聊天服务器失败！code=" + code + "----" + message);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_msg:
                ContextUtil.startActivity(getActivity(), isLogin() ? MessageActivity.class : LoginPhoneActivity.class);
                break;
            case R.id.avatar:
                ContextUtil.startActivity(getActivity(), isLogin() ? UserInfoActivity.class : LoginPhoneActivity.class);
//                ContextUtil.startActivity(getActivity(), isLogin() ? ContactsListActivity.class : LoginPhoneActivity.class);
                break;
            case R.id.btn_qrcode:
                if (Global.session().isLoggedIn()) {
                    ContextUtil.startActivity(getActivity(), MyQrcodeActivity.class);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        if (EMClient.getInstance().isLoggedInBefore()){
            RxBus.post(new HuanXinCountEvent());
        }
    }

    private void refresh() {

        if (isLogin()) {
            API.user().loginInfo().compose(transform()).subscribe(result -> {
                Global.session().update(result.data);
                binding.setIsLoggedIn(true);
                binding.setItem(result.data);
                RxBus.post(new RedDotEvent(null, result.data.isHasMsg));
                initUnReadDot(result.data.isHasMsg);
                if (Global.info().attentionCrops == 0 && isFirst) {
                    isFirst = false;
                    FollowCropsDialog.FollowDialogCustomAttr(getActivity());
                }
            });
        } else {
            binding.setItem(new LoginInfo("未登录"));
        }

        API.user().
                dynamicMenu()
                .compose(onErrorTransform((context, throwable) -> {
                    binding.loading.showError();
                    binding.refresh.stopRefresh();
                }))
                .subscribe(result -> {
                    binding.loading.showContent();
                    binding.refresh.stopRefresh();
                    mAdapter.addAll(result.data, true);
                });

        initUnReadDot(Global.info().isHasMsg);
    }

    public boolean isLogin() {
        return Global.session().isLoggedIn();
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
