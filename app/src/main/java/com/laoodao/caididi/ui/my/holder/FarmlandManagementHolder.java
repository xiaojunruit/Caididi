package com.laoodao.caididi.ui.my.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemFarmlandManagementBinding;
import com.laoodao.caididi.retrofit.user.FarmlandManagement;
import com.laoodao.caididi.ui.my.activity.FarmlandDetailActivity;
import com.laoodao.caididi.ui.widget.ShapedImageView;

import java.util.List;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.Device;

/**
 * Created by XiaoGe on 2017/2/8.
 */
@ItemLayout(R.layout.item_farmland_management)
public class FarmlandManagementHolder extends BindingHolder<FarmlandManagement, ItemFarmlandManagementBinding> {
    public FarmlandManagementHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            FarmlandDetailActivity.start(v1.getContext(), item.id);
        });

    }

    @Override
    public void refresh() {
        binding.setItem(item);

        int count = getParent().getAdapter().getItemCount() - 2;
        if (count - 1 == position) {
            binding.llContent.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
//            binding.llContent.setBackgroundColor(C);
        }
        List<String> imgarr = item.imgarr;
        int size = imgarr.size();
        if (size > 0) {
            binding.imageContent.setVisibility(View.VISIBLE);
            binding.imageContent.removeAllViews();
            for (int i = 0; i < size; i++) {
                ShapedImageView imageView = new ShapedImageView(itemView.getContext());
                imageView.setShapeMode(ShapedImageView.SHAPE_MODE_ROUND_RECT);
                imageView.setRadius(Device.dp2px(3));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                if (i < size - 1) {
                    params.rightMargin = Device.dp2px(5);
                }
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(itemView.getContext()).load(imgarr.get(i)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
                binding.imageContent.addView(imageView);
            }
        } else {
            binding.imageContent.setVisibility(View.GONE);
        }


        binding.executePendingBindings();
    }


}
