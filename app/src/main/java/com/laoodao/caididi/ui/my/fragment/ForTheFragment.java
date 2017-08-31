package com.laoodao.caididi.ui.my.fragment;

import android.databinding.DataBindingUtil;
import android.text.Html;
import android.view.View;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.databinding.HeaderFortheBinding;
import com.laoodao.caididi.ui.my.holder.GreensAllHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by WORK on 2017/3/28.
 */

public class ForTheFragment extends ListFragment {

    private HeaderFortheBinding mHeaderBinding;

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter(GreensAllHolder.class);
    }


    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_forthe;
    }

    @Override
    protected void onHeaderCreated(View view) {
        mHeaderBinding = DataBindingUtil.bind(view);
    }

    @Override
    protected void onPage(int page) {
        API.user().orderList(page, Const.FOR_THE).compose(transform()).subscribe(result -> {
            onPageLoaded(result);
            mHeaderBinding.txtFortheTotal.setText(Html.fromHtml(getResources().getString(R.string.forthe_total, result.data.totalBalance==null?"0.00":result.data.totalBalance)));
        });
    }
}
