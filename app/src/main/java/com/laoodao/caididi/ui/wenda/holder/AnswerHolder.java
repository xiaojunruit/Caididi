package com.laoodao.caididi.ui.wenda.holder;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ItemAnswerBinding;
import com.laoodao.caididi.databinding.PopAnswerMoreBinding;
import com.laoodao.caididi.event.NewestInfoEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.ui.adapter.NineGridImageViewAdapter;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.OtherUserInfoActivity;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;
import com.laoodao.caididi.ui.wenda.dialog.AnswerShareDialog;
import com.laoodao.caididi.ui.wenda.activity.ImgViewPagerActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
@ItemLayout(R.layout.item_answer)
public class AnswerHolder extends BindingHolder<Answer, ItemAnswerBinding> implements View.OnClickListener {
    private MorePop morePop;


    public AnswerHolder(View itemView) {
        super(itemView);
        binding.setListener(this);
        binding.nglImages.setAdapter(mAdapter);
        itemView.setOnClickListener(v -> {
            AnswerDetailActivity.start(itemView.getContext(), item.id, false);
        });
    }

    @Override
    public void refresh() {

        binding.setItem(item);
        binding.nglImages.setImagesData(item.imgArr);
        binding.executePendingBindings();

    }

    private NineGridImageViewAdapter<Answer.Img> mAdapter = new NineGridImageViewAdapter<Answer.Img>() {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, Answer.Img img) {
            int size = item.imgArr.size();
            int placeholder = 0;
            if (size > 1) {
                placeholder = R.mipmap.p430;
            } else {
                placeholder = R.mipmap.p800;
            }
            Glide.with(context)
                    .load(img.url)
                    .centerCrop()
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate()
                    .dontTransform()
                    .into(imageView);
        }

        @Override
        public ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }

        @Override
        public void onItemImageClick(Context context, int index, List<Answer.Img> list) {
            ImgViewPagerActivity.start(context, item.imgArr, index);
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_share:
            case R.id.tv_comments:
            case R.id.tv_want_answer:
                if (!Global.session().isLoggedIn()) {
                    ContextUtil.startActivity(v.getContext(), LoginPhoneActivity.class);
                    return;
                }
                click(v);
                break;
            case R.id.img_more:
                morePop = new MorePop(v.getContext(), this, item.wanttoknow);
                morePop
                        .showAnim(null)
                        .dismissAnim(null)
                        .dimEnabled(true)
                        .anchorView(binding.imgMore)
                        .offset(-50, 20)
                        .dimEnabled(false)
                        .gravity(Gravity.TOP)
                        .show();
                break;
            case R.id.cl_avatar:
                if (Global.info().cid==item.cid){
                    MainActivity.start(v.getContext(),Const.TAB_USER);
                    break;
                }
                OtherUserInfoActivity.start(v.getContext(),item.cid);
//                ContextUtil.startActivity(v.getContext(),OtherUserInfoActivity.class);
                break;
        }
    }

    void click(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                Activity activity = ContextUtil.from(itemView.getContext(), MainActivity.class);
                new AnswerShareDialog(activity, item.shareInfo).show();
                break;
            case R.id.tv_comments:
                AnswerDetailActivity.start(itemView.getContext(), item.id, true);
                break;
            case R.id.tv_want_answer:
                if (item.wanttoknow) {
                    item.wanttoknow = false;
                    setWanttoknow(v.getContext(), Const.CANCEl_WANTKNOW);
                    morePop.setUiBeforShow();
                } else {
                    item.wanttoknow = true;
                    setWanttoknow(v.getContext(), Const.WANTKNOW);
                    morePop.setUiBeforShow();
                }
                break;
        }
    }


    class MorePop extends BasePopup<MorePop> implements View.OnClickListener {
        PopAnswerMoreBinding mBinding;
        private boolean wanttoknow;
        private View.OnClickListener listener;

        public MorePop(Context context, View.OnClickListener listener, boolean wanttoknow) {
            super(context);
            this.listener = listener;
            this.wanttoknow = wanttoknow;
        }

        @Override
        public View onCreatePopupView() {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(itemView.getContext()), R.layout.pop_answer_more, null, false);
            mBinding.setListener(this);
            setCanceledOnTouchOutside(true);
            setCancelable(true);
            return mBinding.getRoot();
        }

        @Override
        public void setUiBeforShow() {
            mBinding.setWanttoknow(wanttoknow);
        }

        @Override
        public void onClick(View v) {
            dismiss();
            listener.onClick(v);

        }

    }

    private void setWanttoknow(Context context, int type) {
        API.main().wanttoknow(item.id, type).compose(new API.Transformer<>(context)).subscribe(response -> {
            RxBus.post(new NewestInfoEvent(String.valueOf(item.id)));
        });
    }


}
