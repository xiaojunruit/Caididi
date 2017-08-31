package com.laoodao.caididi.ui.main.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.Route;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemHomeGridBinding;
import com.laoodao.caididi.retrofit.main.Menu;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;

/**
 * Created by long on 2017/8/29.
 */
@ItemLayout(R.layout.item_home_grid)
public class HomeGridHolder extends BindingHolder<Menu,ItemHomeGridBinding>{
    public HomeGridHolder(View v) {
        super(v);
        v.setOnClickListener(view -> {
            if (item!=null){
                if (!Global.session().isLoggedIn()){
                    ContextUtil.startActivity(view.getContext(), LoginPhoneActivity.class);
                    return;
                }
                Route.go(view.getContext(),item.url);
            }
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
