package com.laoodao.caididi.ui.wenda.fragment;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.AskEvent;
import com.laoodao.caididi.event.LoginInfoChangedEvent;
import com.laoodao.caididi.event.NewestInfoEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.retrofit.main.NewestInfo;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.wenda.activity.FollowUserActivity;
import com.laoodao.caididi.ui.wenda.holder.AnswerHolder;

import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2016/12/14.
 */

public class UserAnswerFragment extends ListFragment {
    private String emptyText;
    private String emptyButton;

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(AnswerHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        binding.tvPositioning.setText("关注用户");
        isPositioning(true);
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
        RxBus.ofType(LoginInfoChangedEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            onRefresh();
        });
//        RxBus.ofType(FollowPeopleEvent.class).compose(bindToLifecycle()).subscribe(event -> {
//            onRefresh();
//        });
    }


    private void listener(){
        binding.tvPositioning.setOnClickListener(v -> {
            ContextUtil.startActivity(getContext(), FollowUserActivity.class);
        });
        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!Global.session().isLoggedIn() || binding.list.getChildAt(0).getHeight() != 0) {
                    isPositioning(false);
                } else {
                    isPositioning(true);
                }
            }
        });

        binding.loading.setOnCustomListener(() ->{
            if (!Global.session().isLoggedIn()) {
                ContextUtil.startActivity(getContext(), LoginPhoneActivity.class);
                return;
            }
            ContextUtil.startActivity(getContext(), FollowUserActivity.class);
        });
    }

    /**
     * 判断tvPositioning隐藏的方法
     *
     * @param isVisibility 是否隐藏
     */
    private void isPositioning(boolean isVisibility) {
        binding.tvPositioning.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPage(int page) {

        API.main().newAnswerList(page, "user", "", "", "").compose(transform()).subscribe(result -> {
            onPageLoaded(result);
            isLoginOrFocus();
        });
    }

    private void isLoginOrFocus() {
        if (Global.info().myAttention < 1) {
            emptyButton = "关注好友";
            emptyText = "您还没有关注好友\n请先关注一些好友 关注后将展示他们参与的问题";
            isPositioning(true);
        }
        if (!Global.session().isLoggedIn()) {
            emptyButton = "登录";
            emptyText = "您当前未登录\n请先登录 登录后将展示您所关注好友参与的问题";
        }
        if (Global.info().myAttention < 1 || !Global.session().isLoggedIn()) {
            binding.loading.customLayout(R.layout.widget_no_choose).customText(emptyText).customBtnText(emptyButton).showCustom();
            return;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
        if (!Global.session().isLoggedIn()) {
            isPositioning(false);
        }
    }

}
