package com.laoodao.caididi.ui.widget.citySelector.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.ui.widget.citySelector.db.DBManager;
import com.laoodao.caididi.ui.widget.citySelector.utils.LocateState;
import com.laoodao.caididi.ui.widget.citySelector.utils.PinyinUtils;
import com.laoodao.caididi.ui.widget.citySelector.view.WrapHeightGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class CityListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 4;

    private Context mContext;
    private LayoutInflater inflater;
    private List<City> mCities;
    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    private OnCityClickListener onCityClickListener;
    private int locateState = LocateState.LOCATING;
    private City locatedCity;
    private HotCityGridAdapter mHistoryCityAdapter;
    private City mCurrentCity;

    private HotCityGridAdapter mCurrentCountyAdapter;
    private boolean isOpen;

    public CityListAdapter(Context mContext, List<City> mCities, List<City> historyCityList, City currentCity) {
        this.mContext = mContext;
        this.mCities = mCities;
        this.mCurrentCity = currentCity;
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
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, City city) {
        this.locateState = state;
        this.locatedCity = city;
        notifyDataSetChanged();
    }


    public void setCurrentCity(City city) {
        this.mCurrentCity = city;
        notifyDataSetChanged();
    }

    public void updateCounty(List<City> countyList) {
        this.mCurrentCountyAdapter.addAll(countyList);
//        notifyDataSetChanged();
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

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_current_city, parent, false);
                TextView current = (TextView) view.findViewById(R.id.current_select_city);
                TextView btnChooesCounty = (TextView) view.findViewById(R.id.btn_chooes_county);
                WrapHeightGridView county = (WrapHeightGridView) view.findViewById(R.id.grid_county);
                county.setAdapter(mCurrentCountyAdapter);
                if (mCurrentCity == null || TextUtils.isEmpty(mCurrentCity.n)) {
                    current.setText("当前：未选择");
                } else {
                    current.setText("当前：" + mCurrentCity.n);
                }
                btnChooesCounty.setVisibility(View.GONE);

                btnChooesCounty.setOnClickListener(view1 -> {
                    isOpen = !isOpen;
//
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
                        state.setText(locatedCity.n);
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
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(locatedCity);
                            }
                        }
                    }
                });
                break;
            case 2:     //热门
                view = inflater.inflate(R.layout.cp_view_hot_city, parent, false);
                WrapHeightGridView gridView = (WrapHeightGridView) view.findViewById(R.id.gridview_hot_city);

                gridView.setAdapter(mHistoryCityAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onCityClickListener != null) {
                            City item = mHistoryCityAdapter.getItem(position);
                            onCityClickListener.onCityClick(item);
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
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(mCities.get(position));
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
    }

    private OnChooesCountyListener onChooesCountyListener;

    public void setOnChooesCountyListener(OnChooesCountyListener listener) {
        this.onChooesCountyListener = listener;
    }

    public interface OnCityClickListener {
        void onCityClick(City city);

        void onLocateClick();

        void location();
    }
}
