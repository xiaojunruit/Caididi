package com.laoodao.caididi.ui.weather.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import ezy.lite.util.Device;

/**
 * @author deadline
 * @time 2016/9/24
 */
public class GradeProgressView extends View {


    private static final String TAG = GradeProgressView.class.getSimpleName();

    private static final int DEFAULT_PROGRESS_COLOR = Color.parseColor("#5A2AB80E");
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#5AFFFFFF");

    private int mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;

    //进度条的每格线宽，间距，长度
    private int dashWith = Device.dp2px(1);
    private int dashSpace = Device.dp2px(1);
    private int lineWidth = Device.dp2px(10);

    //最外圈线的宽度和与进度条之间的间距
    private int outLineWidth = 0;
    private int gapWidth = 25;
    //指针的线宽度， 半径， 以及指针与进度条的间距
    private int pointGap = 20;

    private int mProgress = 0;


    //进度条
    private RectF mRectF;
    private Paint mPaint;
    private Paint mProgressPaint;

    //指针
    private Paint mPointerPaint;
    private Path mPointerPath;
    //文字
    private Paint mTextPaint, mTextPaint2;

    private float centerX;
    private float centerY;

    private ValueAnimator animator;

    private OnProgressChangeListener mListener;

    public GradeProgressView(Context context) {
        this(context, null);
    }

    public GradeProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradeProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GradeProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void setup() {

        mRectF = new RectF();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{dashWith, dashSpace}, dashSpace));

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStrokeWidth(lineWidth);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setPathEffect(new DashPathEffect(new float[]{dashWith, dashSpace}, dashSpace));

        mPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPointerPaint.setStrokeWidth(pointLineWidth / 2);
        mPointerPaint.setColor(mProgressColor);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPointerPaint.setShadowLayer(4, 3, 0, 0x20000000);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(Device.sp2px(28));
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        mTextPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint2.setColor(Color.WHITE);
        mTextPaint2.setTextSize(Device.sp2px(10));

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int value = lineWidth / 2;
        mRectF.set(value,
                value,
                w - value,
                h - value);

        centerX = mRectF.centerX();
        centerY = mRectF.centerY();
        mPointerPath = new Path();
        mPointerPath.addCircle(mRectF.right - pointGap - lineWidth / 2 - Device.dp2px(3), centerY, Device.dp2px(3), Path.Direction.CW);

        mPointerPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float degree = 2.9f * mProgress;


        //draw background arc
        canvas.drawArc(mRectF, 125 + degree, 290 - degree, false, mPaint);

        //draw progress arc
        canvas.drawArc(mRectF, 125, degree, false, mProgressPaint);

        //进度
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        float baseLineY = mRectF.centerY() - top / 2 - bottom / 2;
        canvas.drawText(mProgress + "%", mRectF.centerX(), baseLineY, mTextPaint);


        canvas.drawText("0", getWidth() / 5, getHeight(), mTextPaint2);

        canvas.drawText("100", getWidth() / 5 * 4 - 50, getHeight(), mTextPaint2);

        canvas.save();
        canvas.rotate(125 + degree, centerX, centerY);
        canvas.drawPath(mPointerPath, mPointerPaint);
        canvas.restore();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(measureHeight, measureWidth), Math.min(measureHeight, measureWidth));
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.mListener = listener;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        if (mProgressPaint != null) {
            mProgressPaint.setColor(mProgressColor);
        }

        if (mPointerPaint != null) {
            mPointerPaint.setColor(mProgressColor);
        }

        postInvalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {

        this.mBackgroundColor = backgroundColor;
        if (mPaint != null) {
            mPaint.setColor(mBackgroundColor);
        }

        postInvalidate();
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        if (mPaint != null) {
            mPaint.setStrokeWidth(lineWidth);
        }

        if (mProgressPaint != null) {
            mProgressPaint.setStrokeWidth(lineWidth);
        }
        postInvalidate();
    }

    public int getOutLineWidth() {
        return outLineWidth;
    }


    public int getGapWidth() {
        return gapWidth;
    }

    public void setGapWidth(int gapWidth) {
        this.gapWidth = gapWidth;
    }

    public int getProgress() {
        return mProgress;
    }

    //没动画的
    public void setProgress(@IntRange(from = 0, to = 100) int progress) {
        if (progress > 100) {
            progress = 100;
        }

        if (progress < 0) {
            progress = 0;
        }
        this.mProgress = progress;
        if (mListener != null) {
            mListener.onProgressChanged(GradeProgressView.this, mProgress);
        }
        postInvalidate();
    }

    //有动画的
    public void setProgressWidthAnimation(@IntRange(from = 0, to = 100) int progress) {

        if (progress > 100) {
            progress = 100;
        }

        if (progress < 0) {
            progress = 0;
        }
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }

        animator = ValueAnimator.ofInt(mProgress, progress);
        int duration = 10 * Math.abs(progress - mProgress);
        animator.setDuration(duration);

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (mProgress != value) {
                    mProgress = value;
                    if (mListener != null) {
                        mListener.onProgressChanged(GradeProgressView.this, mProgress);
                    }
                    postInvalidate();
                }
            }
        });
        animator.start();

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (animator != null) {
            animator.cancel();
            animator = null;
        }

    }


    public interface OnProgressChangeListener {

        void onProgressChanged(GradeProgressView gradeProgressView, int progress);
    }
}
