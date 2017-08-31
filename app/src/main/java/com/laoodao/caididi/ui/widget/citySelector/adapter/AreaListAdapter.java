package com.laoodao.caididi.ui.widget.citySelector.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.retrofit.main.Pos;
import com.laoodao.caididi.ui.widget.citySelector.utils.LocateState;
import com.laoodao.caididi.ui.widget.citySelector.utils.PinyinUtils;
import com.laoodao.caididi.ui.widget.citySelector.view.WrapHeightGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ezy.lite.util.UI;

/**
 * author zaaach on 2016/1/26.
 */
public class AreaListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 4;

    private Context mContext;
    private LayoutInflater inflater;
    private List<City> mCities;
    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    private OnCityClickListener onCityClickListener;
    private int locateState = LocateState.LOCATING;
    private Pos locatedCity;
    private HotCityGridAdapter mHistoryCityAdapter;
    private City mCurrentCity;
    private HotCityGridAdapter mCurrentCountyAdapter;

    private City mProvince;
    private City mCity;
    private City mCounty;
    private City mTownship;


    public void setIsOpen(boolean i) {
        this.isOpen = i;
        notifyDataSetChanged();
    }

    public void setProvince(City province) {
        this.mProvince = province;
        notifyDataSetChanged();
    }

    public void setTownship(City township) {
        this.mTownship = township;
        notifyDataSetChanged();
    }

    public void setCity(City city) {
        this.mCity = city;
        notifyDataSetChanged();
    }

    public void setCounty(City county) {
        this.mCounty = county;
        notifyDataSetChanged();
    }

    public City getmCounty() {
        return mCounty;
    }

    public City getmTownship() {
        return mTownship;
    }

    public City getmCity() {
        return mCity;
    }

    public City getmProvince() {
        return mProvince;
    }

    public AreaListAdapter(Context mContext, List<City> mCities, List<City> historyCityList, City currentCity) {
        this.mContext = mContext;
        this.mCities = mCities;
        this.inflater = LayoutInflater.from(mContext);
        if (mCities == null) {
            mCities = new ArrayList<>();
        }
        mCities.add(0, new City("当前", "", "0"));
        mCities.add(1, new City("定位", "", "1"));
        mCities.add(2, new City("最近", "", "2"));
        int size = mCities.size();
        letterIndexes = new HashMap<>();
        sections = new String[size];
        for (int index = 0; index < size; index++) {
            //当前城市拼音首字母
            String currentLetter = PinyinUtils.getFirstLetter(mCities.get(index).py);
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? PinyinUtils.getFirstLetter(mCities.get(index - 1).py) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, index);
                sections[index] = currentLetter;
            }
        }
        mHistoryCityAdapter = new HotCityGridAdapter(mContext, historyCityList);
        mCurrentCountyAdapter = new HotCityGridAdapter(mContext);
    }

    public void setHistory(List<City> cityList) {
        mHistoryCityAdapter.addAll(cityList);
        notifyDataSetChanged();
    }

    public void clearHistory() {
        mHistoryCityAdapter.clear();
        notifyDataSetChanged();
    }

    private boolean isOpen;

    public void openCounty() {
        isOpen = true;
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, Pos city) {
        this.locateState = state;
        this.locatedCity = city;
        if (locatedCity != null) {
            mProvince = locatedCity.province;
            mCity = locatedCity.city;
            mCounty = locatedCity.county;
        }
        notifyDataSetChanged();
    }


    public void updateCounty(List<City> countyList) {
        this.mCurrentCountyAdapter.addAll(countyList);
    }

    /**
     * 获取字母索引的位置
     *
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position < VIEW_TYPE_COUNT - 1 ? position : VIEW_TYPE_COUNT - 1;
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public City getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isShow;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_current_city, parent, false);
                TextView tvProvince = (TextView) view.findViewById(R.id.current_select_city);
                TextView tvCity = (TextView) view.findViewById(R.id.city);
                TextView tvCounty = (TextView) view.findViewById(R.id.county);
                TextView btnChooesCity = (TextView) view.findViewById(R.id.btn_chooes_county);
                TextView xiang = (TextView) view.findViewById(R.id.xiang);
                WrapHeightGridView county = (WrapHeightGridView) view.findViewById(R.id.grid_county);
                county.setAdapter(mCurrentCountyAdapter);

                if (mProvince != null) {
                    tvProvince.setText("当前：" + mProvince.n);
                } else {
                    if (locatedCity != null) {
                        tvProvince.setText("当前：" + locatedCity.province.n);
                    } else {
                        tvProvince.setText("当前：未选择");
                    }
                }
                if (mCity != null) {
                    tvCity.setText(mCity.n);
                } else {
                    tvCity.setText("");
                }
                if (mCounty != null) {
                    tvCounty.setText(mCounty.n);
                } else {
                    tvCounty.setText("");
                }

                if (mTownship != null) {
                    xiang.setText(mTownship.n);
                } else {
                    xiang.setText("");
                }
                btnChooesCity.setOnClickListener(view1 -> {
                    int v = county.getVisibility();
                    county.setVisibility(v == View.GONE ? View.VISIBLE : View.GONE);
                    if (onChooesCountyListener != null) {
                        if (mProvince != null) {
                            onChooesCountyListener.onChooesCounty(mProvince);
                        }
                    }
                });

                if (isOpen) {
                    isOpen = false;
                    county.setVisibility(View.VISIBLE);
                }

                /**
                 * 点击市选项
                 */
                county.setOnItemClickListener((adapterView, view1, i, l) -> {

                    if (onChooesCountyListener != null) {
                        City item = mCurrentCountyAdapter.getItem(i);
                        mCity = item;
                        tvCity.setText(item.n);
                        onChooesCountyListener.onCountyClick(item);

                        isShow = true;
                    }
                });

                break;
            case 1:     //定位

                view = inflater.inflate(R.layout.cp_view_locate_city, parent, false);
                ViewGroup container = (ViewGroup) view.findViewById(R.id.layout_locate);
                TextView state = (TextView) view.findViewById(R.id.tv_located_city);
                switch (locateState) {
                    case LocateState.LOCATING:
                        state.setText("正在定位...");
                        if (onCityClickListener != null) {
                            onCityClickListener.location();
                        }
                        break;
                    case LocateState.FAILED:
                        state.setText("定位失败");
                        break;
                    case LocateState.SUCCESS:
                        state.setText(locatedCity.city.n);

                        break;
                }
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateState == LocateState.FAILED) {
                            //重新定位
                            if (onCityClickListener != null) {
                                onCityClickListener.onLocateClick();
                            }
                        } else if (locateState == LocateState.SUCCESS) {
                            //返回定位城市
                            if (onChooesCountyListener != null) {
                                mCity = locatedCity.city;
                                mProvince = locatedCity.province;
                                notifyDataSetChanged();
                                onChooesCountyListener.onCountyClick(mCity);
                            }
                        }
                    }
                });

