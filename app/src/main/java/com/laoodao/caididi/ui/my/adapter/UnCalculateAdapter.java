package com.laoodao.caididi.ui.my.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.user.Collection;
import com.laoodao.caididi.ui.my.activity.BiddersActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORK on 2017/3/9.
 */

public class UnCalculateAdapter extends RecyclerView.Adapter<UnCalculateAdapter.CalculateHolder> {
    private Context context;
    private List<Collection> mData = new ArrayList<>();
    private CheckClick mCheckClick;
    private CalculateHolder holder;
    private int pageState;
    private String ids = "";
    private int start;
    private int su;
    private int un;

    public UnCalculateAdapter(Context context) {
        this.context = context;
    }

    /**
     * 判断是否全选
     *
     * @param cbState
     */
    public void setCbState(boolean cbState) {
//        this.cbState = cbState;
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).isCheck = cbState;
        }
    }

    public String getIds() {
        return ids;
    }

    public void setPageState(int pageState) {
        this.pageState = pageState;
    }

    public List<Collection> getList() {
        return mData;
    }

    @Override
    public CalculateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        holder = new CalculateHolder(LayoutInflater.from(
                context).inflate(R.layout.item_calculate, parent,
                false));
        return holder;
    }

    public void addAll(List<Collection> items, boolean isClearFirst) {
        if (isClearFirst) {
            this.mData.clear();
        }
        this.mData.addAll(items);
        this.notifyDataSetChanged();

    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setmCheckClick(CheckClick checkClick) {
        this.mCheckClick = checkClick;
    }

    @Override
    public void onBindViewHolder(CalculateHolder holder, int position) {
        holder.txtTitle.setText(mData.get(position).suppler);
        holder.txtTime.setText(context.getResources().getString(R.string.settle_time, (mData.get(position).payDate)));
        holder.txtMoney.setText(Html.fromHtml(context.getResources().getString(R.string.settle_money, (mData.get(position).money))));
        holder.cbCollection.setVisibility(pageState == Const.NO_START ? View.VISIBLE : View.GONE);
        holder.cbCollection.setChecked(mData.get(position).isCheck);
        holder.cbCollection.setOnClickListener(v -> {
            mData.get(position).isCheck = holder.cbCollection.isChecked();
            isCheckAll();
            mCheckClick.OnClick(Double.parseDouble(mData.get(position).money), holder.cbCollection.isChecked(),su,un);
            addIds(String.valueOf(mData.get(position).id), holder.cbCollection.isChecked());
        });
        addIds(String.valueOf(mData.get(position).id), holder.cbCollection.isChecked());
        holder.itemView.setOnClickListener(v -> {
            BiddersActivity.start(v.getContext(), mData.get(position).id);
        });
    }

    private void isCheckAll() {
        su = 0;
        un = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isCheck) {
                su += 1;
            } else {
                un += 1;
            }
        }
    }



    private void addIds(String id, boolean state) {
        if (ids.contains(id) && !state) {
            ids = ids.replace(id + ",", "");
        } else if (!ids.contains(id) && state) {
            ids += id + ",";
        }
    }


    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    class CalculateHolder extends RecyclerView.ViewHolder {

        private CheckBox cbCollection;
        private TextView txtMoney;
        private TextView txtTitle;
        private TextView txtTime;

        public CalculateHolder(View view) {
            super(view);
            cbCollection = (CheckBox) view.findViewById(R.id.cb_collection);
            txtMoney = (TextView) view.findViewById(R.id.txt_money);
            txtTime = (TextView) view.findViewById(R.id.txt_time);
            txtTitle = (TextView) view.findViewById(R.id.txt_title);
        }
    }

    public interface CheckClick {
        void OnClick(double moeny, boolean isCheck,int su,int un);
    }

}
