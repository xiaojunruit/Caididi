package com.laoodao.caididi.ui.my.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityChangeNicknameBinding;
import com.laoodao.caididi.databinding.ActivityChangeSignatureBinding;
import com.laoodao.caididi.validator.NicknameValidator;

import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

public class SignatureEditActivity extends BaseActivity {
    private ActivityChangeSignatureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_signature);
        binding.txtSignature.setText(Global.info().signature);


        binding.txtSignature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.txtNum.setText(editable.length()+"/20");
            }
        });
    }

    private void submitData(){
        final String signature = UI.toString(binding.txtSignature);
        API.user().signature(signature).compose(transform()).subscribe(response -> {
            Global.info().signature = signature;
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                submitData();
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
