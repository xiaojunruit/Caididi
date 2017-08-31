package com.laoodao.caididi.ui.weather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by user on 2016/10/19.
 */
public class IndexHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "IndexHorizontal";
    private Paint textPaint;
    private Today24HourView today24HourView;

    public IndexHorizontalScrollView(Context context) {
        this(context, null);
    }

    public IndexHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int offset = 0;
    int maxOffset = 0;

    private void init() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);

        textPaint.setColor(new Color().WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        offset = computeHorizontalScrollOffset();
        maxOffset = computeHorizontalScrollRange() - getWidth();
        if (today24HourView != null) {
//            today24HourView.drawLeftTempText(canvas, offset);

            today24HourView.setScrollOffset(offset, maxOffset);
        }
    }

    public void setToday24HourView(Today24HourView today24HourView) {
        this.today24HourView = today24HourView;
    }
}
