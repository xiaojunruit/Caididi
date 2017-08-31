package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityUpdateUserInfoBinding;
import com.laoodao.caididi.ui.dialog.AvatarSelectDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.FileUtil;
import ezy.lite.util.IntentUtil;
import ezy.lite.util.LogUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by XiaoGe on 2017/1/18.
 */

public class UpdateUserInfoActivity extends BaseActivity implements View.OnClickListener{
    ActivityUpdateUserInfoBinding mBinding;
    Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_update_user_info);
        mBinding.setListener(this);
        mBinding.setItem(Global.info());
        try {
            uri = Uri.fromFile(File.createTempFile("cdd", ".jpeg", getExternalCacheDir()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getPath(Context ctx, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.setItem(Global.info());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Const.CODE_CAMERA_REQUEST:
                crop(uri, uri, 400, 400, Const.CROP_BIG_PICTURE);
                break;
            case Const.CODE_GALLERY_REQUEST:
                crop(data.getData(), uri, 400, 400, Const.CROP_BIG_PICTURE);
                break;
            case Const.CROP_BIG_PICTURE:
                upload(uri);
                break;
            case Const.CODE_NICKNAME:
                break;
            case Const.CODE_PHONE:
                break;
        }
    }

    private Bitmap decodeAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    File toFile(Uri uri) {
        File file;
        if (uri.getScheme().equals("content")) {
            file = new File(getPath(this, uri));
        } else {
            file = new File(uri.getPath());
        }
        LogUtil.e("===> " + file.toString() + "[" + FileUtil.size(file.length()) + "]");
        return file;
    }

    private void upload(Uri uri) {
        RequestBody avatar = RequestBody.create(MediaType.parse("image/jpeg"), toFile(uri));

        API.user().editAvatar(avatar).compose(transform()).subscribe(result -> {
            Global.session().getInfo().avatar = uri.toString();
            mBinding.avatar.setImageBitmap(decodeAsBitmap(uri));
        });
    }

    private void crop(Uri in, Uri out, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(in, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", false);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.avatar:
                new AvatarSelectDialog(this, this).show();
                break;
            case R.id.btn_camera:
                startActivityForResult(IntentUtil.photoCapture(uri), Const.CODE_CAMERA_REQUEST);
                break;
            case R.id.btn_photo:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Const.CODE_GALLERY_REQUEST);
                break;
            case R.id.fl_name:
                ContextUtil.startActivity(this,NicknameEditActivity.class);
                break;
            case R.id.fl_signature:
                ContextUtil.startActivity(this,SignatureEditActivity.class);
                break;
        }
    }
}
