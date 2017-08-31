package com.laoodao.caididi.ui.weather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.weather.Hours;
import com.laoodao.caididi.ui.weather.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.LogUtil;

/**
 * Created by user on 2016/10/19.
 */
public class Today24HourView extends View {

    private Context mContext;
    private int mStep = dp2px(45);
    private List<Hours> mHours = new ArrayList<>();
    private int MARGIN_LEFT_ITEM = dp2px(10); //左边预留宽度
    private int MARGIN_RIGHT_ITEM = dp2px(10);
    ; //右边预留宽度
    private int mWidth, mHeight;
    private int bottomTextHeight = dp2px(15);
    private int windBoxHeight = dp2px(15);
    private int maxTem = 27;
    private int minTem = -1;
    private int mSize = 0;
    private List<RectF> mBottomTextRect = new ArrayList<>();
    private List<RectF> mWindBoxRect = new ArrayList<>();
    private List<Point> mPointList = new ArrayList<>();
    private Path mPath;
    private int mCurrentIndex;


    private int[] tems = {10, 10, 10, -1, 10, 9, 22, 27, 16,
            14, 24, 23, 10, 10, 22, 19, 18, 12, 13, 26, 24, 15, 13, 10};

    public Today24HourView(Context context) {
        this(context, null);
    }

