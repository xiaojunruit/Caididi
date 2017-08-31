package com.laoodao.caididi;


import android.content.Context;
import android.databinding.repacked.google.common.annotations.VisibleForTesting;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.text.TextUtils;

import com.laoodao.caididi.ui.main.WebViewActivity;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.activity.VegetablesPriceActivity;
import com.laoodao.caididi.ui.my.activity.AddReserveActivity;
import com.laoodao.caididi.ui.my.activity.BiddersActivity;
import com.laoodao.caididi.ui.my.activity.ChatActivity;
import com.laoodao.caididi.ui.my.activity.ContactsListActivity;
import com.laoodao.caididi.ui.my.activity.ExpertActivity;
import com.laoodao.caididi.ui.my.activity.FarmlandManagementActivity;
import com.laoodao.caididi.ui.my.activity.GreensRecordActivity;
import com.laoodao.caididi.ui.my.activity.InviteActivity;
import com.laoodao.caididi.ui.my.activity.MessageDetailActivity;
import com.laoodao.caididi.ui.my.activity.MyAskAnswerActivity;
import com.laoodao.caididi.ui.my.activity.MyQrcodeActivity;
import com.laoodao.caididi.ui.my.activity.MyUnAnswerActivity;
import com.laoodao.caididi.ui.my.activity.OrderDetailActivity;
import com.laoodao.caididi.ui.my.activity.ReserveActivity;
import com.laoodao.caididi.ui.my.activity.ReserveAfterActivity;
import com.laoodao.caididi.ui.my.activity.SaleRecordActivity;
import com.laoodao.caididi.ui.my.activity.SettingActivity;
import com.laoodao.caididi.ui.my.activity.ShengZiActivity;
import com.laoodao.caididi.ui.my.activity.UserInfoActivity;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;
import com.laoodao.caididi.ui.wenda.activity.MyAskActivity;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

public class Route {


    public static final Map<String, Object> map = new HashMap<>();
    public static final String SETTING = "caididi://setting";     //设置
    public static final String MY_ANSWER = "caididi://my/answer"; //我的问答
    public static final String MY_FARMLAND = "caididi://my/farmland"; //我的农田
    public static final String FARMING = "caididi://farming"; //农业资讯
    public static final String QRCODE = "caididi://my/qrCode"; //我的二维码
    public static final String MY_USER_INFO = "caididi://my/userInfo"; //用户资料
    public static final String SALES_RECORD = "caididi://my/salesRecord"; //蔬菜销售记录
    public static final String ANSWER_DETAIL = "caididi://answer/detail";
    public static final String SHENG_ZI = "caididi://my/shengZiRecord";
    public static final String RESERVER = "caididi://my/reserver";
    public static final String UN_ANSWER = "caididi://my/unAnswer";
    public static final String SU_RESERVE = "caididi://my/suReserve";
    public static final String RESERVER_LIST = "caididi://my/reserverList";
    public static final String ASK_QUESTIONS = "caididi://main/askQuestions";//快速提问
    public static final String HOME = "caididi://home";
    public static final String GREENS_PRICE = "caididi://greensPrice";//查询今日菜价
    public static final String INVITE = "caididi://my/invite";
    public static final String MESSAGE_DETAIL = "caididi://my/messageDetail";//消息详情
    public static final String ORDER_DETAIL = "caididi://my/orderDetail";//订单详情
    public static final String SETTLEMENT_DETAIL = "caididi://my/settlementDetail";//结算详情
    public static final String EXPERT_LIST = "caididi://my/expert_list";//专家列表
    public static final String CHAT_LIST="caididi://my/chat_list";//聊天列表
    public static final String SOIL_QUERY="caididi://my/soil_query";//土壤查询

