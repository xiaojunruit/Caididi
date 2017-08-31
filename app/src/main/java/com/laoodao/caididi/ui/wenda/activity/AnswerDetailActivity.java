package com.laoodao.caididi.ui.wenda.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FooterAnswerBinding;
import com.laoodao.caididi.databinding.HeaderAnswerBinding;
import com.laoodao.caididi.event.CommentEvent;
import com.laoodao.caididi.event.MyUnAnswerEvent;
import com.laoodao.caididi.event.NewestInfoEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.retrofit.main.AnswerReply;
import com.laoodao.caididi.retrofit.main.Comment;
import com.laoodao.caididi.retrofit.main.ShareInfo;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.OtherUserInfoActivity;
import com.laoodao.caididi.ui.wenda.dialog.AnswerShareDialog;
import com.laoodao.caididi.ui.wenda.holder.AnswerReplyHolder;
import com.laoodao.caididi.ui.widget.ShapedImageView;
import com.laoodao.caididi.utils.KeyBoard;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;

/**
 * Created by Administrator on 2016/12/8.
 */

public class AnswerDetailActivity extends ListActivity implements View.OnClickListener, KeyBoard.OnInputMethodManagerLinstener {
    HeaderAnswerBinding headerBinding;
    private boolean isKnow;
    private List<String> listTag;
    private int mId;
    FooterAnswerBinding footerBinding;
    private Comment mComment;
    private boolean isComments;
    private ShareInfo shareInfo;
    private boolean isValue;
    private int cid;
    private boolean isUn;

