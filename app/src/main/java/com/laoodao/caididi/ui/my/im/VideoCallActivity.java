/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.laoodao.caididi.ui.my.im;

import android.content.Intent;
import android.databinding.repacked.google.common.eventbus.EventBus;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMCallManager.EMCameraDataProcessor;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.easeui.ui.FastReplyDialog;
import com.hyphenate.easeui.ui.PicSelectDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.superrtc.sdk.VideoView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import ezy.lite.util.LogUtil;


public class VideoCallActivity extends CallActivity implements OnClickListener {

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;

    // 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
    protected EMCallSurfaceView localSurface;
    protected EMCallSurfaceView oppositeSurface;
    private int surfaceState = -1;

    private TextView callStateTextView;

    private LinearLayout comingBtnContainer;
    private LinearLayout refuseBtn;
    private ImageView answerBtn;
    private Button hangupBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private TextView nickTextView;
    private TextView nickTextView2;
    private Chronometer chronometer;
    private Chronometer chronometer2;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView monitorTextView;
    private TextView netwrokStatusVeiw;
    private LinearLayout llExcessive;
    private LinearLayout llConvertVoice;
    private ImageView btnRefuseCall2;
    private ImageView btnRefuseCall3;
    private ImageView ivAvatar;
    private TextView tvMsgReply;
    private Handler uiHandler;

    private boolean isInCalling;
    boolean isRecording = false;
    //    private Button recordBtn;
    private EMVideoCallHelper callHelper;
    private Button toggleVideoBtn;

    private BrightnessDataProcess dataProcessor = new BrightnessDataProcess();
    private FastReplyDialog mDialog;
    private FrameLayout flVoice;
    private LinearLayout llPhoneTime;
    private ImageView hangupBtn2;
    private ImageView muteImage2;
    private ImageView handsFreeImage2;

    // dynamic adjust brightness
    class BrightnessDataProcess implements EMCameraDataProcessor {
        byte yDelta = 0;

        synchronized void setYDelta(byte yDelta) {
            Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
            this.yDelta = yDelta;
        }

