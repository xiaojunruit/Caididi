package com.laoodao.caididi.ui.wenda.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.common.util.ProgressOperator;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityMyAskBinding;
import com.laoodao.caididi.event.AskEvent;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.ui.wenda.adapter.ImagePickerAdapter;
import com.laoodao.caididi.ui.widget.imageSelect.model.LocalMedia;
import com.laoodao.caididi.ui.widget.imageSelect.view.ImagePreviewActivity;
import com.laoodao.caididi.ui.widget.imageSelect.view.ImageSelectorActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.FileUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by Administrator on 2016/12/13.
 */

public class MyAskActivity extends BaseActivity implements View.OnClickListener, ImagePickerAdapter.OnRecyclerViewItemClickListener {
    ActivityMyAskBinding binding;
    private String ids = "";
    private ArrayList<LocalMedia> selImageList; //当前选择的所有图片
    private ImagePickerAdapter adapter;
    private int maxImgCount = 9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_ask);
        binding.setListener(this);


        Global.locator.registerLocationListener(mLocationListener);
        binding.setLocationNode("定位中...");

        Global.locator.start();
        Global.locator.requestLocation();
        LogUtil.e("============>>>>getLocation" + Global.getLocation());

        selImageList = new ArrayList<>();
        binding.list.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        binding.list.setAdapter(adapter);
    }


    /**
     * 定位
     */
    BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            LogUtil.e("============>>>>" + location.getCity());
            Global.setLocation(location);
            Global.locator.stop();
            binding.setLocationNode(location.getCity());
        }


    };


    private void submitData() {

        KeyboardUtil.hideSoftKeyboard(binding.edittxtContent);
        String title = UI.toString(binding.edittxtTitle);
        String content = UI.toString(binding.edittxtContent);
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }
        LogUtil.e("address" + Global.getLocation().address);

        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("content", content)
                .addFormDataPart("lon", Global.getLocation().lon)
                .addFormDataPart("lat", Global.getLocation().lat)
                .addFormDataPart("district", Global.getLocation().address)
                .addFormDataPart("gc_ids", ids);
        int count = selImageList.size();
        Observable.from(selImageList)
                .flatMap(localMedia -> {
                    return Luban.get(MyAskActivity.this)
                            .load(new File(localMedia.getPath()))
                            .putGear(Luban.THIRD_GEAR)
                            .asObservable();
                })
                .subscribeOn(Schedulers.io())
                .toList()
                .lift(new ProgressOperator<>(MyAskActivity.this, "正在提交..."))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files -> {
                    for (int i = 0; i < count; i++) {
                        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), files.get(i));
                        mb.addFormDataPart("images[]", files.get(i).getPath(), body);
                    }
                    API.Transformer<Response> transform = new API.Transformer<>(MyAskActivity.this);
                    transform.error((c, t) -> {
                        UI.showToast(MyAskActivity.this, "提交失败！");
                    });
                    API.main().addQuestion(mb.build()).compose(transform.check()).lift(new ProgressOperator(MyAskActivity.this, "正在提交...")).subscribe(result -> {
                        LogUtil.e("================>>>>" + result);
                        RxBus.post(new AskEvent());
                        finish();
                    });
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.btn_img:
                chooseImage();
                break;
            case R.id.txt_submit:
                submitData();
                break;
            case R.id.rl_crops_classification:
//                FollowCropActivity.start(this, Const.RELEASE_ANSWER, childList);
                FollowCropActivity.startForResult(this, selectPlants);
                break;
        }
    }

    private static final int REQUEST_CODE = 0;


    private void chooseImage() {
        ImageSelectorActivity.start(this, 9 - selImageList.size(), 1, true, true, false);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //选择图片返回的数据
            if (requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
                ArrayList<LocalMedia> images = (ArrayList<LocalMedia>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                LogUtil.e("image" + images.size());
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            } else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                //预览后返回的数据
                ArrayList<LocalMedia> images = (ArrayList<LocalMedia>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            } else if (requestCode == FollowCropActivity.REQUEST_CODE) {
                //选择的作物
                ArrayList<Plants> plantses = (ArrayList<Plants>) data.getSerializableExtra(FollowCropActivity.REQUEST_OUTPUT);
                selectPlants.clear();
                selectPlants.addAll(plantses);
                cropList(plantses);
            }
        }
    }


    private List<Plants> selectPlants = new ArrayList<>();

    public void cropList(List<Plants> plantses) {
        String name = "";
        ids = "";
        for (Plants plantse : plantses) {
            name += Html.fromHtml(plantse.name + "\t\t").toString();
            ids += plantse.id + ",";
        }
        binding.tvCropList.setText(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.locator.unRegisterLocationListener(mLocationListener);
        Global.locator.stop();
    }

    @Override
    public void onItemClick(View view, int position) {

        switch (position) {
            case -1:
                //选择图片
                chooseImage();
                break;
            default:
                //Y预览图片
                ImagePreviewActivity.startPreview(this, selImageList, selImageList, selImageList.size(), position, true);
                break;
        }

    }
}
