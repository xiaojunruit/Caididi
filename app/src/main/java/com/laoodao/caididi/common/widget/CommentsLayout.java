package com.laoodao.caididi.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/12/17.
 */

public class CommentsLayout extends LinearLayout {

    private String clNickName;
    private String clTime;
    private String clAddress;
    private String clReadCount;
    private Drawable clAvatar;
    private TextView tvName;
    private CircleImageView mAvatar;
    private TextView tvTime;
    private TextView tvAddress;
    private TextView tvRead;

    private int mLayoutId;


    public CommentsLayout(Context context) {
        this(context, null);
    }

    public CommentsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommentsLayout);
        clAvatar = a.getDrawable(R.styleable.CommentsLayout_cl_avatar);
        clNickName = a.getString(R.styleable.CommentsLayout_cl_nick_name);
        clTime = a.getString(R.styleable.CommentsLayout_cl_time);
        clAddress = a.getString(R.styleable.CommentsLayout_cl_address);
        clReadCount = a.getString(R.styleable.CommentsLayout_cl_read_count);
        mLayoutId = a.getResourceId(R.styleable.CommentsLayout_cl_layout, R.layout.widget_comments);
        a.recycle();
        inflate(context, mLayoutId, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvName = (TextView) findViewById(R.id.cl_nick_name);
        mAvatar = (CircleImageView) findViewById(R.id.cl_avatar);
        tvTime = (TextView) findViewById(R.id.cl_time);
        tvAddress = (TextView) findViewById(R.id.cl_address);
        tvRead = (TextView) findViewById(R.id.cl_read_count);


    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvAddress() {
        return tvAddress;
    }

    public TextView getTvTime() {
        return tvTime;
    }

    public TextView getTvRead() {
        return tvRead;
    }

    public CircleImageView getmAvatar() {
        return mAvatar;
    }
}