//                if (isShow) {
//                    ViewGroup.LayoutParams params = view.getLayoutParams();
//                    params.height = 1;
//                    view.setLayoutParams(params);
//                } else {
//                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//                }

                if (isShow) {
                    AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
                    params.height = 1;
                    view.setLayoutParams(params);
                } else {
                    view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

                }

                break;
            case 2:     //热门
                view = inflater.inflate(R.layout.cp_view_hot_city, parent, false);
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                View lo = view.findViewById(R.id.location);
                LinearLayout ll = (LinearLayout) view.findViewById(R.id.layout_locate);
                TextView textView = (TextView) view.findViewById(R.id.tv_located_city);
                tvTitle.setText("选择区县");
                WrapHeightGridView gridView = (WrapHeightGridView) view.findViewById(R.id.gridview_hot_city);

                gridView.setAdapter(mHistoryCityAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onCountyClick != null) {
                            City item = mHistoryCityAdapter.getItem(position);
                            onCountyClick.onCountyClick(item);
                        }
                    }
                });
                if (mHistoryCityAdapter.getCount() > 0) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                if (!isShow) {
                    lo.setVisibility(View.GONE);
                } else {
                    lo.setVisibility(View.VISIBLE);
                }
                switch (locateState) {
                    case LocateState.LOCATING:
                        textView.setText("正在定位...");
                        if (onCityClickListener != null) {
                            onCityClickListener.location();
                        }
                        break;
                    case LocateState.FAILED:
                        textView.setText("定位失败");
                        break;
                    case LocateState.SUCCESS:
                        textView.setText(locatedCity.city.n);

                        break;
                }

                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateState == LocateState.FAILED) {
                            //重新定位
                            if (onCityClickListener != null) {
                                onCityClickListener.onLocateClick();
                            }
                        } else if (locateState == LocateState.SUCCESS) {
                            //返回定位城市
                            if (onChooesCountyListener != null) {
                                mCity = locatedCity.city;
                                mProvince = locatedCity.province;
                                isShow = false;
                                notifyDataSetChanged();
                                onChooesCountyListener.onCountyClick(mCity);

                            }
                        }
                    }
                });
                break;
            case 3:     //所有
                if (view == null) {
                    view = inflater.inflate(R.layout.item_city_listview, parent, false);
                    holder = new CityViewHolder();
                    holder.letter = (TextView) view.findViewById(R.id.tv_item_city_listview_letter);
                    holder.name = (TextView) view.findViewById(R.id.tv_item_city_listview_name);
                    view.setTag(holder);
                } else {
                    holder = (CityViewHolder) view.getTag();
                }
                if (position >= 1) {
                    final String city = mCities.get(position).n;
                    holder.name.setText(city);
                    String currentLetter = PinyinUtils.getFirstLetter(mCities.get(position).py);
                    String previousLetter = position >= 1 ? PinyinUtils.getFirstLetter(mCities.get(position - 1).py) : "";
                    if (!TextUtils.equals(currentLetter, previousLetter)) {
                        holder.letter.setVisibility(View.VISIBLE);
                        holder.letter.setText(currentLetter);
                    } else {
                        holder.letter.setVisibility(View.GONE);
                    }
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            City city1 = mCities.get(position);
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(city1);
                            }
                        }
                    });
                }

                break;
        }
        return view;
    }

    public static class CityViewHolder {
        TextView letter;
        TextView name;
    }


    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public interface OnChooesCountyListener {
        void onChooesCounty(City city);

        void onCountyClick(City city);
    }

    private OnChooesCountyListener onChooesCountyListener;

    public void setOnChooesCountyListener(OnChooesCountyListener listener) {
        this.onChooesCountyListener = listener;
    }

    private OnCountyClickListener onCountyClick;

    public void setOnCountyClick(OnCountyClickListener onCountyClick) {
        this.onCountyClick = onCountyClick;
    }

    public interface OnCountyClickListener {
        void onCountyClick(City county);
    }

    public interface OnCityClickListener {
        void onCityClick(City city);

        void onLocateClick();

        void location();
    }
}
