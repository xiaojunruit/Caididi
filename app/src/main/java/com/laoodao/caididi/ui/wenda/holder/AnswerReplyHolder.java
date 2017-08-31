package com.laoodao.caididi.ui.wenda.holder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.flyco.dialog.widget.popup.base.BasePopup;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Result;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ItemAnswerReplyBinding;
import com.laoodao.caididi.databinding.PopProblemMoreBinding;
import com.laoodao.caididi.event.CommentEvent;
import com.laoodao.caididi.retrofit.main.AnswerReply;
import com.laoodao.caididi.retrofit.main.Comment;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.OtherUserInfoActivity;
import com.laoodao.caididi.ui.wenda.dialog.AnswerReplyDialog;
import com.laoodao.caididi.ui.widget.commentList.CommentAdapter;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Administrator on 2016/12/17.
 */
@ItemLayout(R.layout.item_answer_reply)
public class AnswerReplyHolder extends BindingHolder<AnswerReply, ItemAnswerReplyBinding> implements View.OnClickListener {
    private MorePop morePop;
    public CommentAdapter commentAdapter;
    AnswerReplyDialog ard;

    public AnswerReplyHolder(View v) {
        super(v);
        binding.setListener(this);
        commentAdapter = new CommentAdapter(v.getContext());
        commentAdapter.setOnItemClickListener((position1, comment) -> {
            comment.circlePosition = position;
            RxBus.post(new CommentEvent(comment));
        });
        binding.commentList.setAdapter(commentAdapter);
        ard = new AnswerReplyDialog(v.getContext(), this);
    }

    @Override
    public void refresh() {
        commentAdapter.setDatas(item.comment);
        refreshData();
    }

    private void refreshData(){
        commentAdapter.notifyDataSetChanged();
        binding.setItem(item);
        binding.executePendingBindings();
    }

    private void setPraise(Context context,int type){
        API.main().praise(item.id,type).compose(new API.Transformer<>(context)).subscribe(result -> {
            if (result.data==null){
                return;
            }
            item.praiseName=result.data.praiseName;
            item.praiseNum=result.data.praiseNum;
            refreshData();
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.tv_praise&&!Global.session().isLoggedIn()){
            ContextUtil.startActivity(v.getContext(), LoginPhoneActivity.class);
            return;
        }
        switch (v.getId()) {
            case R.id.tv_praise:
                if (item.myPraise){
                    item.myPraise=false;
                    setPraise(v.getContext(), Const.CANCEl_PRAISE);
                    morePop.setUiBeforShow();
                }else{
                    item.myPraise=true;
                    setPraise(v.getContext(),Const.PRAISE);
                    morePop.setUiBeforShow();
                }
                break;
            case R.id.tv_comments1:
            case R.id.tv_comments:
                Comment comment = new Comment();
                comment.id = item.id+"";
                comment.circlePosition = position;
                RxBus.post(new CommentEvent(comment));
                break;
            case R.id.img_more:
                morePop = new MorePop(v.getContext(),this,item.myPraise);
                morePop
                        .showAnim(null)
                        .dismissAnim(null)
                        .dimEnabled(true)
                        .anchorView(binding.imgMore)
                        .offset(-50, 30)
                        .dimEnabled(false)
                        .gravity(Gravity.TOP)
                        .show();
                break;
            case R.id.tv_release:
                KeyboardUtil.hideSoftKeyboard(v);

                API.main().answer(15, ard.getContent(), "", item.id + "").compose(new API.Transformer<Result<Comment>>(v.getContext()).check()).subscribe(result -> {
                    item.comment.add(result.data);
                    commentAdapter.setDatas(item.comment);
                    commentAdapter.notifyDataSetChanged();
                });
                ard.dismiss();
                break;
            case R.id.tv_cancel:
                ard.dismiss();
                break;
            case R.id.cl_avatar:
                if (item.cid==0){
                    break;
                }
                if (Global.info().cid==item.cid){
                    MainActivity.start(v.getContext(),Const.TAB_USER);
                }
                OtherUserInfoActivity.start(v.getContext(),item.cid);
                break;
        }
    }

    class MorePop extends BasePopup<MorePop> implements View.OnClickListener {
        private PopProblemMoreBinding mBinding;
        private View.OnClickListener listener;
        private boolean praise;
        public MorePop(Context context,View.OnClickListener listener,boolean praise) {
            super(context);
            this.listener = listener;
            this.praise=praise;
        }

        @Override
        public View onCreatePopupView() {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(itemView.getContext()), R.layout.pop_problem_more, null, false);
            mBinding.setListener(this);
            setCanceledOnTouchOutside(true);
            setCancelable(true);
            return mBinding.getRoot();
        }

        @Override
        public void setUiBeforShow() {
            mBinding.setPraise(praise);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            InputMethodManager imm = (InputMethodManager) mContext.
                    getSystemService(INPUT_METHOD_SERVICE);
//            imm.showSoftInput(itemView, 0); //显示软键盘
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }

        @Override
        public void onClick(View v) {
            dismiss();
            listener.onClick(v);
        }
    }
}
