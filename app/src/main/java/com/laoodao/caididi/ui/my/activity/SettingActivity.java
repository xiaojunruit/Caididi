package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivitySettingBinding;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.qiugou.activity.ConsultActivity;

import ezy.boost.update.UpdateManager;
import ezy.boost.update.UpdateStatus;
import ezy.lite.util.Cleaner;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.UI;

/**
 * Created by Administrator on 2016/12/30.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    ActivitySettingBinding binding;
    public static final int TAB_FIVE = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setListener(this);
        binding.setItem(Global.info());
        binding.txtClean.setValue(Cleaner.getTotalCacheSize(this));
        binding.txtCheckUpdate.setValue("v" + BuildConfig.VERSION_NAME);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.setItem(Global.info());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loginout:
                if (!Global.session().isLoggedIn()) {
                    return;
                }
                EMClient.getInstance().logout(true);
                API.user().logout().compose(transform()).subscribe();
                Global.session().logout(this);
                finish();


                MainActivity.start(this, TAB_FIVE);
                break;
            case R.id.txt_clean:
                Cleaner.cleanAllCache(this);
                binding.txtClean.setValue("0M");
                break;
            case R.id.btn_info:
                ContextUtil.startActivity(this, UserInfoActivity.class);
                break;
            case R.id.txt_check_update:
                final Context context = getApplicationContext();
                UpdateManager.update(this, (status, info) -> {
                    switch (status) {
                        case UpdateStatus.NoNetwork:
                            UI.showToast(context, "没有 wifi 网络");
                            break;
                        case UpdateStatus.Yes:
                            UI.showToast(context, "已经是最新版");
                            break;
                    }
                });
                break;
            case R.id.txt_bound_account:
                ContextUtil.startActivity(this, BindAccountActivity.class);
                break;
            case R.id.checkbox_push:
                initData(binding.checkboxPush.isChecked() ? 1 : 0);
                break;
            case R.id.txt_about:
                ContextUtil.startActivity(this, AboutActivity.class);
                break;
        }
    }

    private void initData(int isPush) {
        API.user().setPush(isPush).compose(transform()).subscribe();
    }
}
