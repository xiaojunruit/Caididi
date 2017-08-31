package com.laoodao.caididi.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.laoodao.caididi.event.Login3rdEvent;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.Login3rdResultEvent;

import ezy.lite.util.LogUtil;

public class WXEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.e("=====>" + ((getIntent() == null || getIntent().getExtras() == null) ? "" : getIntent().getExtras().toString()));

        RxBus.post(new Login3rdResultEvent(1999, -1, getIntent()));
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("=====>" + ((getIntent() == null || getIntent().getExtras() == null) ? "" : getIntent().getExtras().toString()));

        setIntent(intent);
        RxBus.post(new Login3rdResultEvent(1999, -1, intent));
        finish();
    }
}