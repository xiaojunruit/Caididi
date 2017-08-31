package com.laoodao.caididi.ui.dialog;


import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;

import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2016/9/9.
 */

public class UpdatePasswordDialog {

    public static void passwordDialogCustomAttr(Activity activity) {
        NormalDialog dialog = new NormalDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#FFFFFF"))
                .cornerRadius(5)
                .content("修改密码成功")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#000000"))
                .dividerColor(Color.parseColor("#eeeeee"))
                .btnTextSize(15.5f)
                .btnNum(1)
                .btnText("确定")
                .btnTextColor(Color.parseColor("#ffac2d"))
                .btnPressColor(Color.parseColor("#FFFFFF"))
                .widthScale(0.85f)
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                Global.session().logout();
                MainActivity.start(activity,Const.TAB_USER);
                ContextUtil.startActivity(activity, LoginPhoneActivity.class);
                activity.finish();

            }
        });
    }
}
