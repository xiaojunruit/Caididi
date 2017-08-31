package com.laoodao.caididi.common.util.countdown;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.laoodao.caididi.R;


public class CodeCountDown extends CountDownTimer {
    final TextView txt;

    public CodeCountDown(TextView tv, long seconds) {
        super(seconds * 1000, 10);
        this.txt = tv;
    }
    public CodeCountDown(TextView tv) {
        this(tv, 60);
    }

    @Override
    public void onTick(long s) {
        txt.setText((int) (s / 1000) + "秒后重新获取");
        txt.setEnabled(false);
    }

    @Override
    public void onFinish() {
        txt.setText("发送验证码");
        txt.setEnabled(true);
    }
}