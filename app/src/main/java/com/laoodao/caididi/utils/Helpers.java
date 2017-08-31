package com.laoodao.caididi.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import ezy.lite.util.IoUtil;

/**
 * Created by WORK on 2017/2/9.
 */

public class Helpers {
    static Gson gson = new Gson();

    public static <T> T fromGson(Context context, String file, Class<T> clazz) {
        try {
            String json = IoUtil.readString(context.getAssets().open(file));
            return gson.fromJson(json, clazz);
        } catch (IOException e) {

        }
        return null;
    }
}
