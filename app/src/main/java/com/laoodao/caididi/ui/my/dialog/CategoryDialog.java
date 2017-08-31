package com.laoodao.caididi.ui.my.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.databinding.DialogCategoryBinding;
import com.laoodao.caididi.databinding.ItemCategroyBinding;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.retrofit.user.DemKind;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.Device;

/**
 * Created by XiaoGe on 2017/2/11.
 */

public class CategoryDialog extends BaseDialog<CategoryDialog> {
    private DialogCategoryBinding binding;
    private View mAnchorView;

    private CategoryAdapter mCategoryAdapter = new CategoryAdapter();
    private OnResult mOnResult;

    public static int TYPE_ALL = -1;

    public CategoryDialog(Context context, View anchorView, OnResult onResult) {
        super(context, true);
        mAnchorView = anchorView;
        setCanceledOnTouchOutside(true);
        mOnResult = onResult;
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_category, null, false);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.list.setAdapter(mCategoryAdapter);
        binding.txtAll.setOnClickListener(view -> {
            mOnResult.onResult(new Plants(-1, "全部"));
            dismiss();
        });
        return binding.getRoot();
    }

    public interface OnResult {

        void onResult(Plants plants);
    }

    @Override
    public void setUiBeforShow() {
        API.user().listDemKind().compose(new API.Transformer<>(getContext())).subscribe(result -> {
            mCategoryAdapter.addAll(result.data);
        });


    }


    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
        private List<DemKind> mLists = new ArrayList<>();

        public void addAll(List<DemKind> lists) {
            this.mLists.clear();
            this.mLists.addAll(lists);
            notifyDataSetChanged();
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CategoryHolder holder = new CategoryHolder(LayoutInflater.from(mContext).inflate(R.layout.item_categroy, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            DemKind kind = mLists.get(position);
            holder.cateBinding.taggroup.setList(kind.plants);
            holder.cateBinding.setItem(kind);
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }

        class CategoryHolder extends RecyclerView.ViewHolder {
            protected ItemCategroyBinding cateBinding;

            public CategoryHolder(View itemView) {
                super(itemView);
                cateBinding = DataBindingUtil.bind(itemView);
                cateBinding.taggroup.setTagViewFactory((group, position, o) -> {
                    TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_categroy_name, null, false);
                    view.setWidth(Device.dp2px(75));
                    view.setText(((Plants) o).name);
                    return view;
                });
                cateBinding.taggroup.setOnTagItemClickListener((view, position, tag) -> {
                    Plants plants = (Plants) tag;
                    mOnResult.onResult(plants);
                    dismiss();
                });
            }
        }

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

        mLlControlHeight.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
