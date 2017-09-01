package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityConsultBinding;
import com.laoodao.caididi.databinding.ActivityContactsListBinding;
import com.laoodao.caididi.retrofit.user.ChatAvatar;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.ui.my.adapter.ContactsAdapter;
import com.laoodao.caididi.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ezy.lite.util.LogUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/8/22.
 */

public class ContactsListActivity extends BaseActivity {

    protected ContactsAdapter adapter;
    protected List<EMConversation> conversations = new ArrayList<EMConversation>();
    private ActivityContactsListBinding mBinding;
    private EMMessageListener msgListener;
    private String uids = "";
    private List<ChatAvatar> mAvatarData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contacts_list);
        if (!EMClient.getInstance().isLoggedInBefore()){
            Toast.makeText(this, "登陆失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mBinding.loading.showLoading();
        init();
    }

    private void init() {
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        conversations.clear();
        conversations.addAll(loadConversationList());
        mBinding.loading.showContent();
        mBinding.llEmpty.setVisibility(conversations.size() < 1 ? View.VISIBLE : View.GONE);
        getAvatarData();
        mBinding.etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (content.length() > 0) {
                    adapter.getFilter().filter(content);
                } else {
                    adapter.setData(conversations, mAvatarData);
                }
            }
        });
    }

    private void getAvatarData() {
        for (int i = 0; i < conversations.size(); i++) {
            uids += conversations.get(i).conversationId() + ",";
        }
        if (conversations.size() < 1) {
            registerListener();
        } else {
            API.user().getChatAvatar(uids)
                    .compose(transform())
                    .subscribe(result -> {
                        this.mAvatarData.clear();
                        this.mAvatarData.addAll(result.data);
                        chatListener();
                    });
        }
    }

    private void chatListener() {
        adapter = new ContactsAdapter(this, conversations, mAvatarData);
        adapter.setOnDelListener(count -> {
            mBinding.llEmpty.setVisibility(count < 1 ? View.VISIBLE : View.GONE);
        });
        mBinding.recyclerview.setAdapter(adapter);
        if (msgListener == null) {
            registerListener();
        }
    }


    private void registerListener() {
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
//                for (EMMessage message : messages) {
//                    DemoHelper.getInstance().getNotifier().onNewMsg(message);
//                    EaseUI.getInstance().getNotifier().onNewMsg(message);
//                }
                //收到消息
                refresh();
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
                refresh();
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    private void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conversations.clear();
                conversations.addAll(loadConversationList());
                mBinding.llEmpty.setVisibility(conversations.size()<1?View.VISIBLE:View.GONE);
                adapter = new ContactsAdapter(ContactsListActivity.this, conversations, mAvatarData);
                adapter.setOnDelListener(count -> {
                    mBinding.llEmpty.setVisibility(count < 1 ? View.VISIBLE : View.GONE);
                });
                if (adapter != null && mBinding.recyclerview != null) {
                    mBinding.recyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
    }

    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
