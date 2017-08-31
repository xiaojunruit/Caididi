package com.laoodao.caididi.utils;


import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.wenda.activity.FollowCropActivity;

/**
 * Created by Administrator on 2016/9/9.
 */

public class NormalDialogUtil {

    public static void NormalDialogCustomAttr(Activity activity) {
        NormalDialog dialog = new NormalDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#FFFFFF"))
                .cornerRadius(5)
                .content("为了更好的为您服务请您完善信息")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#000000"))
                .dividerColor(Color.parseColor("#eeeeee"))
                .btnTextSize(15.5f)
                .btnNum(1)
                .btnText("取消", "确定")
                .btnNum(2)
                .btnTextColor(Color.parseColor("#ffac2d"))
                .btnPressColor(Color.parseColor("#FFFFFF"))
                .widthScale(0.85f)
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {

            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                FollowCropActivity.start(activity, null);
                dialog.dismiss();
            }
        });
    }
}
