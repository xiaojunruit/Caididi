package com.laoodao.caididi.ui.wenda.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.AskEvent;
import com.laoodao.caididi.event.FollowCropEvent;
import com.laoodao.caididi.event.LoginInfoChangedEvent;
import com.laoodao.caididi.event.NewestInfoEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.retrofit.main.NewestInfo;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.wenda.activity.FollowCropActivity;
import com.laoodao.caididi.ui.wenda.holder.AnswerHolder;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2016/12/14.
 */

public class CropsAnswerFragment extends ListFragment {
    private String emptyText;
    private String emptyButton;

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(AnswerHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        binding.tvPositioning.setText("切换作物");
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
        RxBus.ofType(FollowCropEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            onRefresh();
        });

    }

    private void listener() {
        binding.tvPositioning.setOnClickListener(v -> {
            FollowCropActivity.start(getContext(), (ArrayList) Global.info().plantsList);
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
        binding.loading.setOnCustomListener(() -> {
            if (!Global.session().isLoggedIn()) {
                ContextUtil.startActivity(getContext(), LoginPhoneActivity.class);
                return;
            }
            FollowCropActivity.start(getActivity(), null);
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


    private void isLoginOrFocus() {
        if (Global.info().attentionCrops == 0) {
            emptyText = "您还没有关注作物\n请先关注作物 关注后将展示这些作物的问题";
            emptyButton = "关注作物";
            isPositioning(true);
        }
        if (!Global.session().isLoggedIn()) {
            emptyText = "您当前未登录\n请先登录 登陆后将展示您所关注的作物问题";
            emptyButton = "登录";
        }
        if (!Global.session().isLoggedIn() || Global.info().attentionCrops == 0) {
            binding.loading.customLayout(R.layout.widget_no_choose).customText(emptyText).customBtnText(emptyButton).showCustom();
        }
    }

    @Override
    protected void onPage(int page) {
        API.main().newAnswerList(page, "crops", "", "", "").compose(transform()).subscribe(result -> {
            onPageLoaded(result);
            isLoginOrFocus();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Global.session().isLoggedIn()) {
            binding.tvPositioning.setVisibility(View.GONE);
        }
    }
}
