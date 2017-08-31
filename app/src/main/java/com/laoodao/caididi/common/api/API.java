package com.laoodao.caididi.common.api;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.common.app.BaseFragment;
import com.laoodao.caididi.retrofit.APIMain;
import com.laoodao.caididi.retrofit.APIUser;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.JointLoginActivity;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ezy.lite.exception.SystemException;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class API {
    public static final String BASE_URL = BuildConfig.API_BASE_URL;
    public static String UA = "caididi/" + BuildConfig.VERSION_NAME +
            " (Linux; U; Android " +
            Build.VERSION.RELEASE + "; " +
            //            Build.MODEL + "; " +
            Locale.getDefault().getLanguage() + "-" +
            Locale.getDefault().getCountry().toLowerCase() + ")";


    static OkHttpClient _client;

    static Retrofit _retrofit;


    static {

        _client = new OkHttpClient.Builder()

                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {

                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response response = chain.proceed(chain.request());
                        if (!response.headers("Set-Cookie").isEmpty()) {
                            HashSet<String> cookies = new HashSet<>();
                            for (String header : response.headers("Set-Cookie")) {
                                cookies.add(header);
                                Log.i("API", "get cookies:" + header);
                            }
                            Global.setting().setCookies(cookies);
                        }
                        return response;
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        builder.addHeader("User-Agent", UA);
                        builder.addHeader("Accept", "application/json; q=0.5");
                        builder.addHeader("X-DEVICEID", Device.deviceId);
                        if (!TextUtils.isEmpty(Global.session().getPushId())) {
                            builder.addHeader("X-CID", Global.session().getPushId());
                        }
                        if (Global.session().isLoggedIn()) {
                            builder.addHeader("X-TOKEN", Global.session().getToken());
                            builder.addHeader("X-M", URLEncoder.encode(Global.session().getAccountId(), "UTF-8"));
                        }
                        //          builder.addHeader("X-TIMESTAMP", "" + System.currentTimeMillis()).build();
                        for (String cookie : Global.setting().getCookies()) {
                            builder.addHeader("Cookie", cookie);
                            Log.i("API", "init cookies:" + cookie);
                        }
                        return chain.proceed(builder.build());
                    }
                })
                //.addInterceptor(new LoggingInterceptor())
                .build();

        _retrofit = new Retrofit.Builder()
                .client(_client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    static APIMain _main;

    public static APIMain main() {
        if (_main == null) {
            _main = _retrofit.create(APIMain.class);
        }
        return _main;
    }

    static APIUser _user;

    public static APIUser user() {
        if (_user == null) {
            _user = _retrofit.create(APIUser.class);
        }
        return _user;
    }


    public static boolean doCheck(Context context, Response result) {
        if (result == null) {
            UI.showToast(context, "网络出错!");
            return false;
        }

        if (!TextUtils.isEmpty(result.message)) {
            UI.showToast(context, result.message);
        }

        if (result.code == 401) {
            if (!Global.session().isLoggedIn()) {
                ContextUtil.startActivity(context, LoginPhoneActivity.class);
            }
        }

        return result.code == 0;
    }

    public static class Transformer<T extends Response> implements Observable.Transformer<T, T> {
        Observable.Transformer<T, T> mLifecycle;

        Context mContext;
        boolean mCheck;

        OnErrorListener onErrorListener;

        public Transformer(BaseFragment fragment) {
            mLifecycle = fragment.bindToLifecycle();
            mContext = fragment.getActivity();
        }

        public Transformer(Context context) {
            mLifecycle = resolveProvider(context).bindToLifecycle();
            mContext = context;
        }

        public Transformer(Context context, ActivityEvent event) {
            mLifecycle = resolveProvider(context).bindUntilEvent(event);
            mContext = context;
        }

        public Transformer(View view) {
            mLifecycle = RxLifecycle.bind(RxView.detaches(view).subscribeOn(AndroidSchedulers.mainThread()));
            mContext = view.getContext();

        }

        public Transformer<T> check() {
            mCheck = true;
            return this;
        }

        public Transformer<T> error(OnErrorListener listener) {
            onErrorListener = listener;
            return this;
        }

        @Override
        public Observable<T> call(Observable<T> o) {
            o = o.compose(mLifecycle).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            if (mCheck) {
                LogUtil.e("check==================>>>" + mCheck);
                o = o.filter(result -> doCheck(mContext, result));
            }
            return o.onErrorResumeNext(throwable -> doCatch(mContext, throwable));
        }

        private boolean doCheck(Context context, Response result) {


            LogUtil.e("========>>>r" + result.message);
            if (result == null) {
                UI.showToast(context, "网络出错!");
                return false;
            }
            if (!TextUtils.isEmpty(result.message)) {
                UI.showToast(context, result.message);
            }
            if (result.code == 0) {
                return true;
            }
            if (result.code == 401) {
                Global.session().logout();
                ContextUtil.startActivity(context, LoginPhoneActivity.class);
                return false;
            }


            LogUtil.e("message!=null");
            return false;
        }

        private <T> Observable<T> doCatch(Context context, Throwable throwable) {
            if (onErrorListener != null) {
                onErrorListener.onError(context, throwable);
            }
            Log.e("throwable", throwable.toString());
            throwable.printStackTrace();

            return Observable.empty();
        }

        private LifecycleProvider resolveProvider(Context context) {
            while (true) {
                if (context instanceof LifecycleProvider) {
                    return (LifecycleProvider) context;
                } else if (context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                } else {
                    throw SystemException.wrap(new Exception("api request need ActivityLifecycleProvider! [" + context + "]"));
                }
            }
        }
    }

    public interface OnErrorListener {
        void onError(Context context, Throwable throwable);
    }
}