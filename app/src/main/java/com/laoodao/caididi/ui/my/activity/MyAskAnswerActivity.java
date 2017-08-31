package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityMyAskAnswerBinding;
import com.laoodao.caididi.ui.wenda.activity.FollowCropActivity;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

/**
 * Created by Administrator on 2016/12/29.
 */

public class MyAskAnswerActivity extends BaseActivity implements View.OnClickListener {
    ActivityMyAskAnswerBinding mBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_ask_answer);
        mBinding.setListener(this);
        mBinding.setLogininfo(Global.info());
        mBinding.executePendingBindings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.txtCropNum.setText(String.valueOf(Global.info().attentionCrops));
        mBinding.txtFollowMy.setText(String.valueOf(Global.info().attentionMe));
        mBinding.txtMyFollow.setText(String.valueOf(Global.info().myAttention));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_problem:
                ContextUtil.startActivity(this,MyProblemActivity.class);
                break;
            case R.id.collection_problem:
                ContextUtil.startActivity(this, MyFavoriteActivity.class);
                break;
            case R.id.my_comments:
                ContextUtil.startActivity(this, MyCommentActivity.class);
                break;
            case R.id.my_answer:
                ContextUtil.startActivity(this,MyAnswerActivity.class);
                break;
            case R.id.want_know_answer:
                ContextUtil.startActivity(this, WantKnowAnswerActivity.class);
                break;
            case R.id.ll_follow_my:
                FollowPeopleActivity.start(this, Const.MY_FOLLOW);
                break;
            case R.id.ll_follow_people:
                FollowPeopleActivity.start(this, Const.FOLLOW_MY);
                break;
            case R.id.ll_follow_crops:
                FollowCropActivity.start(this, (ArrayList) Global.info().plantsList);
                break;
        }
    }
}
