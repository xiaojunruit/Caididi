package com.hyphenate.easeui.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;

/**
 * Created by WORK on 2017/8/18.
 */

public class PicSelectDialog extends Dialog {
    private FrameLayout mBtnCamera;
    private FrameLayout mBtnPhoto;
    private TextView mBtnCancel;
    public OnCameraListener mOnCameraListener;

    public OnPhotoListener mOnPhotoListener;

    public PicSelectDialog(@NonNull Context context) {
        super(context, R.style.style_dialog);
    }

    public void setOnCameraListener(OnCameraListener listener) {
        this.mOnCameraListener = listener;
    }


    public void setOnPhotoListener(OnPhotoListener listener) {
        this.mOnPhotoListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pic_select);

        initView();
        listener();
    }

    private void initView() {
        mBtnCamera = (FrameLayout) findViewById(R.id.btn_camera);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mBtnPhoto = (FrameLayout) findViewById(R.id.btn_photo);
    }

    private void listener() {
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCameraListener.setOnCameraListener();
                dismiss();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoListener.setOnPhotoListener();
                dismiss();
            }
        });
    }

    public interface OnCameraListener {
        void setOnCameraListener();
    }



    public interface OnPhotoListener {
        void setOnPhotoListener();
    }
}
