package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityAskbuyDetailBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiaoGe on 2017/2/11.
 */

public class AskBuyDetailActivity extends BaseActivity {
    ActivityAskbuyDetailBinding mBinding;
    List<String> bannerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_askbuy_detail);
        initData();
    }

    private void initData() {
        bannerList = new ArrayList<>();
        bannerList.add("http://c.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=48e14a30708da9774e7a8e2f8561d42f/c83d70cf3bc79f3dfaad6d33b9a1cd11728b2960.jpg");
        bannerList.add("http://f.hiphotos.baidu.com/zhidao/pic/item/a044ad345982b2b76d5abcc633adcbef77099bef.jpg");
        bannerList.add("http://a.hiphotos.baidu.com/lvpics/h=800/sign=bbda92bad50735fa8ef043b9ae500f9f/8601a18b87d6277f8eed029929381f30e824fc97.jpg");
        bannerList.add("http://e.hiphotos.baidu.com/zhidao/pic/item/1b4c510fd9f9d72a6c9a6427d62a2834349bbb09.jpg");
        mBinding.setPromo(bannerList);
    }
}
