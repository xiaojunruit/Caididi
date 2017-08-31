package com.laoodao.caididi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.laoodao.caididi.retrofit.main.Location;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.stat.StatConfig;

import java.util.HashSet;
import java.util.List;

import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;
import ezy.lite.util.Packer;
import ezy.lite.util.Prefs;

/**
 * Created by ezy on 15-9-29.
 */
public final class Global {

    private static Session _session;

    public static Session session() {
        return _session;
    }

    public static LoginInfo info() {
        return _session.getInfo();
    }

    private static LocalSetting _setting;

    public static LocalSetting setting() {
        return _setting;
    }

    private static Context _context;

    public static Context context() {
        return _context;
    }

    public static String getPushId() {
        return PushManager.getInstance().getClientid(_context);
    }

    public static boolean isVoiceCalling;
    public static boolean isVideoCalling;
//    public static boolean isAutoVoice;


    public static String getChannel() {
        return Packer.getMarket(_context, BuildConfig.FLAVOR);
    }

    public static void init(Context context) {
        if (_context != null) {
            return;
        }
        final Context ctx = context.getApplicationContext();
        _context = ctx;
        _session = new Session(ctx);
        _setting = LocalSetting.with(ctx);
        Prefs.init(ctx);
        if (_setting.getCookies() == null) {
            _setting.setCookies(new HashSet<>());
        }
        if (_setting.getKeywords() == null) {
            _setting.setKeywords(new HashSet<>());
        }

        Device.init(ctx);
        FlowManager.init(new FlowConfig.Builder(ctx).build()); // flowdb


        // qqmta
        StatConfig.setAppKey(BuildConfig.STATS_APP_KEY);
        StatConfig.setInstallChannel(getChannel());
        StatConfig.setSessionTimoutMillis(30000);
        StatConfig.setAutoExceptionCaught(false);
        StatConfig.setDebugEnable(BuildConfig.DEBUG);
        StatConfig.setEnableStatService(true);

        // bugly
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(ctx);
        strategy.setAppChannel(getChannel());
        strategy.setAppVersion(BuildConfig.VERSION_NAME);
        strategy.setDeviceID(Device.deviceId);
        CrashReport.initCrashReport(ctx, BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy);

        PushManager.getInstance().initialize(ctx);
        setPushTag("app.init", getChannel(), BuildConfig.VERSION_NAME);
        _session.login();
        SDKInitializer.initialize(ctx);
        locator = new LocationClient(ctx);     //声明LocationClient类
        locator.registerLocationListener(locationListener);    //注册监听函数
        initLocation();
        locator.start();
        locator.requestLocation();
    }

    static Location mLocation = new Location("未定位城市");

    public static void setPushTag(String sn, String... tags) {
        Tag[] items = new Tag[tags.length];
        for (int i = 0; i < tags.length; i++) {
            Tag t = new Tag();
            //name 字段只支持：中文、英文字母（大小写）、数字、除英文逗号以外的其他特殊符号, 具体请看代码示例
            t.setName(tags[i]);
            items[i] = t;
        }
        PushManager.getInstance().setTag(_context, items, sn);

    }

    public static LocationClient locator = null;
    public static BDLocationListener locationListener = new LocationListener();

    public static Location getLocation() {
        return mLocation;
    }

    public static void setLocation(BDLocation loc) {
        LogUtil.e("loc" + loc.getCity());
        String cityCode = loc.getAddress().cityCode;
        if (!TextUtils.isEmpty(cityCode)) {
            mLocation.address = loc.getAddrStr();
            mLocation.lat = loc.getLatitude() + "";
            mLocation.lon = loc.getLongitude() + "";
            mLocation.city = loc.getCity();
            mLocation.isSuccess = true;
        } else {
            mLocation.isSuccess = false;
            mLocation.address = "";
            mLocation.lat = "";
            mLocation.lon = "";
            mLocation.city = "";
        }
    }


    private static void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 200 * 1000;
        option.setScanSpan(span);       //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);  //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);        //可选，默认false,设置是否使用gps
        option.setLocationNotify(false); //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true); //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);  //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);     //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);  //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);     //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locator.setLocOption(option);
    }


    public static class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                LogUtil.e("----------->>>>>>>>");
                return;
            }
            Global.setLocation(location);
            Global.locator.unRegisterLocationListener(this);
            Global.locator.stop();
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("ezy-location", sb.toString());
        }

    }
}
