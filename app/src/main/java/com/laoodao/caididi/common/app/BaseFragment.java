package com.laoodao.caididi.common.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Response;
import com.tencent.stat.StatService;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;

public class BaseFragment extends RxFragment {

    public BaseFragment() {
    }

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return new API.Transformer<T>(this).check();
    }

    public <T extends Response> Observable.Transformer<T, T> onErrorTransform(API.OnErrorListener errorListener) {
        return new API.Transformer<T>(this).check().error(errorListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.trackBeginPage(getActivity(), this.getClass().getName());

    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.trackEndPage(getActivity(), this.getClass().getName());
    }
}
