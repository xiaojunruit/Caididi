package com.laoodao.caididi.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

import ezy.lite.util.DateTime;
import ezy.lite.util.Hash;

/**
 * Created by ezy on 15/11/10.
 */
public class Convert {

    public static String toToken(int userId, String hash) {
        return Hash.md5(userId + hash + DateTime.format("yyyyMMdd"));
    }

//    public static <T extends ViewDataBinding> T inflate(Context context, int layoutId) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        return DataBindingUtil.inflate(inflater, layoutId, null, false);
//    }
//    public static <T extends ViewDataBinding> T bind(Context context, int layoutId, Object item) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        T binding = DataBindingUtil.inflate(inflater, layoutId, null, false);
//        binding.setVariable(BR.item, item);
//        return binding;
//
//    }

    public static final Uri toUri(@NonNull Context context, @AnyRes int resId) {
        Resources res = context.getResources();
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                       "://" + res.getResourcePackageName(resId)
                                       + '/' + res.getResourceTypeName(resId)
                                       + '/' + res.getResourceEntryName(resId));
        return resUri;
    }

}
