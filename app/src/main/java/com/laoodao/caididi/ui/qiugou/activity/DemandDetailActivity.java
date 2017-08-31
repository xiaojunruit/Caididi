package com.laoodao.caididi.ui.qiugou.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityDemandDetailBinding;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;

import ezy.lite.util.ContextUtil;

/**
 * Created by XiaoGe on 2017/2/11.
 */

public class DemandDetailActivity extends BaseActivity implements View.OnClickListener {
    private ActivityDemandDetailBinding mBinding;
    private int id;

    public static void start(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ContextUtil.startActivity(context, DemandDetailActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demand_detail);
        mBinding.setListener(this);
        id = getIntent().getIntExtra("id", 0);
        initData();
        mBinding.cbCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.session().isLoggedIn()) {
                    mBinding.cbCollection.setChecked(mBinding.cbCollection.isChecked() ? false : true);
                    ContextUtil.startActivity(v.getContext(), LoginPhoneActivity.class);
                    return;
                }
                if (mBinding.cbCollection.isChecked()) {
                    collected(1);
                } else {
                    collected(2);
                }
            }
        });

    }

    private void collected(int type) {
        API.user().buyCollection(id, type).compose(new API.Transformer<>(this)).subscribe(result -> {
            mBinding.cbCollection.setText("收藏 "+result.data.get("collect_total"));
        });
    }

    private void initData() {
        API.user().demandDetail(id).compose(transform()).subscribe(result -> {
            mBinding.setItem(result.data);
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_callphone:
                if (mBinding.getItem() != null && !TextUtils.isEmpty(mBinding.getItem().mobile)) {
                    ContextUtil.call(this, mBinding.getItem().mobile);
                }
                break;
            case R.id.txt_chat:
                ContextUtil.startActivity(this,ConsultActivity.class);
                break;
        }
    }
}
