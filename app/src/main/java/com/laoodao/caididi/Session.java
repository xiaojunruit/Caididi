package com.laoodao.caididi;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.util.Convert;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.LoginInfoChangedEvent;
import com.laoodao.caididi.retrofit.user.LoginInfo;

import ezy.lite.util.Hash;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Session {

    private Context _context;
    private String _token = "";
    private boolean _isLoggedIn = false;
    private LoginInfo _info;

    public Session(Context context) {
        _context = context;
        _info = new LoginInfo();
    }

    public boolean isLoggedIn() {
        return _isLoggedIn;
    }

    public String getToken() {
        return _token;
    }

    public String getNickname() {
        return _info.nickname;
    }

    public String getAccountId() {
        return _info.uid;
    }

    public int getUserId() {
        return _info.cid;
    }

    public LoginInfo getInfo() {
        return _info;
    }

    public void login() {

        String hash = Global.setting().getHash();
        int cid = Global.setting().getCid();

        if (hash.length() > 0 && cid > 0) {
            _token = Convert.toToken(cid, hash);
            _info = new LoginInfo();
            _info.cid = cid;
            _info.uid = Global.setting().getUid();
            _isLoggedIn = true;
        }

        if (!_isLoggedIn) {
            return;
        }
        API.user()
                .loginInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe(result -> {
                    if (result == null) {
                        return;
                    }
                    if (result.code == 401) {
                        logout();
                        return;
                    }
                    if (result.code == 0) {
                        update(result.data);
                        return;
                    }
                    if (!TextUtils.isEmpty(result.message)) {
                        UI.showToast(_context, result.message);
                    }
                });
    }

    public void login(@NonNull String password, @NonNull LoginInfo u) {

        String hash = Hash.md5(Hash.md5(password));
        _token = Convert.toToken(u.cid, hash);
        _info = u;

        LogUtil.e("===============>>>>0" + _token.length());
        if (_token.length() > 0) {
            _isLoggedIn = true;
        }
        Global.setting().setCid(_info.cid).setUid(_info.uid).setHash(hash);
        RxBus.post(new LoginInfoChangedEvent());
    }

    public void logout() {
        logout(null);
    }

    public void logout(Activity context) {
        _info = new LoginInfo();
        _token = "";
        _isLoggedIn = false;
        Global.setting().setCid(0).setUid("").setHash("");
        RxBus.post(new LoginInfoChangedEvent());
    }

    private static boolean _isUserInfoChanged = true;

    public static void markUserInfoChanged() {
        _isUserInfoChanged = true;
    }

    public boolean isUserInfoChanged() {
        return _isUserInfoChanged;
    }

    public void update(@NonNull LoginInfo info) {
        _info = info;
        _isUserInfoChanged = false;
    }


    public String getPushId() {
        return PushManager.getInstance().getClientid(_context);
    }


}