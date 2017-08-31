package com.laoodao.caididi.ui.widget.citySelector.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.laoodao.caididi.R;

import ezy.lite.util.LogUtil;


public class SideLetterBar extends View {
    private static final String[] b = {"当前", "定位", "最近", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private int choose = -1;
    private Paint paint;
    private boolean showBg = false;
    private OnLetterChangedListener onLetterChangedListener;
    private TextView overlay;
    private Handler handler;

    private OverlayThread overlayThread;

    public SideLetterBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handler = new Handler();
        overlayThread = new OverlayThread();
        paint = new Paint();
    }

    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    public SideLetterBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideLetterBar(Context context) {
        this(context, null);
    }

    /**
     * 设置悬浮的textview
     *
     * @param overlay
     */
    public void setOverlay(TextView overlay) {
        this.overlay = overlay;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBg) {
            canvas.drawColor(Color.TRANSPARENT);
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            paint.setTextSize(getResources().getDimension(R.dimen.sp_6));
            paint.setColor(Color.parseColor("#8c8c8c"));
            paint.setAntiAlias(true);
            if (i == choose) {
                paint.setColor(Color.parseColor("#8c8c8c"));
//                paint.setFakeBoldText(true);  //加粗
            }
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnLetterChangedListener listener = onLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onLetterChanged(b[c]);
                        choose = c;
                        invalidate();

                        if (overlay != null) {
                            LogUtil.e("ACTION_DOWN================>>>" + b[c]);
                            showOverlay(b[c]);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onLetterChanged(b[c]);
                        choose = c;
                        invalidate();
                        if (overlay != null) {
                            LogUtil.e("ACTION_MOVE================>>>" + b[c]);
                            showOverlay(b[c]);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBg = false;
                choose = -1;
                invalidate();
//                if (overlay != null) {
//                    overlay.setVisibility(GONE);
//                }
                break;
        }
        return true;
    }

    public void showOverlay(String s) {
        overlay.setVisibility(VISIBLE);
        LogUtil.e("======================sss" + s);
        overlay.setText(s);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    public interface OnLetterChangedListener {
        void onLetterChanged(String letter);
    }

}
