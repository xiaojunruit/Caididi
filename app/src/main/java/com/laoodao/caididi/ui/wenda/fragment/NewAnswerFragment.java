package com.laoodao.caididi.ui.wenda.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaeger.library.StatusBarUtil;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseFragment;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.AskEvent;
import com.laoodao.caididi.event.NewestInfoEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.retrofit.main.NewestInfo;
import com.laoodao.caididi.ui.wenda.holder.AnswerHolder;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/7.
 */

public class NewAnswerFragment extends ListFragment {

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(AnswerHolder.class);
    }

    @Override
    protected void onBodyCreated() {
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
        RxBus.ofType(AskEvent.class).compose(bindToLifecycle()).subscribe(event ->{
            onRefresh();
        });
    }
    @Override
    protected void onPage(int page) {
        API.main().newAnswerList(page, "new","","","").compose(transform()).subscribe(this::onPageLoaded);
    }
}
