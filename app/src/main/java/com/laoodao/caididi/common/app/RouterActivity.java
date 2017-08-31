package com.laoodao.caididi.common.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.laoodao.caididi.Route;

public class RouterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            Route.go(this, uri.toString());
        }
        finish();
    }
}