        // data size is width*height*2
        // the first width*height is Y, second part is UV
        // the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
        @Override
        public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final int rotateAngel) {
            int wh = width * height;
            for (int i = 0; i < wh; i++) {
                int d = (data[i] & 0xFF) + yDelta;
                d = d < 16 ? 16 : d;
                d = d > 235 ? 235 : d;
                data[i] = (byte) d;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.em_activity_video_call);

        Global.isVideoCalling = true;
        callType = 1;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
        refuseBtn = (LinearLayout) findViewById(R.id.btn_refuse_call);
        tvMsgReply = (TextView) findViewById(R.id.tv_msg_reply);
        answerBtn = (ImageView) findViewById(R.id.btn_answer_call);
        hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        hangupBtn2 = (ImageView) findViewById(R.id.btn_hangup_call2);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        btnRefuseCall3 = (ImageView) findViewById(R.id.btn_refuse_call3);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        muteImage2 = (ImageView) findViewById(R.id.iv_mute2);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        handsFreeImage2 = (ImageView) findViewById(R.id.iv_handsfree2);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        nickTextView2 = (TextView) findViewById(R.id.tv_nick2);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer2 = (Chronometer) findViewById(R.id.chronometer2);
        llConvertVoice = (LinearLayout) findViewById(R.id.ll_convert_voice);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        llExcessive = (LinearLayout) findViewById(R.id.ll_excessive);
        flVoice = (FrameLayout) findViewById(R.id.fl_voice);
        RelativeLayout btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        llPhoneTime = (LinearLayout) findViewById(R.id.ll_phone_time);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        monitorTextView = (TextView) findViewById(R.id.tv_call_monitor);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
        btnRefuseCall2 = (ImageView) findViewById(R.id.btn_refuse_call2);
//        recordBtn = (Button) findViewById(R.id.btn_record_video);
        LinearLayout switchCameraBtn = (LinearLayout) findViewById(R.id.btn_switch_camera);
        Button captureImageBtn = (Button) findViewById(R.id.btn_capture_image);
        SeekBar YDeltaSeekBar = (SeekBar) findViewById(R.id.seekbar_y_detal);
        hangupBtn2.setOnClickListener(this);
        tvMsgReply.setOnClickListener(this);
        llConvertVoice.setOnClickListener(this);
        btnRefuseCall3.setOnClickListener(this);
        btnRefuseCall2.setOnClickListener(this);
        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        muteImage2.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        handsFreeImage2.setOnClickListener(this);
        rootContainer.setOnClickListener(this);
//        recordBtn.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);
        captureImageBtn.setOnClickListener(this);

        YDeltaSeekBar.setOnSeekBarChangeListener(new YDeltaSeekBarListener());

        msgid = UUID.randomUUID().toString();
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");

        getAvatar();
        nickTextView.setText(username);
        nickTextView2.setText(username);

        // local surfaceview
        localSurface = (EMCallSurfaceView) findViewById(R.id.local_surface);
        localSurface.setOnClickListener(this);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        // remote surfaceview
        oppositeSurface = (EMCallSurfaceView) findViewById(R.id.opposite_surface);

        // set call state listener
        addCallStateListener();
        if (!isInComingCall) {// outgoing call
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            tvMsgReply.setVisibility(View.GONE);
//            hangupBtn.setVisibility(View.VISIBLE);
            btnRefuseCall3.setVisibility(View.VISIBLE);
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call

            callStateTextView.setText("Ringing");
            if (EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
            voiceContronlLayout.setVisibility(View.GONE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();

        EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);

    }


    private void getAvatar() {
        API.user().getChatAvatar(username)
                .compose(transform())
                .subscribe(result -> {
                    if (result.data.size() >= 1) {
                        String avatar = result.data.get(0).avatar;
                        Glide.with(this).load(avatar).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivAvatar);
                    }
                });
    }

    class YDeltaSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            dataProcessor.setYDelta((byte) (20.0f * (progress - 50) / 50.0f));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (surfaceState == 0) {
            surfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
        } else {
            surfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                switch (callState) {

                    case CONNECTING: // is connecting
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                callStateTextView.setText(R.string.Are_connected_to_each_other);
                            }

                        });
                        break;
                    case CONNECTED: // connected
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
//                            callStateTextView.setText(R.string.have_connected_with);
                            }

                        });
                        break;

                    case ACCEPTED: // call is accepted
                        surfaceState = 0;
                        handler.removeCallbacks(timeoutHangup);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null)
                                        soundPool.stop(streamID);
                                    EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                                } catch (Exception e) {
                                }
                                openSpeakerOn();
                                ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                        ? R.string.direct_call : R.string.relay_call);
                                handsFreeImage2.setImageResource(R.mipmap.ic_hands_free_su);

                                isHandsfreeState = true;
                                isInCalling = true;
                                llExcessive.setVisibility(View.GONE);
                                rootContainer.setVisibility(View.VISIBLE);
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer2.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                chronometer2.setBase(SystemClock.elapsedRealtime());
                                // call durations start
                                chronometer.start();
                                chronometer2.start();
                                nickTextView.setVisibility(View.INVISIBLE);
                                callStateTextView.setText(R.string.In_the_call);
