package com.laoodao.caididi;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.laoodao.caididi.common.app.RouterActivity;

import org.json.JSONObject;

import ezy.lite.util.LogUtil;
import ezy.lite.util.NotifyUtil;

public class PushReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                String appid = bundle.getString("appid");
                String payload = new String(bundle.getByteArray("payload"));

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));


                if (payload == null) {
                    break;
                }

                int smallIcon = R.mipmap.ic_launcher;
                String ticker = "您有一条新通知";
                String title = "";
                String content = "";
                String url = "";
                try {
                    JSONObject data = new JSONObject(payload);
                    title = data.getString("title");
                    content = data.getString("content");
                    url = data.getString("url");
                } catch (Exception e) {

                }

                LogUtil.e("url:===================>>>>" + url);
                Intent p = new Intent(context, RouterActivity.class);
                p.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                p.setData(Uri.parse(url));
                PendingIntent pIntent = PendingIntent.getActivity(context, 1, p, PendingIntent.FLAG_UPDATE_CURRENT);

                //实例化工具类，并且调用接口
                NotifyUtil notify1 = new NotifyUtil(context, 1);
                notify1.notify_normal_singline(pIntent, smallIcon, ticker, title, content, true, true, false);

                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                Log.e("push", "cid = " + cid);
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 * 
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
}
