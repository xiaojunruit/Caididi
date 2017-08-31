package com.laoodao.caididi.ui.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.laoodao.caididi.R;
import com.laoodao.caididi.databinding.DialogAvatarSelectBinding;

/**
 * Created by WORK on 2016/9/2.
 */

public class AvatarSelectDialog extends BottomBaseDialog<AvatarSelectDialog> implements View.OnClickListener {

    private View.OnClickListener mListener;
    private DialogAvatarSelectBinding binding;

    public AvatarSelectDialog(Context context, @NonNull View.OnClickListener listener) {
        super(context);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_avatar_select, null, false);

        binding.setListener(this);
        this.mListener = listener;
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

                dismiss();;
                break;
            default:
                dismiss();
                mListener.onClick(v);

                break;
        }

    }
}