    static {
        map.put(SETTING, SettingActivity.class);
        map.put(MY_ANSWER, MyAskAnswerActivity.class);
        map.put(MY_FARMLAND, FarmlandManagementActivity.class);
        map.put(MY_USER_INFO, UserInfoActivity.class);
        map.put(QRCODE, MyQrcodeActivity.class);
        map.put(SHENG_ZI, ShengZiActivity.class);
        map.put(SALES_RECORD, GreensRecordActivity.class);
        map.put(RESERVER_LIST, ReserveActivity.class);
        map.put(RESERVER, AddReserveActivity.class);
        map.put(UN_ANSWER, MyUnAnswerActivity.class);
        map.put(ASK_QUESTIONS, MyAskActivity.class);
        map.put(GREENS_PRICE, VegetablesPriceActivity.class);
        map.put(INVITE, InviteActivity.class);
        map.put(EXPERT_LIST, ExpertActivity.class);
        map.put(CHAT_LIST,ContactsListActivity.class);
        map.put(MESSAGE_DETAIL, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                String id = path.params.getValue("id");
                MessageDetailActivity.start(context, Integer.parseInt(id));
            }
        });
        map.put(ORDER_DETAIL, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                String id = path.params.getValue("id");
                String purchaseCode = path.params.getValue("purchaseCode");
                OrderDetailActivity.start(context, Integer.parseInt(id), purchaseCode);
            }
        });
        map.put(SETTLEMENT_DETAIL, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                String id = path.params.getValue("id");
                BiddersActivity.start(context, Integer.parseInt(id));
            }
        });
        map.put(HOME, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                MainActivity.start(context, 0);
            }
        });
        map.put(SU_RESERVE, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                String id = path.params.getValue("id");
                ReserveAfterActivity.start(context, id);
            }
        });
        map.put(FARMING, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                String id = path.params.getValue("id");
                boolean isInsect = Boolean.parseBoolean(path.params.getValue("isInsect"));
                String title = path.params.getValue("title");
                Bundle bundle = new Bundle();
                bundle.putInt("tab", 1);
                bundle.putString("id", id);
                bundle.putBoolean("isInsect", isInsect);
                bundle.putString("title", title);
                MainActivity.start(context, bundle);

            }
        });
        map.put(ANSWER_DETAIL, new RouteHandler() {
            @Override
            public void handle(Context context, Path path) {
                String id = path.params.getValue("id");
                if (!TextUtils.isEmpty(id))
                    AnswerDetailActivity.start(context, Integer.parseInt(id), false);
            }
        });
    }

    public static void go(Context context, String url) {
        go(context, url, "");
    }

    public static void go(Context context, String url, String title) {
        LogUtil.e("url = " + url);

        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (Exception e) {
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            WebViewActivity.start(context, "", url);
            return;
        }
        if (!(url.startsWith("caididi://"))) {
            return;
        }
        Path path = new Path(url);
        if (map.containsKey(path.route)) {
            Object handler = map.get(path.route);
            if (handler instanceof Class) {
                ContextUtil.startActivity(context, (Class) handler);
            } else if (handler instanceof RouteHandler) {
                ((RouteHandler) handler).handle(context, path);
            }
        }


    }

    static class Path {
        String route;
        String query;
        String hash;
        UrlQuerySanitizer params;

        public Path(String url) {

            int queryIndex = url.indexOf('?');
            int hashIndex = url.indexOf('#');

            query = parseQuery(url, queryIndex, hashIndex);
            route = parseRouteId(url, queryIndex, hashIndex);

            if (hashIndex >= 0) {
                hash = url.substring(hashIndex);
            }

            params = new UrlQuerySanitizer();
            params.setAllowUnregisteredParamaters(true);
            params.setUnregisteredParameterValueSanitizer(sDefaultSanitizer);
            params.parseQuery(query);

            LogUtil.e("url = " + url);
            LogUtil.e("route = " + route);
            LogUtil.e("query = " + query);
        }

        String parseRouteId(String url, int queryIndex, int hashIndex) {
            String route = url;
            if (queryIndex >= 0) {
                route = url.substring(0, queryIndex);
            } else if (hashIndex >= 0) {
                route = url.substring(0, hashIndex);
            }
            if (route.endsWith("/")) {
                return route.substring(0, route.length() - 1);
            }
            return route;
        }

        String parseQuery(String url, int queryIndex, int hashIndex) {
            String query;
            if (queryIndex >= 0 && hashIndex >= 0) {
                query = url.substring(queryIndex + 1, hashIndex);
            } else if (queryIndex >= 0) {
                query = url.substring(queryIndex + 1);
            } else {
                query = "";
            }
            return query;
        }

    }

    static UrlQuerySanitizer.ValueSanitizer sDefaultSanitizer = new UrlQuerySanitizer.ValueSanitizer() {
        @Override
        public String sanitize(String value) {
            return value;
        }
    };


    static class MainHandler implements RouteHandler {
        int mIndex;
        static List<String> types = Arrays.asList("all", "underway", "deposit", "invalid");

        public MainHandler(int index) {

//            boolean discover = Prefs.get("discover", false);
            mIndex = index;
//            if (!discover) {
//                if (mIndex >= 3) {
//                    mIndex = 3;
//                }
//            }
        }

        @Override
        public void handle(Context context, Path path) {
            Bundle bundle = new Bundle();
            if (path.params.hasParameter(Const.TYPE)) {
                String type = path.params.getValue(Const.TYPE);
                LogUtil.e("type = " + type + ", index = " + types.indexOf(type));
                bundle.putInt(Const.TYPE, types.indexOf(type));
            }
            bundle.putInt("tab", mIndex);
            MainActivity.start(context, bundle);
        }
    }

    interface RouteHandler {
        void handle(Context context, Path path);
    }

}
