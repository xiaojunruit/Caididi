package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.ProgressOperator;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityNewFarmlandBinding;
import com.laoodao.caididi.event.AskEvent;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.retrofit.user.FarmlandDetail;
import com.laoodao.caididi.ui.main.SplashActivity;
import com.laoodao.caididi.ui.wenda.activity.MyAskActivity;
import com.laoodao.caididi.ui.widget.citySelector.activity.AreaSelectorActivity;
import com.laoodao.caididi.ui.widget.citySelector.activity.CitySelectorActivity;
import com.laoodao.caididi.ui.widget.imageSelect.model.LocalMedia;
import com.laoodao.caididi.ui.widget.imageSelect.view.ImagePreviewActivity;
import com.laoodao.caididi.ui.widget.imageSelect.view.ImageSelectorActivity;
import com.laoodao.caididi.ui.widget.wheelPicker.OptionPicker;
import com.laoodao.caididi.utils.Helpers;
import com.laoodao.caididi.utils.PermissionUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by XiaoGe on 2017/2/8.
 * 新建农田
 */

public class AddFarmlandActivity extends BaseActivity implements View.OnClickListener {
    private ActivityNewFarmlandBinding binding;
    private ArrayList<LocalMedia> selImageList = new ArrayList<>(); //当前选择的所有图片

    private City mProvince;
    private City mCity;
    private City mCounty;
    private City mXiang;
    private int mId;

    public static void start(Context context, FarmlandDetail fd) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", fd);
        ContextUtil.startActivity(context, AddFarmlandActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_farmland);
        binding.setOnClick(this);
        FarmlandDetail fd = (FarmlandDetail) getIntent().getSerializableExtra("data");
        if (fd != null) {
            binding.setItem(fd);
            mProvince = fd.province;
            mCity = fd.city;
            mCounty = fd.county;
            mXiang = fd.township;
            mId = fd.id;
            setTitle("编辑农田");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooes_image:
                PermissionUtil.externalStorage(new PermissionUtil.RequestPermissionListener() {
                    @Override
                    public void success() {
                        ImageSelectorActivity.start(AddFarmlandActivity.this, 3 - selImageList.size(), 1, true, true, false);
                    }

                    @Override
                    public void failure() {
                        Toast.makeText(AddFarmlandActivity.this, "请求权限失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
                    }
                }, new RxPermissions(this));

                break;
            case R.id.btn_chooes_address:
                AreaSelectorActivity.start(this);
                break;

            case R.id.btn_commit:
                commit();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //选择图片返回的数据
            if (requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
                ArrayList<LocalMedia> images = (ArrayList<LocalMedia>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                LogUtil.e("image" + images.size());

                addImages(images);
            } else if (requestCode == AreaSelectorActivity.REQUEST_AREA) {
                mProvince = (City) data.getSerializableExtra("province");
                mCity = (City) data.getSerializableExtra("city");
                mCounty = (City) data.getSerializableExtra("county");
                mXiang = (City) data.getSerializableExtra("xiang");
                String address = "";
                if (mProvince != null) {
                    address += mProvince.n;
                }
                if (mCity != null) {
                    address += mCity.n;
                }
                if (mCounty != null) {
                    address += mCounty.n;
                }
                if (mXiang != null) {
                    address += mXiang.n;
                }
                binding.tvAddress.setText(address);
            } else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                ArrayList<LocalMedia> images = (ArrayList<LocalMedia>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                selImageList.clear();
                addImages(images);
            }
        }
    }

    private void addImages(ArrayList<LocalMedia> images) {
        selImageList.addAll(images);
        binding.imageContent.removeAllViews();
        binding.chooesImage.setVisibility(selImageList.size() >= 3 ? View.GONE : View.VISIBLE);
        for (int i = 0; i < selImageList.size(); i++) {
            ImageView imageView = new ImageView(this);
            int width = (binding.llImageParent.getWidth() - Device.dp2px(80) - Device.dp2px(30)) / 3;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            params.rightMargin = Device.dp2px(10);
            imageView.setLayoutParams(params);
            Glide.with(this).load(selImageList.get(i).getPath()).thumbnail(0.5f).into(imageView);
            binding.imageContent.addView(imageView);
            imgClick(imageView, i);
        }
    }


    private void imgClick(ImageView imageView, int position) {
        imageView.setOnClickListener(v -> {
            ImagePreviewActivity.startPreview(this, selImageList, selImageList, selImageList.size(), position, true);
        });
    }

    private void commit() {
        if (mProvince == null || mCity == null) {
            UI.showToast(this, "请填写农田所有位置");
            return;
        }
        String name = binding.etCropName.getText().toString();
        String are = binding.etArea.getText().toString();
        String address = binding.tvDetailAddress.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(are)) {
            UI.showToast(this, "农田信息不能为空");
            return;
        }

        String province = mProvince != null ? mProvince.i : "";
        String city = mCity != null ? mCity.i : "";
        String county = mCounty != null ? mCounty.i : "";
        String xiang = mXiang != null ? mXiang.i : "";


        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("area_id_1", province)
                .addFormDataPart("area_id_2", city)
                .addFormDataPart("area_id_3", county)
                .addFormDataPart("area_id_4", xiang)
                .addFormDataPart("address", address)
                .addFormDataPart("acreage", are)
                .addFormDataPart("crops_name", name);

        if (mId != 0) {
            mb.addFormDataPart("id", mId + "");
        }

        int count = selImageList.size();
        Observable.from(selImageList)
                .flatMap(localMedia -> {
                    return Luban.get(AddFarmlandActivity.this)
                            .load(new File(localMedia.getPath()))
                            .putGear(Luban.THIRD_GEAR)
                            .asObservable();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(files -> {
                    for (int i = 0; i < count; i++) {
                        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), files.get(i));
                        mb.addFormDataPart("images[]", files.get(i).getPath(), body);
                    }
                    API.Transformer<Response> transform = new API.Transformer<>(AddFarmlandActivity.this);
                    transform.error((c, t) -> {
                        UI.showToast(AddFarmlandActivity.this, "提交失败！");
                    });

                    API.user().addFarmland(mb.build()).compose(transform.check()).lift(new ProgressOperator(AddFarmlandActivity.this, "正在提交...")).subscribe(result -> {
                        finish();
                    });
                });

    }


}
