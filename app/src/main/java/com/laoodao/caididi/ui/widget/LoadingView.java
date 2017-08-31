package com.laoodao.caididi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;

import java.util.HashMap;
import java.util.Map;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.widget.layout.LoadingLayout;


public class LoadingView extends RelativeLayout {

    private static final int NULL_RESOURCE_ID = -1;

    private int mEmptyViewResId;
    private int mErrorViewResId;
    private int mLoadingViewResId;
    private int mNoNetworkViewResId;
    private int mContentViewResId;
    private int mCustomId;
    Map<Integer, ViewGroup> mLayouts = new HashMap<>();

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0);

        mEmptyViewResId =
                a.getResourceId(R.styleable.LoadingView_emptyView, R.layout.widget_empty_view);
        mErrorViewResId =
                a.getResourceId(R.styleable.LoadingView_errorView, R.layout.widget_error_view);
        mLoadingViewResId =
                a.getResourceId(R.styleable.LoadingView_loadingView, R.layout.widget_loading_view);
        mNoNetworkViewResId = a.getResourceId(R.styleable.LoadingView_noNetworkView,
                R.layout.widget_no_network_view);
        mContentViewResId = a.getResourceId(R.styleable.LoadingView_contentView,
                NULL_RESOURCE_ID);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLayouts.put(mContentViewResId, (ViewGroup) this.getChildAt(0));
        showContent();
    }


    /**
     * 显示空视图
     */
    public LoadingView showEmpty() {

        show(mEmptyViewResId);

        return this;
    }

    /**
     * 显示错误视图
     */
    public LoadingView showError() {
        show(mErrorViewResId);

        return this;
    }

    public LoadingView showLoading() {
        show(mLoadingViewResId);
        return this;
    }


    private void show(int layoutId) {
        for (ViewGroup view : mLayouts.values()) {
            view.setVisibility(GONE);
        }
        layout(layoutId).setVisibility(VISIBLE);
    }

    public LoadingView customLayout(int id) {
        remove(this.mCustomId);
        this.mCustomId = id;
        return this;
    }

    public LoadingView customText(String value) {
        TextView view = (TextView) layout(this.mCustomId).findViewById(R.id.empty_text);
        if (view != null) {
            view.setText(value);
        }
        return this;
    }

    public View getEmptyView() {
        return layout(mEmptyViewResId);
    }

    public LoadingView customBtnText(String value) {
        TextView btnTxt = (TextView) layout(this.mCustomId).findViewById(R.id.empty_button);
        if (btnTxt != null) {
            btnTxt.setText(value);
        }
        return this;
    }

    private void remove(int layoutId) {
        if (this.mLayouts.containsKey(Integer.valueOf(layoutId))) {
            ViewGroup vg = (ViewGroup) this.mLayouts.remove(Integer.valueOf(layoutId));
            this.removeView(vg);
        }

    }

    private ViewGroup layout(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            return mLayouts.get(layoutId);
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutId, this, false);
        layout.setVisibility(GONE);
        addView(layout);
        mLayouts.put(layoutId, layout);
        if (layoutId == mErrorViewResId) {
            if (mListener != null) {
                layout.setOnClickListener(v -> {
                    showLoading();
                    mListener.onRetry();
                });
            }
        }
        if (layoutId == mCustomId) {
            if (mCustomListener != null) {
                layout.setOnClickListener(v -> {
                    mCustomListener.onCustom();
                });
            }
        }
        return layout;
    }

    private OnRetryListener mListener;
    private OnCustomListener mCustomListener;

    public void setOnRetryListener(OnRetryListener listener) {
        this.mListener = listener;
    }

    public void setOnCustomListener(OnCustomListener listener) {
        this.mCustomListener = listener;
    }

    public interface OnRetryListener {
        void onRetry();
    }

    public interface OnCustomListener {
        void onCustom();
    }


    /**
     * 显示无网络视图
     */
    public LoadingView showNoNetwork() {
        show(mNoNetworkViewResId);

        return this;
    }

    /**
     * 显示内容视图
     */
    public LoadingView showContent() {
        show(mContentViewResId);
        return this;
    }

    public LoadingView showCustom() {
        show(mCustomId);
        return this;
    }

}
