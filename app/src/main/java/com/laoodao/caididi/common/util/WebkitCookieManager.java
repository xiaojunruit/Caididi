package com.laoodao.caididi.common.util;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WebkitCookieManager extends CookieManager {
    private android.webkit.CookieManager wcm;

    public WebkitCookieManager() {
        this(null);
    }

    public WebkitCookieManager(CookiePolicy policy) {
        super(null, policy);

        this.wcm = android.webkit.CookieManager.getInstance();
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        if ((uri == null) || (responseHeaders == null)) {
            return;
        }
        String url = uri.toString();

        for (String key : responseHeaders.keySet()) {
            if ((key == null) || !(key.equalsIgnoreCase("Set-Cookie2") || key.equalsIgnoreCase("Set-Cookie"))) {
                continue;
            }
            for (String headerValue : responseHeaders.get(key)) {
                this.wcm.setCookie(url, headerValue);
            }
        }
    }

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        if ((uri == null) || (requestHeaders == null)) {
            throw new IllegalArgumentException("Argument is null");
        }
        String url = uri.toString();
        Map<String, List<String>> res = new java.util.HashMap<String, List<String>>();

        String cookie = this.wcm.getCookie(url);
        if (cookie != null) {
            res.put("Cookie", Arrays.asList(cookie));
        }
        return res;
    }

    @Override
    public CookieStore getCookieStore() {
        throw new UnsupportedOperationException();
    }
}