package com.laoodao.caididi.ui.my.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseFragment;
import com.laoodao.caididi.ui.my.activity.ChatActivity;
import com.laoodao.caididi.ui.my.im.VideoCallActivity;
import com.laoodao.caididi.ui.my.im.VoiceCallActivity;

import java.io.File;

import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/8/23.
 */

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper{

    @Override
    protected void setUpView() {
        setChatFragmentHelper(this);
        super.setUpView();
    }

    @Override
    public void onSendVoice(String content) {
        ((ChatActivity) getActivity()).requestSendVoice(toFile(content));
    }

    @Override
    public void onSendText(String content) {
        ((ChatActivity) getActivity()).requestSendText(content);

    }

    @Override
    public void onSendImage(String content) {
        ((ChatActivity) getActivity()).requestSendImage(toFile(content));
    }

    private File toFile(String url) {
        File file = new File(url);
        return file;
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {

    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId){
            case Const.ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case Const.ITEM_VIDEO_CALL:
                startVideoCall();
                break;
        }
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }


    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            hideSoftKeyboard();
        }
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
//            Global.isAutoVoice=false;
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            hideSoftKeyboard();
        }
    }
}
