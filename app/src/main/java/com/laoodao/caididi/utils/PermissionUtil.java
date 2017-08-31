package com.laoodao.caididi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.tbruyelle.rxpermissions.RxPermissions;

public class PermissionUtil {
    public static final String TAG = "Permission";


    public interface RequestPermissionListener {
        void success();

        void failure();
    }


    /**
     * 请求摄像头权限
     */
    public static void launchCamera(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
        //先确保是否已经申请过摄像头，和写入外部存储的权限
        boolean isPermissionsGranted =
                        rxPermissions
                                .isGranted(Manifest.permission.CAMERA);

        if (isPermissionsGranted) {//已经申请过，直接执行操作
            requestPermission.success();
        } else {//没有申请过，则申请
            rxPermissions.request(Manifest.permission.CAMERA)
                    .subscribe(
                            granted -> {
                                if (granted) {
                                    requestPermission.success();
                                } else {
                                    requestPermission.failure();
                                }
                            },
                            throwable -> {
                                requestPermission.failure();
                            }
                    );
        }
    }

    public static boolean judgeLocation(RxPermissions rxPermissions) {
        boolean isLocation = rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION) && rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION);
        return isLocation;
    }

    /**
     * 请求外部存储的权限
     */
    public static void externalStorage(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
        //先确保是否已经申请过摄像头，和写入外部存储的权限
        boolean isPermissionsGranted = rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && rxPermissions.isGranted(Manifest.permission.CALL_PHONE)
                && rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                && rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        &&rxPermissions.isGranted(Manifest.permission.CAMERA);

        if (isPermissionsGranted) {//已经申请过，直接执行操作
            requestPermission.success();
        } else {//没有申请过，则申请
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA)
                    .subscribe(
                            granted -> {
                                if (granted) {
                                    requestPermission.success();
                                } else {
                                    requestPermission.failure();
                                }
                            },
                            throwable -> {
                                requestPermission.failure();
                            }
                    );
        }
    }


    /**
     * 请求麦克风
     */
    public static void recordAudio(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
        //先确保是否已经申请过摄像头，和写入外部存储的权限
        boolean isPermissionsGranted = rxPermissions.isGranted(Manifest.permission.RECORD_AUDIO);

        if (isPermissionsGranted) {//已经申请过，直接执行操作
            requestPermission.success();
        } else {//没有申请过，则申请
            rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                    .subscribe(
                            granted -> {
                                if (granted) {
                                    requestPermission.success();
                                } else {
                                    requestPermission.failure();
                                }
                            },
                            throwable -> {
                                requestPermission.failure();
                            }
                    );
        }
    }


    /**
     * 申请位置权限
     *
     * @param requestPermission
     * @param rxPermissions
     */
    public static void sendLocation(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
        //先确保是否已经申请过摄像头，和写入外部存储的权限
        boolean isPermissionsGranted = rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION) && rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (isPermissionsGranted) {//已经申请过，直接执行操作
            requestPermission.success();
        } else {//没有申请过，则申请
            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(
                            granted -> {
                                if (granted) {
                                    requestPermission.success();
                                } else {
                                    requestPermission.failure();
                                }
                            },
                            throwable -> {
                                requestPermission.failure();
                            }
                    );
        }
    }

    /**
     * 请求发送短信权限
     */
    public static void sendSms(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
//先确保是否已经申请过权限
//        boolean isPermissionsGranted =
//                rxPermissions
//                        .isGranted(Manifest.permission.SEND_SMS);
//
//        if (isPermissionsGranted) {//已经申请过，直接执行操作
//            requestPermission.onRequestPermissionSuccess();
//        } else {//没有申请过，则申请
//            rxPermissions
//                    .request(Manifest.permission.SEND_SMS)
//                    .subscribe(new ErrorHandleSubscriber<Boolean>(errorHandler) {
//                        @Override
//                        public void onNext(Boolean granted) {
//                            if (granted) {
//                                Timber.tag(TAG).d("request SEND_SMS success");
//                                requestPermission.onRequestPermissionSuccess();
//                            } else {
//                                view.showMessage("request permissons failure");
//                            }
//                        }
//                    });
//        }
    }


    /**
     * 请求打电话权限
     */
    public static void callPhone(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
//先确保是否已经申请过权限
        boolean isPermissionsGranted = rxPermissions.isGranted(Manifest.permission.CALL_PHONE);
//
        if (isPermissionsGranted) {//已经申请过，直接执行操作
            requestPermission.success();
        } else {//没有申请过，则申请
            rxPermissions
                    .request(Manifest.permission.CALL_PHONE)
                    .subscribe(
                            aBoolean -> {
                                if (aBoolean) {
                                    requestPermission.success();
                                } else {
                                    requestPermission.failure();
                                }
                            }, throwable -> {
                                requestPermission.failure();
                            }
                    );
        }
    }


    /**
     * 请求获取手机状态的权限
     */
    public static void readPhonestate(final RequestPermissionListener requestPermission, RxPermissions rxPermissions) {
//先确保是否已经申请过权限
//        boolean isPermissionsGranted =
//                rxPermissions
//                        .isGranted(Manifest.permission.READ_PHONE_STATE);
//
//        if (isPermissionsGranted) {//已经申请过，直接执行操作
//            requestPermission.onRequestPermissionSuccess();
//        } else {//没有申请过，则申请
//            rxPermissions
//                    .request(Manifest.permission.READ_PHONE_STATE)
//                    .subscribe(new ErrorHandleSubscriber<Boolean>(errorHandler) {
//                        @Override
//                        public void onNext(Boolean granted) {
//                            if (granted) {
//                                Timber.tag(TAG).d("request SEND_SMS success");
//                                requestPermission.onRequestPermissionSuccess();
//                            } else {
//                                Timber.tag(TAG).e("request permissons failure");
//                            }
//                        }
//                    });
//        }
    }


    public static void startActionAetailsSettings(Activity activity) {
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        activity.startActivity(intent);
    }

}

