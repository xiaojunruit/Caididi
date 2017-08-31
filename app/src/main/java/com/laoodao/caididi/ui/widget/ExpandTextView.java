package com.laoodao.caididi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;

import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/2/22.
 */

public class ExpandTextView extends LinearLayout {
    private Context mContext;
    private ImageView mImageView;  //翻转icon
    private TextView mTextContext;
    private TextView mTextFull;
    private LinearLayout llOperate;
    private int maxLine = 5;
    private int total;
    private Drawable etvFullImg;
    private String etvPrompt;
    private boolean isExpand;  //是否翻转
    private float spacing;
    private int color;

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandTextView);
        etvFullImg = a.getDrawable(R.styleable.ExpandTextView_etv_full_img);
        maxLine = a.getInteger(R.styleable.ExpandTextView_etv_max_lines, 5);
        etvPrompt = a.getString(R.styleable.ExpandTextView_etv_prompt);
        color = a.getColor(R.styleable.ExpandTextView_etv_text_color, 0xff666666);
        a.recycle();
        initData();

    }

    private void initData() {
        View.inflate(mContext, R.layout.widget_expand, this);
        mImageView = (ImageView) findViewById(R.id.img_right);
        mTextContext = (TextView) findViewById(R.id.txt_content);
        mTextFull = (TextView) findViewById(R.id.txt_full);
        llOperate = (LinearLayout) findViewById(R.id.ll_operate);
        mImageView.setBackground(etvFullImg);
        mTextContext.setTextColor(color);
        mTextFull.setOnClickListener(new MyTurnListener());
    }


    public void postText() {
        //根据高度来控制是否展示翻转icon
        mTextContext.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                total = mTextContext.getLineCount();
                if (total > maxLine) {
                    mTextContext.setMaxLines(maxLine);
                    llOperate.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.VISIBLE);
                    mTextFull.setVisibility(View.VISIBLE);
                } else {
                    mTextContext.setMaxLines(total);
                    llOperate.setVisibility(View.GONE);
                    mImageView.setVisibility(View.GONE);
                    mTextFull.setVisibility(View.GONE);
                }
            }
        },100);
    }

    public TextView getTextContent() {
        return mTextContext;
    }

    public void setEtvPrompt(String prompt) {
        this.etvPrompt = prompt;
    }

    public void setExpand(boolean expand) {
        this.isExpand = expand;
        mTextFull.setText(etvPrompt);
        RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(0);
        animation.setFillAfter(true);
        mImageView.startAnimation(animation);
    }

    public class MyTurnListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            isExpand = !isExpand;
            mTextContext.clearAnimation();  //清除动画
            final int tempHight;
            final int startHight = mTextContext.getHeight();  //起始高度
            int durationMillis = 200;
            if (isExpand) {
                /**
                 * 折叠效果，从长文折叠成短文
                 */
                etvPrompt = "点击收起";
//                tempHight = mTextContext.getLineHeight() * mTextContext.getLineCount() - startHight;  //为正值，长文减去短文的高度差


                tempHight = total;
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                mImageView.startAnimation(animation);
            } else {
                /**
                 * 展开效果，从短文展开成长文
                 */
                etvPrompt = "阅读全文";
//                tempHight = mTextContext.getLineHeight() * maxLine - startHight;//为负值，即短文减去长文的高度差


                tempHight = maxLine;
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                mImageView.startAnimation(animation);
            }
            mTextFull.setText(etvPrompt);
            Animation animation = new Animation() {
                //interpolatedTime 为当前动画帧对应的相对时间，值总在0-1之间
                protected void applyTransformation(float interpolatedTime, Transformation t) { //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
//                    mTextContext.setHeight((int) (startHight + tempHight * interpolatedTime));//原始长度+高度差*（从0到1的渐变）即表现为动画效果

                }
            };
            mTextContext.setMaxLines(tempHight);
            animation.setDuration(durationMillis);
            mTextContext.startAnimation(animation);

        }
    }

}
