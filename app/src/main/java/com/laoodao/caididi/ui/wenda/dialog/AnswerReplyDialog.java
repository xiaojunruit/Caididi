package com.laoodao.caididi.ui.wenda.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.laoodao.caididi.R;
import com.laoodao.caididi.databinding.DialogAnswerReplyBinding;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Administrator on 2016/12/17.
 */

public class AnswerReplyDialog extends BottomBaseDialog<AnswerReplyDialog> implements View.OnClickListener {
    private DialogAnswerReplyBinding binding;

    private View.OnClickListener mListener;

    public AnswerReplyDialog(Context context, View.OnClickListener listener) {
        super(context);
        this.mListener = listener;
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_answer_reply, null, false);
        binding.setListener(this);

        return binding.getRoot();
    }

    public String getContent(){
        String content =  binding.edttxtContent.getText().toString();
        return content;
    }

    @Override
    public void setUiBeforShow() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) mContext.
                getSystemService(INPUT_METHOD_SERVICE);
//        imm.showSoftInput(binding.edttxtContent, 0); //显示软键盘
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onClick(View v) {
        mListener.onClick(v);
    }
}
