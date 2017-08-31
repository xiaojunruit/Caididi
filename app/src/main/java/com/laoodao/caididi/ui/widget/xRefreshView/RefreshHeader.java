package com.laoodao.caididi.ui.widget.xRefreshView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.SimpleViewSwithcer;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.laoodao.caididi.R;

import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2017/2/22.
 */

public class RefreshHeader extends LinearLayout implements IHeaderCallBack {
    private Context mContext;
    private RotateAnimation mRotateUpAnim, mRotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 180;

    private View mView;
    private TextView mStatusText;
    private ImageView mArrow;
    private SimpleViewSwithcer mProgress;

    public RefreshHeader(Context context) {
        this(context, null);

    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);


        mView = LayoutInflater.from(mContext).inflate(R.layout.widget_refresh_header, this);
        mStatusText = (TextView) mView.findViewById(R.id.status_text);
        mArrow = (ImageView) mView.findViewById(R.id.arrow);
        mProgress = (SimpleViewSwithcer) mView.findViewById(R.id.progressbar);
        AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(this.getContext());
        progressView.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
        mProgress.setView(progressView);
    }

    @Override
    public void onStateNormal() {
        mStatusText.setText("下拉刷新");
        mArrow.clearAnimation();
        mArrow.setVisibility(VISIBLE);
        mProgress.setVisibility(GONE);
        mArrow.startAnimation(mRotateDownAnim);
    }

    @Override
    public void onStateReady() {
        mStatusText.setText("松开刷新");
        mArrow.clearAnimation();
        mArrow.setVisibility(VISIBLE);
        mProgress.setVisibility(GONE);
        mArrow.startAnimation(mRotateUpAnim);
    }

    @Override
    public void onStateRefreshing() {
        mArrow.clearAnimation();
        mStatusText.setText("正在加载...");
        mArrow.setVisibility(GONE);
        mProgress.setVisibility(VISIBLE);
    }

    @Override
    public void onStateFinish(boolean success) {
        mArrow.setVisibility(GONE);
        mProgress.setVisibility(GONE);
        mStatusText.setText("加载完成");

    }



    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY, int deltaY) {

    }

    @Override
    public void setRefreshTime(long lastRefreshTime) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }
}
