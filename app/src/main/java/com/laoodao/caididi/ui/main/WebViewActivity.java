package com.laoodao.caididi.ui.main;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ezy.lite.util.ContextUtil;

public class WebViewActivity extends BaseActivity {

    public static void start(Context context, String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        bundle.putString("URL", url);
        ContextUtil.startActivity(context, WebViewActivity.class, bundle);
    }

    private WebView vWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        final String title = getIntent().getExtras().getString("TITLE");
        final String url = getIntent().getExtras().getString("URL");
        setTitle(title);
        View btnBack = (View) getToolbar().findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            if (vWeb.canGoBack()) {
                vWeb.goBack();
            } else {
                finish();
            }
        });
        final Map<String, String> headers = new HashMap<>();
        if (Global.session().isLoggedIn()) {
            try {
                headers.put("X-TOKEN", Global.session().getToken());
                headers.put("X-M", URLEncoder.encode("" + Global.session().getAccountId(), "UTF-8"));
            } catch (IOException e) {

            }
        }

        vWeb = new WebView(getApplicationContext());
        vWeb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) findViewById(R.id.content)).addView(vWeb, 0);


        vWeb.setWebViewClient(new WebViewClient() {

        });
        vWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String t) {
                super.onReceivedTitle(view, t);
                if (!TextUtils.isEmpty(t) && TextUtils.isEmpty(title)) {
                    setTitle(vWeb.getTitle());
                }
            }
        });
        vWeb.addJavascriptInterface(new JavascriptInterface(this), "caididi");

        WebSettings settings = vWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSaveFormData(true);


        settings.setDatabasePath(getDir("database", MODE_PRIVATE).getPath());
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(getFilesDir().getPath());
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(vWeb, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            vWeb.setWebContentsDebuggingEnabled(true);
        }
        vWeb.requestFocusFromTouch();


        vWeb.requestFocus();
        vWeb.loadUrl(url, headers);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (vWeb.canGoBack()) {
                vWeb.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class JavascriptInterface {
        private Context mContext;

        public JavascriptInterface(Context context) {
            this.mContext = context;
        }

    }
}
