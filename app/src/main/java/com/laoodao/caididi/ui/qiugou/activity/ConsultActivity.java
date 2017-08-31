package com.laoodao.caididi.ui.qiugou.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.databinding.ActivityConsultBinding;
import com.laoodao.caididi.retrofit.main.ChatMessage;
import com.laoodao.caididi.ui.qiugou.holder.ChatMessageHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.FileUtil;
import ezy.lite.util.IntentUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/2/16.
 */

public class ConsultActivity extends BaseActivity implements XRecyclerView.LoadingListener, View.OnClickListener {
    private ActivityConsultBinding binding;
    private BaseAdapter adapter = new ItemAdapter(ChatMessageHolder.class);
    boolean mInited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_consult);
        binding.list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(false);
        binding.list.setLoadingListener(this);
        binding.setListener(this);
        binding.txtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.txtContent.getText().toString().trim())) {
                    binding.btnSendText.setEnabled(false);
                } else {
                    binding.btnSendText.setEnabled(true);
                }
            }
        });

        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == 0) {
                    Glide.with(getApplicationContext()).resumeRequests();
                } else {
                    Glide.with(getApplicationContext()).pauseRequests();
                }

            }
        });
        KeyboardUtil.autoHide(binding.txtContent, binding.list);
        sync();
        headerRefresh();
    }

    private void headerRefresh() {
        API.main().chatInit(8).compose(transform()).subscribe(result -> {
            binding.setItem(result.data);
            binding.executePendingBindings();
        });
    }


    int mOldestMessageId = 0;
    int mNewestMessageId = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (mInited) {
            sync();
        }
    }

    void sync() {
        Observable.just(0).delay(mInited ? 5 : 0, TimeUnit.SECONDS).compose(bindToLifecycle()).subscribe(o -> {
            API.main().demChatList(8, mNewestMessageId).compose(transform()).doOnTerminate(this::sync).subscribe(result -> {
                List<ChatMessage> items = result.data;
                if (items.size() > 0) {
                    append(items, mInited);
                    if (mOldestMessageId < 1) {
                        mOldestMessageId = items.get(0).id;
                    }
                    mNewestMessageId = items.get(items.size() - 1).id;
                }
                if (!mInited) {
                    mInited = true;
                }
//                if (!Prefs.get(Const.PREF_FAQS, false)) {
//                    Prefs.apply(Const.PREF_FAQS, true);
//                    adapter.add(config.toFaqIndexMessage());
//                }
            });
        });
    }


    void append(ChatMessage cm, boolean isSelf) {
        fillUserInfo(cm, isSelf);
        adapter.setNotifyOnChange(true);
        adapter.add(cm);
        binding.executePendingBindings();
        scrollToBottom(true);
    }

    void append(List<ChatMessage> items, boolean smooth) {
        addAll(items, false);
        binding.executePendingBindings();
        scrollToBottom(smooth);
    }

    void prepend(List<ChatMessage> items) {
        addAll(items, true);
    }

    void scrollToBottom(final boolean smooth) {
        binding.list.postDelayed(() -> {
            int pos = binding.list.getAdapter().getItemCount() - 1;
            LogUtil.e("scrollToBottom ==> pos = " + pos);
            if (smooth) {
                binding.list.smoothScrollToPosition(pos);
            } else {
                binding.list.scrollToPosition(pos);
            }
        }, 100);
    }

    void addAll(List<ChatMessage> items, boolean older) {
        for (int i = 0; i < items.size(); i++) {
            fillUserInfo(items.get(i), items.get(i).isSelf);
        }
        if (older) {
            binding.list.postDelayed(() -> {
                binding.list.scrollToPosition(items.size() + 3);
            }, 100);
            adapter.setNotifyOnChange(false);
            adapter.addAll(0, items);
            binding.executePendingBindings();

        } else {
            adapter.addAll(items);
        }
    }

    void fillUserInfo(ChatMessage cm, boolean isSelf) {
        cm.isSelf = isSelf;
        if (isSelf) {
            cm.cid = Global.info().cid;
            cm.memberAvatar = Global.info().avatar;
        }
    }


    public static Bitmap resize(Bitmap src, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, src.getWidth(), src.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Const.CODE_CAMERA_REQUEST) {
            addImage(uri);
            return;
        }
        if (requestCode == Const.CODE_GALLERY_REQUEST && null != data) {
            addImage(data.getData());
            return;
        }
    }

    private void addImage(Uri src) {
        final Uri dst = uri;
        try {
            Bitmap thumbnail = resize(BitmapFactory.decodeFile(toFile(src).toString()), 1024, 1024);
            FileOutputStream bos = new FileOutputStream(dst.getPath());
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            bos.close();
        } catch (IOException e) {
        }
        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(8))
                .addFormDataPart("type", String.valueOf(2));
        RequestBody image = RequestBody.create(MediaType.parse("image/jpeg"), toFile(dst));
        mb.addFormDataPart("image", dst.getLastPathSegment(), image);
        API.main().chatSend(mb.build()).compose(transform()).subscribe(result -> {
            UI.showToast(this, "上传成功");
            if (mNewestMessageId < result.data.id) {
                mNewestMessageId = result.data.id;
                append(result.data, true);
            }
        });
    }

    Uri uri;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_image:
                final String[] selects = {"拍照", "从手机相册选择"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(selects, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            uri = Uri.fromFile(File.createTempFile("caididi", ".jpeg", getExternalCacheDir()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        switch (which) {
                            case 0:
                                startActivityForResult(IntentUtil.photoCapture(uri), Const.CODE_CAMERA_REQUEST);
                                break;
                            case 1:
                                startActivityForResult(IntentUtil.pickImage(), Const.CODE_GALLERY_REQUEST);
                                break;
                        }
                    }
                });
                builder.show();
                break;
            case R.id.btn_send_text:
                API.main().chatSend(8, 1, UI.toString(binding.txtContent)).compose(new API.Transformer<>(this)).subscribe(result -> {
                    binding.txtContent.setText("");
                    if (mNewestMessageId < result.data.id) {
                        mNewestMessageId = result.data.id;
                        append(result.data, true);
                    }
                });
                break;
        }
    }

    protected int cursor = 0;
    int page = 0;

   /* public <Item> void onPageLoaded(Page<Item> result) {

        if (page < 2) {
            adapter.addAll(result.data.items, true);

        } else {
            int size = adapter.getItemCount();
            adapter.getList().addAll(result.data.items);
            adapter.notifyItemRangeChanged(size + 2
                    , adapter.getList().size() - 1);
        }
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            binding.list.noMoreLoading();
            binding.list.loadMoreComplete();
        }
        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }*/

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    /*binding.loading.showError();*/
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(this, result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.list.refreshComplete();
                    } else {
                        binding.list.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        page++;
        onPage(page);
    }

    @Override
    public void onLoadMore() {
        binding.list.setIsnomore(false);
        page = 1;
        cursor = 0;
        onPage(page);
    }

    private void onPage(int page) {
        API.main().chatOlder(8, mOldestMessageId, page).compose(transform()).doOnTerminate(() -> {
            binding.list.refreshComplete();
        }).subscribe(result -> {
            List<ChatMessage> items = result.data.items;
            if (items == null || items.size() < 1) {
                return;
            }
            prepend(items);
            boolean noMore = result.data.size * page >= result.data.total;
            if (noMore) {
                binding.list.noMoreLoading();
                binding.list.loadMoreComplete();
            }
            mOldestMessageId = items.get(0).id;
            /*onPageLoaded(result);*/
        });
    }
}
