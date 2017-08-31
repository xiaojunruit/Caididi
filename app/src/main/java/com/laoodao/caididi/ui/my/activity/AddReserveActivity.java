package com.laoodao.caididi.ui.my.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Result;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.ProgressOperator;
import com.laoodao.caididi.databinding.ActivityAddReserveBinding;
import com.laoodao.caididi.ui.widget.imageSelect.model.LocalMedia;
import com.laoodao.caididi.ui.widget.imageSelect.view.ImagePreviewActivity;
import com.laoodao.caididi.ui.widget.imageSelect.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

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
 * Created by WORK on 2017/3/6.
 */

public class AddReserveActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddReserveBinding mBinding;
    private ArrayList<LocalMedia> selImageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_reserve);
        mBinding.setOnClick(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooes_image:
                ImageSelectorActivity.start(this, 3 - selImageList.size(), 1, true, true, false);
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
            } else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                //预览后返回的数据
                ArrayList<LocalMedia> images = (ArrayList<LocalMedia>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                selImageList.clear();
                addImages(images);
            }
        }
    }

    private void addImages(ArrayList<LocalMedia> images) {
//        selImageList.clear();
        selImageList.addAll(images);
        mBinding.imageContent.removeAllViews();
        mBinding.chooesImage.setVisibility(selImageList.size() >= 3 ? View.GONE : View.VISIBLE);
        for (int i = 0; i < selImageList.size(); i++) {
            ImageView imageView = new ImageView(this);
            int width = (mBinding.llImageParent.getWidth() - Device.dp2px(80) - Device.dp2px(30)) / 3;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            params.rightMargin = Device.dp2px(10);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(params);
            Glide.with(this).load(selImageList.get(i).getPath()).thumbnail(0.5f).into(imageView);
            mBinding.imageContent.addView(imageView);
            imgClick(imageView, i);
        }
    }

    private void imgClick(ImageView imageView, int position) {
        imageView.setOnClickListener(v -> {
            ImagePreviewActivity.startPreview(this, selImageList, selImageList, selImageList.size(), position, true);
        });
    }

    private void commit() {
        String name = mBinding.editName.getText().toString().replaceAll(" ", "");
        String phone = mBinding.editPhone.getText().toString();
        String cropsName = mBinding.editCropsName.getText().toString().replaceAll(" ", "");
        String farmlandArea = mBinding.editFarmlandArea.getText().toString().replaceAll(" ", "");
        String desc = mBinding.editDesc.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(cropsName) || TextUtils.isEmpty(farmlandArea) || TextUtils.isEmpty(desc)) {
            UI.showToast(this, "请完善您的资料");
            return;
        }

        if (phone.length() != 11) {
            UI.showToast(this, "请填写正确的电话号码");
            return;
        }

        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("mobile", phone)
                .addFormDataPart("crops_name", cropsName)
                .addFormDataPart("acreage", farmlandArea)
                .addFormDataPart("remark", desc);
        int count = selImageList.size();
        Observable.from(selImageList)
                .flatMap(localMedia -> {
                    return Luban.get(AddReserveActivity.this)
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
                    API.Transformer<Result<Map<String, String>>> transform = new API.Transformer<>(AddReserveActivity.this);
                    transform.error((c, t) -> {
                        UI.showToast(AddReserveActivity.this, "提交失败！");
                    });

                    API.user().addReserve(mb.build()).compose(transform.check()).lift(new ProgressOperator<>(AddReserveActivity.this, "正在提交...")).subscribe(result -> {
                        ReserveAfterActivity.start(this, result.data.get("id"));
                        finish();
                    });

                });
    }


}
