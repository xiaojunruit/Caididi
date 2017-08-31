package com.laoodao.caididi.ui.my.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityChangeNicknameBinding;
import com.laoodao.caididi.validator.NicknameValidator;

import ezy.lite.util.UI;

public class NicknameEditActivity extends BaseActivity {
    private ActivityChangeNicknameBinding binding;

    NicknameValidator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_nickname);
        binding.txtNickname.setText(Global.info().nickname);

        mValidator = new NicknameValidator(binding.txtNickname).succeeded(() -> {
            final String nickname = UI.toString(binding.txtNickname);
            API.user().updateNickName(nickname).compose(transform()).subscribe(response -> {
                Global.info().nickname = nickname;
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_save:
            mValidator.validate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }
}