    public Today24HourView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Today24HourView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mWidth = MARGIN_LEFT_ITEM + MARGIN_RIGHT_ITEM + 24 * mStep;
        mHeight = dp2px(120);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

    }

    private Paint mTextPaint, mWindyBoxPaint, mLinePaint, mPointPaint;

    private void initPaint() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(sp2px(10));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mWindyBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWindyBoxPaint.setTextSize(1);
        mWindyBoxPaint.setColor(Color.parseColor("#ddffffff"));
        mWindyBoxPaint.setAlpha(80);


        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAlpha(80);


        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStrokeWidth(5);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.WHITE);

        Shader mShader = new LinearGradient(0, 0, MARGIN_LEFT_ITEM + 24 * mStep, 0,
                new int[]{Color.parseColor("#4D2AB80E"), Color.parseColor("#4dfdb41c")},
                null, Shader.TileMode.REPEAT);
        mLinePaint.setShader(mShader);

    }

    public void setData(int maxTem, int minTem, List<Hours> hoursList) {

        this.minTem = minTem;
        this.maxTem = maxTem;
        mHours.clear();
        mHours.addAll(hoursList);
        mSize = mHours.size();
        initData();

        invalidate();
    }

    private void initData() {
        mPath = new Path();
        //控件总宽度

        //控件总高度
        for (int i = 0; i < mSize; i++) {
            int left = MARGIN_LEFT_ITEM + i * mStep;
            int top = mHeight - bottomTextHeight;
            int right = left + mStep - 1;
            int bottom = mHeight;
            //底部时间
            RectF rect = new RectF(left, top, right, bottom);
            mBottomTextRect.add(rect);
            //风力
            RectF windBox = new RectF(left, top - windBoxHeight, right, top);
            mWindBoxRect.add(windBox);

            Hours hours = mHours.get(i);
            hours.res = WeatherUtils.getWeatherResId(hours.cw);
            if (i != 0) {
                Hours preHours = mHours.get(i - 1);
                if (preHours.cw.equals(hours.cw)) {
                    hours.res = -1;
                }
            }


        }

        /**
         * 曲线高度范围
         */
        int range = mHeight - bottomTextHeight - windBoxHeight - dp2px(20);

        int cha = maxTem - minTem;


        // height:300,  最高30°最低10° 相差20°  最低温度应该在0 ,最高在300px . 没读15px
        // height -  (温度 - 最低温度 )* 15px = 所在位置

        mPath.reset();

        for (int i = 0; i < mSize; i++) {
            Point point = new Point();
            Hours hours = mHours.get(i);
            point.y = (range) - (hours.tmp - minTem) * ((range - dp2px(30)) / cha);
            LogUtil.e("rang = " + range + "tem = " + hours.tmp + " minTem = " + minTem + " maxTem  = " + maxTem);
            int x = MARGIN_LEFT_ITEM + i * mStep + mStep / 2;
            point.x = x;
            mPointList.add(point);
//            if (i == 0) {
//                mPath.moveTo(point.x, point.y);
//            } else {
//                mPath.lineTo(point.x, point.y);
//            }

            if (i != 0) {
                Point pointPre = mPointList.get(i - 1);
                mPath.moveTo(pointPre.x, pointPre.y);
                if (i % 2 == 0)
                    mPath.cubicTo((pointPre.x + point.x) / 2, (pointPre.y + point.y) / 2 - 7, (pointPre.x + point.x) / 2, (pointPre.y + point.y) / 2 + 7, point.x, point.y);
                else
                    mPath.cubicTo((pointPre.x + point.x) / 2, (pointPre.y + point.y) / 2 + 7, (pointPre.x + point.x) / 2, (pointPre.y + point.y) / 2 - 7, point.x, point.y);
            }
        }

//        measurePath();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mSize; i++) {
            onDrawText(canvas, i);
            onDrawWindBox(canvas, i);
//            onDrawLine(canvas, i);
//            drawScrollLine(canvas);

            canvas.drawPath(mPath, mLinePaint);

            onDrawWeather(canvas, i);
            //画表示天气图片
            if (mHours.get(i).res != -1 && i != mCurrentIndex) {

                Point point = mPointList.get(i);

                Drawable drawable = ContextCompat.getDrawable(getContext(), mHours.get(i).res);
                drawable.setBounds(point.x - dp2px(10),
                        point.y - dp2px(25),
                        point.x + dp2px(10),
                        point.y - dp2px(5));
                drawable.draw(canvas);
            }
        }
        for (int i = 0; i < mSize; i++) {
            onDrawPoint(canvas, i);
        }
    }

    private void onDrawWeather(Canvas canvas, int i) {
        Hours hours = mHours.get(i);


        if (i == mCurrentIndex) {

            int y = getTempBarY();
            Drawable hint = ContextCompat.getDrawable(mContext, R.mipmap.hour24_hint);
            //气泡
            hint.setBounds(getScrollBarX(),
                    y - dp2px(25), getScrollBarX() + mStep, y - dp2px(3));
            hint.draw(canvas);
            //画天气
            int res = findCurrentRes(i);
            if (res != -1) {

                Drawable drawable = ContextCompat.getDrawable(mContext, res);

                drawable.setBounds(
                        getScrollBarX() + mStep / 2 + (mStep / 2 - dp2px(18)) / 2,
                        y - dp2px(24),
                        getScrollBarX() + mStep - (mStep / 2 - dp2px(18)) / 2,
                        y - dp2px(6));
                drawable.draw(canvas);
            }

            int offset = mStep / 2;
            if (res == -1)
                offset = mStep;
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            Rect targetRect = new Rect(getScrollBarX()
                    , y - dp2px(24),
                    getScrollBarX() + offset,
                    y - dp2px(6));
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText(hours.tmp + "°", targetRect.centerX(), baseline, mTextPaint);
        }
    }


    private void measurePath() {
        //保存曲线路径 
        //保存辅助线路径
        Path mAssistPath = new Path();

        mPath = new Path();

        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        final int lineSize = mPointList.size();
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                Point point = mPointList.get(valueIndex);
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    Point point = mPointList.get(valueIndex - 1);
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    Point point = mPointList.get(valueIndex - 2);
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < lineSize - 1) {
                Point point = mPointList.get(valueIndex + 1);
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                mPath.moveTo(currentPointX, currentPointY);
                mPath.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (0.2f * firstDiffX);
                final float firstControlPointY = previousPointY + (0.2f * firstDiffY);
                final float secondControlPointX = currentPointX - (0.2f * secondDiffX);
                final float secondControlPointY = currentPointY - (0.2f * secondDiffY);
                //画出曲线
                mPath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
                //将控制点保存到辅助路径上
                mAssistPath.lineTo(firstControlPointX, firstControlPointY);
                mAssistPath.lineTo(secondControlPointX, secondControlPointY);
                mAssistPath.lineTo(currentPointX, currentPointY);
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;

            LogUtil.e("=================>>>>:");
        }
    }


    private void drawScrollLine(Canvas canvas) {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < mPointList.size() - 1; i++) {
            startp = mPointList.get(i);
            endp = mPointList.get(i + 1);
            int wt = (startp.x + endp.x) / 2;
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = startp.y;
            p3.x = wt;
            p4.y = endp.y;
            p4.x = wt;
            Path path = new Path();
            path.moveTo(startp.x, startp.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            canvas.drawPath(path, mLinePaint);
        }
    }

    private int findCurrentRes(int i) {
        if (mHours.get(i).res != -1)
            return mHours.get(i).res;
        for (int k = i; k >= 0; k--) {
            if (mHours.get(k).res != -1)
                return mHours.get(k).res;
        }
        return -1;
    }

    private void onDrawPoint(Canvas canvas, int i) {


        Point point = mPointList.get(i);
        Log.e("====>>>", point.x + " i " + i);
        canvas.drawCircle(point.x, point.y, dp2px(3), mPointPaint);
    }

    private void onDrawWindBox(Canvas canvas, int i) {
        RectF rectF = mWindBoxRect.get(i);
        Hours hours = mHours.get(i);
        canvas.drawRoundRect(rectF, 4, 4, mWindyBoxPaint);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        float baseLineY = rectF.centerY() - top / 2 - bottom / 2;
        canvas.drawText(hours.wdg + "级", rectF.centerX(), baseLineY, mTextPaint);
    }

    private void onDrawText(Canvas canvas, int i) {

        RectF rectF = mBottomTextRect.get(i);
        Hours hours = mHours.get(i);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        float baseLineY = rectF.centerY() - top / 2 - bottom / 2;
        canvas.drawText(hours.isNow ? "现在" : hours.showTime, rectF.centerX(), baseLineY, mTextPaint);
    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());

    }

    public int sp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpVal, getResources().getDisplayMetrics());

    }

    private int maxScrollOffset = 1;//滚动条最长滚动距离
    private int scrollOffset = 0; //滚动条偏移量

    public void setScrollOffset(int offset, int maxOffset) {
        this.maxScrollOffset = maxOffset;
        scrollOffset = offset;
        int index = calculateItemIndex(offset);
        mCurrentIndex = index;
        postInvalidate();
    }

    //通过滚动条偏移量计算当前选择的时刻
    private int calculateItemIndex(int offset) {
//        Log.d(TAG, "maxScrollOffset = " + maxScrollOffset + "  scrollOffset = " + scrollOffset);
        int x = getScrollBarX();
        int sum = MARGIN_LEFT_ITEM - mStep / 2;
        for (int i = 0; i < mSize; i++) {
            sum += mStep;
            if (x < sum)
                return i;
        }
        return mSize - 1;
    }

    private int getScrollBarX() {
        int x = (mSize - 1) * mStep * scrollOffset / maxScrollOffset;
        x = x + MARGIN_LEFT_ITEM;

        Log.e("========>>>", maxScrollOffset + "------------->>>" + scrollOffset);
        Log.e("====>>>", mWidth + "------>>>" + x + "----" + (mStep * 23 + MARGIN_RIGHT_ITEM + MARGIN_LEFT_ITEM));
        return x;
    }


    //计算温度提示文字的运动轨迹
    private int getTempBarY() {
        int x = getScrollBarX();
        int sum = MARGIN_LEFT_ITEM;
        Point startPoint = null, endPoint;
        int i;
        for (i = 0; i < mSize; i++) {
            sum += mStep;
            if (x < sum) {
                startPoint = mPointList.get(i);
                break;
            }
        }
        if (i + 1 >= mSize || startPoint == null)
            return mPointList.get(mSize - 1).y;
        endPoint = mPointList.get(i + 1);

        RectF rect = mWindBoxRect.get(i);
        int y = (int) (startPoint.y + (x - rect.left) * 1.0 / mStep * (endPoint.y - startPoint.y));
        return y;
    }

    private String json = "[\n" +
            "    {\n" +
            "        \"airp\": \"101031\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"02\",\n" +
            "        \"cwd\": \"03\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"76\",\n" +
            "        \"show_time\": \"00:00\",\n" +
            "        \"st\": \"18\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"18\",\n" +
            "        \"w\": \"阴\",\n" +
            "        \"wd\": \"东南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101031\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"02\",\n" +
            "        \"cwd\": \"03\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"76\",\n" +
            "        \"show_time\": \"01:00\",\n" +
            "        \"st\": \"18\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"18\",\n" +
            "        \"w\": \"阴\",\n" +
            "        \"wd\": \"东南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101031\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"02\",\n" +
            "        \"cwd\": \"03\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"76\",\n" +
            "        \"show_time\": \"02:00\",\n" +
            "        \"st\": \"18\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"18\",\n" +
            "        \"w\": \"阴\",\n" +
            "        \"wd\": \"东南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100935\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"02\",\n" +
            "        \"cwd\": \"01\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"81\",\n" +
            "        \"show_time\": \"03:00\",\n" +
            "        \"st\": \"17\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"17\",\n" +
            "        \"w\": \"阴\",\n" +
            "        \"wd\": \"东北风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100935\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"01\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"81\",\n" +
            "        \"show_time\": \"04:00\",\n" +
            "        \"st\": \"20\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"20\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东北风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100935\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"01\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"81\",\n" +
            "        \"show_time\": \"05:00\",\n" +
            "        \"st\": \"19\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"19\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东北风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101053\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"01\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"79\",\n" +
            "        \"show_time\": \"06:00\",\n" +
            "        \"st\": \"18\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"18\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东北风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101053\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"01\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"79\",\n" +
            "        \"show_time\": \"07:00\",\n" +
            "        \"st\": \"17\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"17\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东北风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101053\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"01\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"79\",\n" +
            "        \"show_time\": \"08:00\",\n" +
            "        \"st\": \"19\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"19\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东北风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101095\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"02\",\n" +
            "        \"is_now\": true,\n" +
            "        \"rh\": \"71\",\n" +
            "        \"show_time\": \"09:00\",\n" +
            "        \"st\": \"21\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"21\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101095\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"02\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"71\",\n" +
            "        \"show_time\": \"10:00\",\n" +
            "        \"st\": \"23\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"23\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101095\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"02\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"71\",\n" +
            "        \"show_time\": \"11:00\",\n" +
            "        \"st\": \"24\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"24\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100834\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"51\",\n" +
            "        \"show_time\": \"12:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100834\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"51\",\n" +
            "        \"show_time\": \"13:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100834\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"51\",\n" +
            "        \"show_time\": \"14:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100689\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"48\",\n" +
            "        \"show_time\": \"15:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100689\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"48\",\n" +
            "        \"show_time\": \"16:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100689\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"48\",\n" +
            "        \"show_time\": \"17:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100911\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"73\",\n" +
            "        \"show_time\": \"18:00\",\n" +
            "        \"st\": \"25\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"25\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"3\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100911\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"73\",\n" +
            "        \"show_time\": \"19:00\",\n" +
            "        \"st\": \"24\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"24\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"3\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"100911\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"04\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"73\",\n" +
            "        \"show_time\": \"20:00\",\n" +
            "        \"st\": \"23\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"23\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"南风\",\n" +
            "        \"wdg\": \"3\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101130\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"03\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"86\",\n" +
            "        \"show_time\": \"21:00\",\n" +
            "        \"st\": \"22\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"22\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101130\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"03\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"86\",\n" +
            "        \"show_time\": \"22:00\",\n" +
            "        \"st\": \"22\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"22\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"airp\": \"101130\",\n" +
            "        \"aqi\": \"0\",\n" +
            "        \"cw\": \"01\",\n" +
            "        \"cwd\": \"03\",\n" +
            "        \"is_now\": false,\n" +
            "        \"rh\": \"86\",\n" +
            "        \"show_time\": \"23:00\",\n" +
            "        \"st\": \"21\",\n" +
            "        \"tip_aqi\": \"\",\n" +
            "        \"tmp\": \"21\",\n" +
            "        \"w\": \"多云\",\n" +
            "        \"wd\": \"东南风\",\n" +
            "        \"wdg\": \"0\"\n" +
            "    }\n" +
            "]";
}
