package com.laoodao.caididi.ui.my.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flyco.dialog.widget.base.BaseDialog;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.databinding.DialogAbortTimeBinding;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.retrofit.user.AbortTime;
import com.laoodao.caididi.ui.my.holder.AbortTimeHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by XiaoGe on 2017/2/11.
 */

public class AbortTimeDialog extends BaseDialog<AbortTimeDialog> {
    private DialogAbortTimeBinding binding;
    private View mAnchorView;
    private OnResult mOnResult;
    private ArrayAdapter<AbortTime> mAbortAdapter;
    public static int TYPE_DIY = 8;

    public AbortTimeDialog(Context context, View view, OnResult onResult) {
        super(context, true);
        mAnchorView = view;
        this.mOnResult = onResult;
        setCanceledOnTouchOutside(true);
        mAbortAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dropdown_province);
    }

    public interface OnResult {
        void onResult(AbortTime at);
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_abort_time, mLlControlHeight, false);
        binding.list.setAdapter(mAbortAdapter);
        binding.list.setOnItemClickListener((parent, view, position, id) -> {
            AbortTime item = mAbortAdapter.getItem(position);
            mOnResult.onResult(item);
            dismiss();
        });
        return binding.getRoot();
    }


    @Override
    public void setUiBeforShow() {
        API.user().listAbortTime().compose(new API.Transformer<>(getContext())).subscribe(result -> {
            mAbortAdapter.addAll(result.data);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        final Rect rect = new Rect();
        mAnchorView.getWindowVisibleDisplayFrame(rect);

        int[] location = new int[2];
        mAnchorView.getLocationInWindow(location);

        int y = location[1] + mAnchorView.getHeight();

        lp.dimAmount = 0;
        lp.gravity = Gravity.TOP;
        lp.x = location[0];
        lp.y = y;


        mLlTop.setBackgroundColor(0x40000000);
        mLlTop.setGravity(Gravity.TOP);
        mLlTop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.dm.heightPixels - y));

        mLlControlHeight.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.dm.heightPixels - y));
    }

}
