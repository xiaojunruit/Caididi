package com.laoodao.caididi.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * =====作者=====
 * 许英俊
 * =====时间=====
 * 2016/11/8.
 */
public class WaterRippleView extends View {

    //波浪画笔
    private Paint mPaint;

    //波浪Path类
    private Path mPath;
    //一个波浪长度
    private int mWaveLength = 1000;
    private int mWaveLength2 = 800;
    private int mWaveLength3 = 600;
    //波纹个数
    private int mWaveCount;
    private int mWaveCount2;
    private int mWaveCount3;

    //平移偏移量
    private int mOffset;
    private int mOffse2;
    private int mOffse3;
    //波纹的中间轴
    private float mCenterY;
    private float mCenterY2;
    private float mCenterY3;


    //屏幕高度
    private int mScreenHeight;
    //屏幕宽度
    private int mScreenWidth;


    public WaterRippleView(Context context) {
        super(context);
    }

    public WaterRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaterRippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#2AB80E"));
        mPaint.setStyle(Paint.Style.STROKE);
        startAnimator();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeight = h;
        mScreenWidth = w;
        //加1.5：至少保证波纹有2个，至少2个才能实现平移效果
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
        mWaveCount2 = (int) Math.round(mScreenWidth / mWaveLength2 + 1.5);
        mWaveCount3 = (int) Math.round(mScreenWidth / mWaveLength3 + 1.5);
        mCenterY = (float) (mScreenHeight / 2.2);
        mCenterY2 = (float) (mScreenHeight / 2.4);
        mCenterY3 = (float) (mScreenHeight / 2.6);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(-mWaveLength + mOffset, mCenterY);
        for (int i = 0; i < mWaveCount; i++) {
            mPath.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset, mCenterY + 60, (-mWaveLength / 2) + (i * mWaveLength) + mOffset, mCenterY);
            mPath.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset, mCenterY - 60, i * mWaveLength + mOffset, mCenterY);
        }
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(-mWaveLength2 + mOffse2, mCenterY2);
        for (int i = 0; i < mWaveCount2; i++) {
            mPath.quadTo((-mWaveLength2 * 3 / 4) + (i * mWaveLength2) + mOffse2, mCenterY2 + 120, (-mWaveLength2 / 2) + (i * mWaveLength2) + mOffse2, mCenterY2);
            mPath.quadTo((-mWaveLength2 / 4) + (i * mWaveLength2) + mOffse2, mCenterY2 - 120, i * mWaveLength2 + mOffse2, mCenterY2);
        }
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(-mWaveLength3 + mOffse3, mCenterY3);
        for (int i = 0; i < mWaveCount3; i++) {
            mPath.quadTo((-mWaveLength3 * 3 / 4) + (i * mWaveLength3) + mOffse3, mCenterY3 + 80, (-mWaveLength3 / 2) + (i * mWaveLength3) + mOffse3, mCenterY3);
            mPath.quadTo((-mWaveLength3 / 4) + (i * mWaveLength3) + mOffse3, mCenterY3 - 80, i * mWaveLength3 + mOffse3, mCenterY3);
        }
        canvas.drawPath(mPath, mPaint);
    }

    private void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(0, mWaveLength);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
        ValueAnimator animator1 = ValueAnimator.ofInt(0, mWaveLength2);
        animator1.setDuration(1000);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffse2 = (int) animation.getAnimatedValue();
            }
        });
        animator1.start();
        animator.start();
        ValueAnimator animator2 = ValueAnimator.ofInt(0, mWaveLength3);
        animator2.setDuration(1000);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffse3 = (int) animation.getAnimatedValue();
            }
        });
        animator2.start();
    }
}
