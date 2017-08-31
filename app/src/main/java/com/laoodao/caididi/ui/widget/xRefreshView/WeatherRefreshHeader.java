package com.laoodao.caididi.ui.widget.xRefreshView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.laoodao.caididi.R;

import ezy.lite.util.LogUtil;

/**
 * Created by Administrator on 2017/2/20.
 */

public class WeatherRefreshHeader extends LinearLayout implements IHeaderCallBack {
    private Context mContext;
    private View mView;
    private ImageView mLoadImage;
    private TextView mLoadText;

    public WeatherRefreshHeader(Context context) {
        this(context, null);
    }

    public WeatherRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

//    private final Animation mRotateAnimation;

    private ObjectAnimator mRouteAnim;

    public WeatherRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

       /* mRotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(400);
        mRotateAnimation.setRepeatCount(Integer.MAX_VALUE);
        mRotateAnimation.setFillAfter(true);
*/
        initView();

    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_weather_header, this);
        mLoadImage = (ImageView) mView.findViewById(R.id.loading);
        mLoadText = (TextView) mView.findViewById(R.id.loading_text);
        mRouteAnim = ObjectAnimator.ofFloat(mLoadImage, "rotation", 0f, 359f);
        mRouteAnim.setDuration(1000);
        mRouteAnim.setInterpolator(new LinearInterpolator());
        mRouteAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        mRouteAnim.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    public void onStateNormal() {
        mLoadText.setText("下拉更新");

    }

    @Override
    public void onStateReady() {
        mLoadText.setText("松手可更新");
        if (!mRouteAnim.isRunning()) {
            mRouteAnim.start();
        }
    }

    @Override
    public void onStateRefreshing() {

        mLoadText.setText("正在更新...");
    }

    @Override
    public void onStateFinish(boolean success) {
        if (mRouteAnim.isRunning()) {
            mRouteAnim.cancel();
        }
        mLoadText.setText("更新完成");

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY, int deltaY) {

        LogUtil.e("headerMovePercent = " + headerMovePercent + " offsetY " + offsetY + " deltaY " + deltaY);

        if (deltaY < 0) {

        }
    }

    @Override
    public void setRefreshTime(long lastRefreshTime) {


    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }
}
