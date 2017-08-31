package com.laoodao.caididi.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

/**
 * Created by WORK on 2017/1/3.
 */

public class Helper {

    public static final Uri toUri(@NonNull Context context, @AnyRes int resId) {
        Resources res = context.getResources();
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId) + '/' + res.getResourceTypeName(resId) + '/' + res
                .getResourceEntryName(resId));
        return resUri;
    }
}
