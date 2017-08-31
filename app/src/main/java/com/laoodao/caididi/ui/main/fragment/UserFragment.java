package com.laoodao.caididi.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FragmentUserBinding;
import com.laoodao.caididi.event.LoginInfoChangedEvent;
import com.laoodao.caididi.ui.dialog.FollowCropsDialog;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.AddOperationActivity;
import com.laoodao.caididi.ui.my.activity.ContactsListActivity;
import com.laoodao.caididi.ui.my.activity.MessageActivity;
import com.laoodao.caididi.ui.my.activity.MyProblemActivity;
import com.laoodao.caididi.ui.my.activity.UserInfoActivity;
import com.laoodao.caididi.ui.my.holder.DynamicMenuHolder;
import com.laoodao.caididi.ui.weather.activity.WeatherActivity;
import com.laoodao.caididi.ui.widget.SupportGridItemDecoration;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;


public class UserFragment extends BindingFragment<FragmentUserBinding> implements View.OnClickListener {

    private boolean isFirst;
    private BaseAdapter adapter = new ItemAdapter<>(DynamicMenuHolder.class);

    @Override
    public void onClick(View v) {
        if (!Global.session().isLoggedIn()) {
            ContextUtil.startActivity(getContext(), LoginPhoneActivity.class);
            return;
        }
        switch (v.getId()) {
            case R.id.avatar:
                if (Global.session().isLoggedIn()) {
                    ContextUtil.startActivity(getContext(), UserInfoActivity.class);
                } else {
                    ContextUtil.startActivity(getActivity(), LoginPhoneActivity.class);
                }
                break;
            case R.id.ll_problem:
                ContextUtil.startActivity(getContext(), MyProblemActivity.class);
                break;
            case R.id.msg:
                ContextUtil.startActivity(getActivity(), MessageActivity.class);
                break;
            case R.id.ll_supply:
                ContextUtil.startActivity(getActivity(), WeatherActivity.class);
                break;
            case R.id.ll_ask_buy:

                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setOnClick(this);
        binding.executePendingBindings();
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, binding.content);
        binding.list.setHasFixedSize(true);
        binding.list.addItemDecoration(new SupportGridItemDecoration(getContext()));
        binding.list.setLayoutManager(new GridLayoutManager(getContext(), 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.list.setAdapter(adapter);

        refresh();
        RxBus.ofType(LoginInfoChangedEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            isFirst = true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        binding.setIsLoggedIn(Global.session().isLoggedIn());
        if (Global.session().isLoggedIn()) {
            API.user().loginInfo().compose(transform()).subscribe(result -> {
                Global.session().update(result.data);
                binding.setIsLoggedIn(true);
                binding.setLogin(result.data);
                initUnReadDot(result.data.isHasMsg);
                if (Global.info().attentionCrops == 0 && isFirst) {
                    isFirst = false;
                    FollowCropsDialog.FollowDialogCustomAttr(getActivity());
                }
            });
        }
        API.user().dynamicMenu().compose(transform()).subscribe(listResult -> {
            adapter.addAll(listResult.data,true);
        });
        initUnReadDot(Global.info().isHasMsg);

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

}
