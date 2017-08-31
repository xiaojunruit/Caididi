package com.laoodao.caididi.ui.my.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.laoodao.caididi.R;
import com.laoodao.caididi.databinding.DialogSelectAreaBinding;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.ui.widget.citySelector.db.DBManager;
import com.laoodao.caididi.utils.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/2/11.
 */

public class DropdownAreaDialog extends BaseDialog<DropdownAreaDialog> {


    private View mAnchorView;
    private ArrayAdapter<City> mProvinceAdapter;
    private ArrayAdapter<City> mCityAdapter;
    private ArrayAdapter<City> mCountyAdapter;
    private DialogSelectAreaBinding binding;
    private OnResult mOnResult;
    private DBManager mDBManager;
    private int mProIndex;
    private int mCityIndex;
    private int mCountyIndex;


    public DropdownAreaDialog(Context context, View anchorView, OnResult onResult) {
        super(context, true);
        mAnchorView = anchorView;
        setCanceledOnTouchOutside(true);
        mOnResult = onResult;
        mProvinceAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dropdown_province);
        mCityAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dropdown_city);
        mCountyAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dropdown_county);
        mDBManager = new DBManager(mContext);
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_select_area, mLlControlHeight, false);

        //省
        binding.provinceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        binding.provinceList.dispatchSetSelected(true);
        binding.provinceList.setAdapter(mProvinceAdapter);
        //市
        binding.cityList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        binding.cityList.dispatchSetSelected(true);
        binding.cityList.setAdapter(mCityAdapter);
        //县
        binding.countyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        binding.countyList.dispatchSetSelected(true);
        binding.countyList.setAdapter(mCountyAdapter);

        //省点击
        binding.provinceList.setOnItemClickListener((parent, view, position, id) -> {
            mProIndex = position;
            mCityIndex = 0;
            mCountyIndex = 0;
            if (position == 0) {
                mOnResult.onResult(mProIndex, mCityIndex, mCountyIndex, mProvinceAdapter.getItem(position));
                dismiss();
                return;
            }
            binding.cityList.setVisibility(View.VISIBLE);
            binding.countyList.setVisibility(View.INVISIBLE);
            mCityAdapter.clear();
            mCountyAdapter.clear();
            //
            mCityAdapter.addAll(getAreaByChildId(mProvinceAdapter.getItem(position).i));
            binding.cityList.setItemChecked(-1, true);
        });
        //市点击
        binding.cityList.setOnItemClickListener((parent, view, position, id) -> {
            mCityIndex = position;
            mCountyIndex = 0;

            if (position == 0) {
                mOnResult.onResult(mProIndex, mCityIndex, mCountyIndex, mProvinceAdapter.getItem(mProIndex));
                dismiss();
                return;
            }
            binding.countyList.setVisibility(View.VISIBLE);
            mCountyAdapter.clear();
            mCountyAdapter.addAll(getAreaByChildId(mCityAdapter.getItem(position).i));
            binding.countyList.setItemChecked(-1, true);
        });
        binding.countyList.setOnItemClickListener((parent, view, position, id) -> {
            mCountyIndex = position;
            City area = null;
            if (position == 0) {
                area = mCityAdapter.getItem(mCityIndex);
            } else {
                area = mCountyAdapter.getItem(position);
            }
            mOnResult.onResult(mProIndex, mCityIndex, mCountyIndex, area);
            dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void setUiBeforShow() {

    }

    //根据父id查找下级
    private List<City> getAreaByChildId(String parentId) {
        List<City> cityList = mDBManager.searchByP(parentId);
        cityList.add(0, new City("-1", "全部"));
        return cityList;
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

    public interface OnResult {
        void onResult(int proIndex, int cityIndex, int countyIndex, City area);
    }


    public void show(int proIndex, int cityIndex, int countyIndex) {

        mProIndex = proIndex;
        mCityIndex = cityIndex;
        mCountyIndex = countyIndex;

        show();

        LogUtil.e("=========>>>" + mProIndex + " , " + mCityIndex + " , " + mCountyIndex);

        mLlTop.postDelayed(() -> {


            List<City> provinceList = mDBManager.searchByLevel(City.LEVEL_PROVINCE);
            provinceList.add(0, new City("0", "全国"));
            mProvinceAdapter.addAll(provinceList);
            binding.provinceList.setSelection(mProIndex);
            binding.provinceList.setItemChecked(mProIndex, true);
            if (mProIndex > 0) {

                mCityAdapter.addAll(getAreaByChildId(mProvinceAdapter.getItem(mProIndex).i));
                binding.cityList.setSelection(mCityIndex);
                binding.cityList.setItemChecked(mCityIndex, true);
                binding.cityList.setVisibility(View.VISIBLE);

                if (mCityIndex > 0) {

                    mCountyAdapter.addAll(getAreaByChildId(mCityAdapter.getItem(mCityIndex).i));
                    binding.countyList.setSelection(mCountyIndex);
                    binding.countyList.setItemChecked(mCountyIndex, true);
                    binding.countyList.setVisibility(View.VISIBLE);
                }
            }
        }, 1);

    }
}