    public static void start(Context context, int id, boolean isComments) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putBoolean("iscomments", isComments);
        ContextUtil.startActivity(context, AnswerDetailActivity.class, bundle);
    }

    public static void start(Context context, int id, boolean isComments, boolean isUn) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putBoolean("iscomments", isComments);
        bundle.putBoolean("isUn", isUn);
        ContextUtil.startActivity(context, AnswerDetailActivity.class, bundle);
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(AnswerReplyHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        headerBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header_answer, null, false);
        mId = getIntent().getIntExtra("id", 0);
        isComments = getIntent().getBooleanExtra("iscomments", false);
        isUn = getIntent().getBooleanExtra("isUn", false);
        binding.list.addHeaderView(headerBinding.getRoot());
        binding.loading.showContent();
        /**
         * 滑动时，隐藏软键盘，重置回复人
         */
        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean isSignificantDelta = Math.abs(dy) > ViewConfiguration.getTouchSlop();
                if (isSignificantDelta && dy > 50) {
                    pid = "";
                    footerBinding.edittxtContent.clearFocus();
                    footerBinding.edittxtContent.setHint("说点什么吧");
                    KeyboardUtil.hideSoftKeyboard(footerBinding.edittxtContent);
                }
            }
        });
        if (isComments) {
            footerBinding.edittxtContent.requestFocus();
            new Handler().postDelayed(() -> {
                KeyboardUtil.showSoftKeyboard(footerBinding.edittxtContent);
            }, 100);
        }
        /**
         * 处理评论事件，调起editTExt
         */
        RxBus.ofType(CommentEvent.class).compose(bindToLifecycle()).subscribe(result -> {
            reply(result.comment);
        });
        binding.loading.showLoading();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.answer_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                new AnswerShareDialog(this, shareInfo).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getFooterLayoutId() {
        return R.layout.footer_answer;
    }

    @Override
    protected void onFooterCreated(View view) {
        footerBinding = DataBindingUtil.bind(view);
        footerBinding.setOnClick(this);
        footerBinding.cbCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.session().isLoggedIn()) {
                    footerBinding.cbCollection.setChecked(footerBinding.cbCollection.isChecked() ? false : true);
                    ContextUtil.startActivity(v.getContext(), LoginPhoneActivity.class);
                    return;
                }
                if (footerBinding.cbCollection.isChecked()) {
                    collected(1);
                } else {
                    collected(2);
                }
            }
        });
        footerBinding.edittxtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    isValue = true;
                    footerBinding.tvSend.setTextColor(Color.parseColor("#2AB80E"));
                } else {
                    isValue = false;
                    footerBinding.tvSend.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        KeyBoard.assistActivity(this, this);
    }

    public void reply(Comment comment) {
        mComment = comment;
        if (comment != null && !TextUtils.isEmpty(comment.replayMemberName)) {
            footerBinding.edittxtContent.setHint("回复" + comment.replayMemberName + ":");
        } else if (comment != null && !TextUtils.isEmpty(comment.memberName)) {
            footerBinding.edittxtContent.setHint("回复" + comment.memberName + ":");
        } else {
            footerBinding.edittxtContent.setHint("说点什么吧");
        }

        footerBinding.edittxtContent.requestFocus();
        new Handler().postDelayed(() -> {
            KeyboardUtil.showSoftKeyboard(footerBinding.edittxtContent);
        }, 1);
        ((LinearLayoutManager) binding.list.getLayoutManager()).scrollToPositionWithOffset(comment.circlePosition + 2, 0);

    }


    @Override
    public void onRefresh() {
        headerViewRefresh();
        super.onRefresh();
    }

    private void collected(int type) {
        API.main().collection(mId, type).compose(transform()).subscribe(result -> {
        });
    }

    private void headerViewRefresh() {


        API.main().answerDetailList(mId).compose(transform()).subscribe(result -> {
            binding.loading.showContent();
            shareInfo = result.data.shareInfo;
            headerBinding.setItem(result.data);
            isKnow = result.data.wanttoknow;
            cid = result.data.cid;
            if (isKnow) {
                headerBinding.txtKnow.setText("点击取消关注答案");
            } else {
                headerBinding.txtKnow.setText("我也想知道答案");
            }
//            headerBinding.llImgContent.removeAllViews();
            footerBinding.cbCollection.setChecked(result.data.isCollected);
            addImageView(result.data.imgArr);


        });

        headerBinding.setListener(this);
        listTag = new ArrayList<>();
        listTag.add("黄瓜");
        listTag.add("草莓");
        listTag.add("苹果");
        listTag.add("橘子");
        listTag.add("蜜瓜");
        headerBinding.tags.setList(listTag);
    }


    /**
     * 添加图片
     *
     * @param imgArr
     */
    private void addImageView(ArrayList<Answer.Img> imgArr) {
        int width = Device.dm.widthPixels - Device.dp2px(30);
        headerBinding.llImgContent.removeAllViewsInLayout();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = Device.dp2px(10);
        for (int i = 0; i < imgArr.size(); i++) {
            ShapedImageView view = new ShapedImageView(this);
            int finalI = i;
            Glide.with(this).load(imgArr.get(i).url).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Matrix matrix = new Matrix();
                    float sale = width * 1f / resource.getWidth();
                    matrix.postScale(sale, sale);
                    Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
                    view.setImageBitmap(bitmap);
                    view.setLayoutParams(params);
                    view.setOnClickListener(v -> {
                        ImgViewPagerActivity.start(AnswerDetailActivity.this, imgArr, finalI);
                    });
                }
            });
            headerBinding.llImgContent.addView(view);
        }
    }


    private String pid;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_know:
                if (!Global.session().isLoggedIn()) {
                    ContextUtil.startActivity(this, LoginPhoneActivity.class);
                    return;
                }
                if (!isKnow) {
                    headerBinding.txtKnow.setText("点击取消关注答案");
                    knowInterface(Const.WANTKNOW);
                    isKnow = true;
                } else {
                    headerBinding.txtKnow.setText("我也想知道答案");
                    knowInterface(Const.CANCEl_WANTKNOW);
                    isKnow = false;
                }
                break;
            case R.id.tv_send:
                String content = footerBinding.edittxtContent.getText().toString() + "";
                API.main().answer(mId, content, "", mComment != null ? mComment.id : "").compose(transform()).subscribe(result -> {
                    //回复层主or评论层主
                    if (mComment != null && !TextUtils.isEmpty(mComment.id)) {
                        AnswerReply ar = (AnswerReply) adapter.getItem(mComment.circlePosition);
                        ar.comment.add(result.data);
                        adapter.notifyItemChanged(mComment.circlePosition + 2);
                    } else {
                        //评论楼主
                        binding.list.scrollToPosition(2);
                        onPage(1);
                    }
                    mComment = null;
                    KeyboardUtil.hideSoftKeyboard(footerBinding.edittxtContent);
                    footerBinding.edittxtContent.setHint("说点什么吧");
                    footerBinding.edittxtContent.setText("");

                    if (isUn) {
                        RxBus.post(new MyUnAnswerEvent());
                    }
                });

                break;
            case R.id.cl_avatar:
                if (cid == 0) {
                    break;
                }
                if (Global.info().cid == cid) {
                    MainActivity.start(this, Const.TAB_USER);
                    break;
                }
                OtherUserInfoActivity.start(this, cid);
                break;
        }
    }


    @Override
    protected void onPage(int page) {
        API.main().comments(page, mId).compose(transform()).subscribe(r -> {
            onPageLoaded(r);
            binding.loading.showContent();
            headerBinding.replyCount.setText(adapter.getItemCount() + "人回复");
        });
    }

    private void knowInterface(int type) {
        API.main().wanttoknow(mId, type).compose(transform()).subscribe(result -> {
            API.main().newestInfo(String.valueOf(mId)).compose(transform()).subscribe(result1 -> {
                for (int i = 0; i < result1.data.size(); i++) {
                    headerBinding.txtNames.setVisibility(result1.data.get(i).namesTotal == 0 ? View.GONE : View.VISIBLE);
                    headerBinding.txtNamesTotal.setVisibility(result1.data.get(i).namesTotal == 0 ? View.GONE : View.VISIBLE);
                    headerBinding.txtNames.setText(result1.data.get(i).names);
                    headerBinding.txtNamesTotal.setText(result1.data.size() > 1 ? " 等" + result1.data.get(i).namesTotal + "人想知道答案" : " " + result1.data.get(i).namesTotal + "人想知道答案");
                }
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mId != 0) {
            RxBus.post(new NewestInfoEvent(String.valueOf(mId)));
        }
    }

    @Override
    public void inputMethodCallBack(boolean isShow) {
        if (isValue) {
            isShow = true;
        }
        footerBinding.cbCollection.setVisibility(isShow ? View.GONE : View.VISIBLE);
        footerBinding.tvSend.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
