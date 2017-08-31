package com.laoodao.caididi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;

/**
 * Created by ouyua on 2016/11/30.
 */

public class TextImageButton extends RelativeLayout {
    private float mImageSize;
    private float mTextSize;
    private String mText;
    private Drawable mImage;
    private TextView mTextView;
    private ImageView mImageView;

    public TextImageButton(Context context) {
        this(context, null);
    }

    public TextImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextImageButton);
        mImageSize = a.getDimension(R.styleable.TextImageButton_ttb_imageSize, 30);
        mTextSize = a.getDimension(R.styleable.TextImageButton_ttb_textSize, 14);
        mText = a.getString(R.styleable.TextImageButton_ttb_text);
        mImage = a.getDrawable(R.styleable.TextImageButton_ttb_image);
        a.recycle();
        inflate(context, R.layout.widget_icon_button, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (TextView) findViewById(R.id.title);
        mImageView = (ImageView) findViewById(R.id.icon);
        if (!TextUtils.isEmpty(mText)) {
            mTextView.setText(mText);
        }
        mTextView.setTextSize(mTextSize);
        if (mImage != null) {
            mImageView.setImageDrawable(mImage);
        }
        ViewGroup.LayoutParams params = mImageView.getLayoutParams();
        params.width = (int) mImageSize;
        params.height = (int) mImageSize;
        mImageView.setLayoutParams(params);
    }
}
