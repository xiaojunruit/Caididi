package com.laoodao.caididi.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


public class KeyBoard {
    public static void assistActivity(Activity activity, OnInputMethodManagerLinstener linstener) {
        new KeyBoard(activity, linstener);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;

    private KeyBoard(final Activity activity, final OnInputMethodManagerLinstener linstener) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent(activity, linstener);
            }
        });
    }

    private void possiblyResizeChildOfContent(Activity activity, OnInputMethodManagerLinstener linstener) {
        int usableHeightNow = computeUsableHeight(activity);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                if (linstener != null) {
                    linstener.inputMethodCallBack(true);
                }
            } else {
                if (linstener != null) {
                    linstener.inputMethodCallBack(false);
                }
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight(Activity activity) {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        int sta = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sta = ScreenUtils.getStatusHeight(activity);
        }
        return (r.bottom - r.top + sta);
    }


    /**
     * 键盘监听接口
     */
    public interface OnInputMethodManagerLinstener {
        /**
         * 键盘状态接口
         *
         * @param isShow      是否显示
         */
        public void inputMethodCallBack(boolean isShow);
    }

}
