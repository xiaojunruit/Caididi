package com.laoodao.caididi;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.laoodao.caididi.receiver.CallReceiver;
import com.laoodao.caididi.utils.SharedPreferencesUtil;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ezy on 15-9-28.
 */
public class CaididiAppcation extends Application {

    private CallReceiver callReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        Global.init(this);
        SharedPreferencesUtil.init(this,
                getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
        GrowingIO.startWithConfiguration(this, new Configuration()
                .useID()
                .trackAllFragments()
                .setChannel(Global.getChannel()));

        //初始化环信sdk
        EMOptions options = new EMOptions();
        options.setGCMNumber(BuildConfig.GCM_NUMBER);
        EMClient.getInstance().init(this, options);
        EaseUI.getInstance().init(this, options);
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(2);
        EaseUI.getInstance().setAvatarOptions(avatarOptions);
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        registerReceiver(callReceiver, callFilter);
    }


}
