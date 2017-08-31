package com.laoodao.caididi.ui.main.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.laoodao.caididi.R;
import com.laoodao.caididi.databinding.DialogLoginSelectBinding;

public class LoginSelectDialog extends BottomBaseDialog<LoginSelectDialog> implements View.OnClickListener {
    private DialogLoginSelectBinding binding;
    private View.OnClickListener mListener;

    public LoginSelectDialog(Context context, @NonNull View.OnClickListener listener) {
        super(context);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_login_select, null, false);
        binding.setListener(this);
        mListener = listener;
    }

    @Override
    public View onCreateView() {
        return binding.getRoot();
    }


    @Override
    public void setUiBeforShow() {
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_cancel:
            dismiss();
            break;
        default:
            mListener.onClick(v);
            dismiss();
            break;
        }
    }
}