package com.laoodao.caididi.ui.adapter;

import android.content.Context;
import android.widget.ImageView;


import com.laoodao.caididi.ui.widget.ShapedImageView;

import java.util.List;

import ezy.lite.util.Device;


public abstract class NineGridImageViewAdapter<T> {
    public abstract void onDisplayImage(Context context, ImageView imageView, T t);

    public void onItemImageClick(Context context, int index, List<T> list) {
    }

    public ImageView generateImageView(Context context) {
        ShapedImageView imageView = new ShapedImageView(context);
        imageView.setShapeMode(ShapedImageView.SHAPE_MODE_ROUND_RECT);
        imageView.setRadius(Device.dp2px(5));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

}