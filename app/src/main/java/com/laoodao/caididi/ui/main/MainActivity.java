package com.laoodao.caididi.ui.main;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.databinding.ActivityMainBinding;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.ui.main.fragment.AnswerFragment;
import com.laoodao.caididi.ui.main.fragment.FarmingFragment;
import com.laoodao.caididi.ui.main.fragment.HomeFragment;
import com.laoodao.caididi.ui.main.fragment.MyFragment;
import com.laoodao.caididi.ui.my.activity.ChatActivity;
import com.laoodao.caididi.ui.my.im.VideoCallActivity;
import com.laoodao.caididi.ui.my.im.VoiceCallActivity;
import com.laoodao.caididi.utils.PermissionUtil;
import com.laoodao.caididi.utils.SharedPreferencesUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.stat.StatService;

import java.util.List;

import ezy.boost.update.UpdateAgent;
import ezy.boost.update.UpdateManager;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.UI;
import ezy.widget.adapter.TabsAdapter;
import ezy.widget.view.IconTextButton;

import static com.hyphenate.easeui.utils.EaseUserUtils.getUserInfo;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private long _lastClickTime = 0;
    private EMMessageListener msgListener;

    public static void start(Context context, int tab) {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.TAB, tab);
        ContextUtil.startActivity(context, MainActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void start(Context context, Bundle bundle) {
        ContextUtil.startActivity(context, MainActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void updateTab(Intent intent) {
        int tab = intent.getIntExtra(Const.TAB, binding != null ? binding.tabs.getSelectedTabPosition() : 0);
        binding.pager.setCurrentItem(tab, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        updateTab(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        StatService.trackCustomEvent(this, "onCreate", "");
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()), 0);

        addTab(R.drawable.home_icon_one_selector, "首页");
        addTab(R.drawable.home_icon_two_selector, "资讯");
        addTab(R.drawable.home_icon_three_selector, "社区");
        addTab(R.drawable.home_icon_four_selector, "我的");

        final Fragment[] fragments = {
                new HomeFragment(),
                new FarmingFragment(),
                new AnswerFragment(),
                new MyFragment()};
        binding.pager.setAdapter(new TabsAdapter(getSupportFragmentManager(), fragments));
        binding.pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
        binding.tabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.pager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                KeyboardUtil.hide(binding.getRoot());
                binding.pager.setCurrentItem(tab.getPosition(), false);

            }

        });
        UpdateAgent.setChannel(Global.getChannel());
        UpdateAgent.setUpdateUrl(API.BASE_URL + "?act=check_update");
        UpdateAgent.setUserAgent(API.UA);
        UpdateManager.update(this);
        binding.pager.setCurrentItem(0, false);
        if (Global.session().isLoggedIn()) {
            initIMPwd();
        }
        setEaseUIProviders();
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
//                    DemoHelper.getInstance().getNotifier().onNewMsg(message);
                    EaseUI.getInstance().getNotifier().onNewMsg(message);
                }

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        PermissionUtil.externalStorage(new PermissionUtil.RequestPermissionListener() {
            @Override
            public void success() {

            }

            @Override
            public void failure() {
                Toast.makeText(MainActivity.this, "请求权限失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            }
        }, new RxPermissions(this));
//        RxBus.ofType(LoginInfoChangedEvent.class).compose(bindToLifecycle()).subscribe(event -> {
//
//        });
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
    }

    private void initIMPwd() {
        API.user().getIMPwd()
                .compose(transform())
                .subscribe(result -> {
                    SharedPreferencesUtil.getInstance().putString("im_pwd", result.data.get("emcode"));
                    loginHuanXin(result.data.get("emcode"));
                });
    }


    private void setEaseUIProviders() {
        //set notification options, will use default if you don't set it
        EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, MainActivity.this);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(getString(R.string.at_your_in_group), user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                } else {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(getString(R.string.at_your_in_group), message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                if (Global.isVideoCalling) {
                    intent = new Intent(MainActivity.this, VideoCallActivity.class);
                } else if (Global.isVoiceCalling) {
                    intent = new Intent(MainActivity.this, VoiceCallActivity.class);
                } else {
                    LoginInfo info = Global.info();
                    if (info == null) {
                        return intent;
                    }
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", Const.CHATTYPE_SINGLE);
                    intent.putExtra("avatar", "");
                    intent.putExtra("userAvatar", info.avatar);
                }
                return intent;
            }
        });
    }


    public void loginHuanXin(String pwd) {
        if (!EMClient.getInstance().isLoggedInBefore()) {
            LoginInfo info = Global.info();
            if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(info.username)) {
                EMClient.getInstance().login(info.username, pwd, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.e("main", "登录聊天服务器成功！");
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.e("main", "登录聊天服务器失败！code=" + code + "----" + message);
                    }
                });
            }
        }

    }

    private void addTab(int icon, String text) {
        ViewGroup vg = (ViewGroup) getLayoutInflater().inflate(R.layout.widget_tab_item, binding.tabs, false);
        IconTextButton view = (IconTextButton) vg.findViewById(R.id.button);
        view.setText(text);
        view.setGravity(Gravity.CENTER);
        view.setIconDrawable(getResources().getDrawable(icon));
        binding.tabs.addTab(binding.tabs.newTab().setCustomView(vg));
    }

    @Override
    public void onBackPressed() {
        if (isFastClick()) {
            super.onBackPressed();
            this.finishAffinity();
            return;
        }
        UI.showToast(this, "再按一次退出程序！");
    }

    private boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - _lastClickTime < 4000) {
            return true;
        }
        _lastClickTime = now;
        return false;
    }

}
