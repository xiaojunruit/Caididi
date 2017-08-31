package com.laoodao.caididi.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AutofitRoundImageView extends ShapedImageView {


    public AutofitRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutofitRoundImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getDrawable() == null) {
            return;
        }


        int w = getDrawable().getIntrinsicWidth();
        int h = getDrawable().getIntrinsicHeight();
        if (w <= 0) {
            w = 1;
        }
        if (h <= 0) {
            h = 1;
        }

        // Desired aspect ratio of the view's contents (not including padding)
        float ratio = (float) w / (float) h;

        // We are allowed to change the view's width and height
        boolean resizeWidth = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY;
        boolean resizeHeight = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;

        int pw = getPaddingLeft() + getPaddingRight();
        int ph = getPaddingTop() + getPaddingBottom();

        // Get the sizes that ImageView decided on.
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

//        if (resizeWidth && !resizeHeight) {
//            width = (int) (ratio * (height - ph)) + pw;
//            setMeasuredDimension(width, height);
//        } else if (resizeHeight && !resizeWidth) {
        height = (int) ((width - pw) / ratio) + ph;
        setMeasuredDimension(width, height);
//        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }
}