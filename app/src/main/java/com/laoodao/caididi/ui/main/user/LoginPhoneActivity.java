package com.laoodao.caididi.ui.main.user;

import android.content.Intent;
import android.os.Bundle;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.Login3rdResultEvent;
import com.laoodao.caididi.ui.main.fragment.LoginPhoneFragment;

import ezy.lite.util.FragmentUtil;

public class LoginPhoneActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_login);

        FragmentUtil.add(this, R.id.lyt_login_body, LoginPhoneFragment.class);
    }

    boolean mIsLoggedIn = false;

    @Override
    public void onResume() {
        super.onResume();
        if (mIsLoggedIn != Global.session().isLoggedIn()) {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getExtras() == null) {
            return;
        }
        RxBus.post(new Login3rdResultEvent(requestCode, resultCode, data));
    }
}