//                            recordBtn.setVisibility(View.VISIBLE);
                                callingState = CallingState.NORMAL;
                                startMonitor();
                            }

                        });
                        break;
                    case NETWORK_DISCONNECTED:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.VISIBLE);
                                netwrokStatusVeiw.setText(R.string.network_unavailable);
                            }
                        });
                        break;
                    case NETWORK_UNSTABLE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.VISIBLE);
                                if (error == CallError.ERROR_NO_DATA) {
                                    netwrokStatusVeiw.setText("没有通话数据");
                                } else {
                                    netwrokStatusVeiw.setText("网络不稳定");
                                }
                            }
                        });
                        break;
                    case NETWORK_NORMAL:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                            }
                        });
                        break;
                    case VIDEO_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                                try {
                                    if (EMClient.getInstance().callManager().getCallState()!=CallState.VIDEO_PAUSE) {
                                        llExcessive.setVisibility(View.VISIBLE);
                                        rootContainer.setVisibility(View.GONE);
                                        callStateTextView.setVisibility(View.GONE);
                                        llPhoneTime.setVisibility(View.VISIBLE);
                                        flVoice.setVisibility(View.VISIBLE);
                                        tvMsgReply.setVisibility(View.GONE);
                                        comingBtnContainer.setVisibility(View.GONE);
                                        localSurface.setVisibility(View.GONE);
                                        EMClient.getInstance().callManager().pauseVideoTransfer();
                                    }
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    case VIDEO_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();

                            }
                        });
                        break;
                    case VOICE_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();

                            }
                        });
                        break;
                    case VOICE_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case DISCONNECTED: // call is disconnected
                        handler.removeCallbacks(timeoutHangup);
                        @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg() {
                                uiHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        removeCallStateListener();
                                        saveCallRecord();
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(1200);
                                        rootContainer.startAnimation(animation);
                                        finish();
                                    }

                                }, 200);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                chronometer2.stop();
                                callDruationText = chronometer.getText().toString();
                                String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String s2 = getResources().getString(R.string.Connection_failure);
                                String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                                String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                                String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                                String s6 = getResources().getString(R.string.hang_up);
                                String s7 = getResources().getString(R.string.The_other_is_hang_up);
                                String s8 = getResources().getString(R.string.did_not_answer);
                                String s9 = getResources().getString(R.string.Has_been_cancelled);
                                String s10 = getResources().getString(R.string.Refused);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUSED;
                                    callStateTextView.setText(s1);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(s2);
                                } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                    callingState = CallingState.OFFLINE;
                                    callStateTextView.setText(s3);
                                } else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(s4);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NO_RESPONSE;
                                    callStateTextView.setText(s5);
                                } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                                    callingState = CallingState.VERSION_NOT_SAME;
                                    callStateTextView.setText(R.string.call_version_inconsistent);
                                } else {
                                    if (isRefused) {
                                        callingState = CallingState.REFUSED;
                                        callStateTextView.setText(s10);
                                    } else if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                        } else {
                                            callStateTextView.setText(s7);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(s8);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCELLED;
                                                callStateTextView.setText(s9);
                                            } else {
                                                callStateTextView.setText(s6);
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(VideoCallActivity.this, callStateTextView.getText(), Toast.LENGTH_SHORT).show();
                                postDelayedCloseMsg();
                            }

                        });

                        break;

                    default:
                        break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    public void fastReplyDialog() {
        if (mDialog == null) {
            mDialog = new FastReplyDialog(this);
        }
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        mDialog.setOnContentListener(content -> {
            if (!TextUtils.isEmpty(content)) {
                EMMessage message = EMMessage.createTxtSendMessage(content, username);
                EMClient.getInstance().chatManager().sendMessage(message);
            }
            finish();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_msg_reply:
                fastReplyDialog();
                break;
            case R.id.ll_convert_voice:
                llExcessive.setVisibility(View.VISIBLE);
                rootContainer.setVisibility(View.GONE);
                callStateTextView.setVisibility(View.GONE);
                llPhoneTime.setVisibility(View.VISIBLE);
                flVoice.setVisibility(View.VISIBLE);
                tvMsgReply.setVisibility(View.GONE);
                comingBtnContainer.setVisibility(View.GONE);
                localSurface.setVisibility(View.GONE);
                try {
                    EMClient.getInstance().callManager().pauseVideoTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.local_surface:
                changeCallView();
                break;
            case R.id.btn_refuse_call3:
            case R.id.btn_refuse_call2:
            case R.id.btn_refuse_call: // decline the call
                isRefused = true;
                refuseBtn.setEnabled(false);
                btnRefuseCall2.setEnabled(false);
                btnRefuseCall3.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
                break;

            case R.id.btn_answer_call: // answer the call
                EMLog.d(TAG, "btn_answer_call clicked");
                llExcessive.setVisibility(View.GONE);
                rootContainer.setVisibility(View.VISIBLE);
                answerBtn.setEnabled(false);
                openSpeakerOn();
                if (ringtone != null)
                    ringtone.stop();

                callStateTextView.setText("answering...");
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
                handsFreeImage2.setImageResource(R.mipmap.ic_hands_free_su);
                isAnswered = true;
                isHandsfreeState = true;
                comingBtnContainer.setVisibility(View.INVISIBLE);
                tvMsgReply.setVisibility(View.GONE);
//                hangupBtn.setVisibility(View.VISIBLE);
                btnRefuseCall3.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.GONE);//这里修改过
                localSurface.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_hangup_call2:
            case R.id.btn_hangup_call: // hangup
                hangupBtn.setEnabled(false);
                hangupBtn2.setEnabled(false);
                chronometer.stop();
                chronometer2.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                if (isRecording) {
                    callHelper.stopVideoRecord();
                }
                EMLog.d(TAG, "btn_hangup_call");
                handler.sendEmptyMessage(MSG_CALL_END);
                break;
            case R.id.iv_mute2:
            case R.id.iv_mute: // mute
                if (isMuteState) {
                    // resume voice transfer
//                    muteImage.setImageResource(R.mipmap.ic_mute_un);
                    muteImage2.setImageResource(R.mipmap.ic_mute_un);
                    try {
                        EMClient.getInstance().callManager().resumeVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = false;
                } else {
                    // pause voice transfer
//                    muteImage.setImageResource(R.mipmap.ic_mute_su);
                    muteImage2.setImageResource(R.mipmap.ic_mute_su);
                    try {
                        EMClient.getInstance().callManager().pauseVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = true;
                }
                break;
            case R.id.iv_handsfree2:
            case R.id.iv_handsfree: // handsfree
                if (isHandsfreeState) {
                    // turn off speaker
                    handsFreeImage2.setImageResource(R.mipmap.ic_hands_free_un);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    handsFreeImage2.setImageResource(R.mipmap.ic_hands_free_su);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
        /*
        case R.id.btn_record_video: //record the video
            if(!isRecording){
//                callHelper.startVideoRecord(PathUtil.getInstance().getVideoPath().getAbsolutePath());
                callHelper.startVideoRecord("/sdcard/");
                EMLog.d(TAG, "startVideoRecord:" + PathUtil.getInstance().getVideoPath().getAbsolutePath());
                isRecording = true;
                recordBtn.setText(R.string.stop_record);
            }else{
                String filepath = callHelper.stopVideoRecord();
                isRecording = false;
                recordBtn.setText(R.string.recording_video);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.record_finish_toast), filepath), Toast.LENGTH_LONG).show();
            }
            break;
        */
            case R.id.root_layout:
                if (callingState == CallingState.NORMAL) {
                    if (bottomContainer.getVisibility() == View.VISIBLE) {
                        bottomContainer.setVisibility(View.GONE);
                        topContainer.setVisibility(View.GONE);
                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

                    } else {
                        bottomContainer.setVisibility(View.VISIBLE);
                        topContainer.setVisibility(View.GONE);//修改过
                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
                    }
                }
                break;
            case R.id.btn_switch_camera: //switch camera
                handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
                break;
            case R.id.btn_capture_image:
                DateFormat df = new DateFormat();
                Date d = new Date();
                File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                final String filename = storage.getAbsolutePath() + "/" + df.format("MM-dd-yy--h-mm-ss", d) + ".jpg";
                EMClient.getInstance().callManager().getVideoCallHelper().takePicture(filename);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoCallActivity.this, "saved image to:" + filename, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Global.isVideoCalling = false;
        stopMonitor();
        if (isRecording) {
            callHelper.stopVideoRecord();
            isRecording = false;
        }
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
        oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        super.onBackPressed();
    }

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor() {
        monitor = true;
        new Thread(new Runnable() {
            public void run() {
                while (monitor) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            monitorTextView.setText("WidthxHeight：" + callHelper.getVideoWidth() + "x" + callHelper.getVideoHeight()
                                    + "\nDelay：" + callHelper.getVideoLatency()
                                    + "\nFramerate：" + callHelper.getVideoFrameRate()
                                    + "\nLost：" + callHelper.getVideoLostRate()
                                    + "\nLocalBitrate：" + callHelper.getLocalBitrate()
                                    + "\nRemoteBitrate：" + callHelper.getRemoteBitrate());

                            ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }

    void stopMonitor() {
        monitor = false;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

}
