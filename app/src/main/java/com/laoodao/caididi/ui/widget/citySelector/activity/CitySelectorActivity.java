package com.laoodao.caididi.ui.widget.citySelector.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityCitySelectorBinding;
import com.laoodao.caididi.event.CitySelectEvent;
import com.laoodao.caididi.event.ProvinceSelectEvent;
import com.laoodao.caididi.event.TabAddressEvent;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.ui.my.activity.FollowPeopleActivity;
import com.laoodao.caididi.ui.widget.citySelector.adapter.CityListAdapter;
import com.laoodao.caididi.ui.widget.citySelector.adapter.ResultListAdapter;
import com.laoodao.caididi.ui.widget.citySelector.db.DBManager;
import com.laoodao.caididi.ui.widget.citySelector.db.DatabaseHelper;
import com.laoodao.caididi.ui.widget.citySelector.utils.LocateState;
import com.laoodao.caididi.ui.widget.citySelector.utils.PinyinUtils;
import com.laoodao.caididi.ui.widget.citySelector.view.SideLetterBar;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.Prefs;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/2/22.
 */

public class CitySelectorActivity extends BaseActivity implements TextWatcher {

    public String mType = City.LEVEL_CITY;
    private ActivityCitySelectorBinding binding;
    private DBManager mDbManager;
    private List<City> mAllCities;
    private List<City> mHistoryCityList = new ArrayList<>();
    private CityListAdapter mCityAdapter;
    private DatabaseHelper mDatabaseHepler;
    private ResultListAdapter mResultAdapter;

    public static void start(Context context, String type) {

        Bundle bundle = new Bundle();
        bundle.putString("type", type);
//        bundle.putBoolean();
        ContextUtil.startActivity(context, CitySelectorActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_selector);
        mDatabaseHepler = new DatabaseHelper(this);
        mType = getIntent().getStringExtra("type");
        initData();
        initView();
        initLocation();
    }


    /**
     * 定位
     */
    private void initLocation() {
        if (Global.locator == null) {
            return;
        }
        Global.locator.registerLocationListener(mLocationListener);
        Global.locator.start();
        Global.locator.requestLocation();
    }

    @Override
    protected void onDestroy() {
        Global.locator.unRegisterLocationListener(mLocationListener);
        Global.locator.stop();
        super.onDestroy();
    }

    /**
     * 定位
     */
    BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null) {
                LogUtil.e("定位失败~");
                mCityAdapter.updateLocateState(LocateState.FAILED, new City("", "定位失败"));
                return;
            }


            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }


            Global.locator.stop();
            Global.setLocation(location);
            LogUtil.e("定位成功" + sb.toString() + "-" + location.getAddress() + "" + location.getLongitude() + "," + location.getLatitude());
            City city = new City();
            String pos = location.getLongitude() + "," + location.getLatitude();
            API.main().getPos(pos).compose(transform()).subscribe(result -> {
                if (result.data.city == null) {
                    return;
                }
                if (mType.equals(City.LEVEL_CITY)) {
                    city.n = result.data.city.n;
                    city.i = result.data.city.i;
                    city.p = result.data.province.i;
                } else {
                    city.n = result.data.province.n;
                    city.i = result.data.province.i;
                    city.p = result.data.province.p;
                }
                city.py = PinyinUtils.getFirstLetter(city.n);
                city.d = mType;
                mCityAdapter.updateLocateState(LocateState.SUCCESS, city);
            });
        }
    };


    /**
     * 侧边滑动
     */
    SideLetterBar.OnLetterChangedListener mOnLetterListener = new SideLetterBar.OnLetterChangedListener() {
        @Override
        public void onLetterChanged(String letter) {
            int position = mCityAdapter.getLetterPosition(letter);
            binding.allCityList.setSelection(position);
        }
    };
    boolean isScroll;

    private void initView() {
        mResultAdapter = new ResultListAdapter(this);
        binding.searchResultList.setAdapter(mResultAdapter);
        binding.allCityList.setAdapter(mCityAdapter);
        binding.sideLetterBar.setOverlay(binding.tvLetterOverlay);
        binding.sideLetterBar.setOnLetterChangedListener(mOnLetterListener);
        binding.allCityList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
                    isScroll = true;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (!isScroll) {
                    return;
                }
                if (firstVisibleItem > 3) {
                    City city = mAllCities.get(firstVisibleItem);
                    String firstLetter = PinyinUtils.getFirstLetter(city.py);
                    binding.sideLetterBar.showOverlay(firstLetter);
                }
            }
        });
        binding.etSearchCity.addTextChangedListener(this);
        binding.btnClear.setOnClickListener(v -> {
            binding.etSearchCity.setText("");
            binding.btnClear.setVisibility(View.GONE);
            binding.noResult.setVisibility(View.GONE);
            binding.searchResultList.setVisibility(View.GONE);
        });
        //搜索列表点击
        binding.searchResultList.setOnItemClickListener((adapterView, view, i, l) -> {
            City city = mResultAdapter.getItem(i);
            mDatabaseHepler.addToHistoryCity(city);
            back(city);
        });

    }

    private void initData() {
        mDbManager = new DBManager(this);
        mDbManager.copyDBFile();
        mAllCities = mDbManager.getAllArea(mType);
        mHistoryCityList = mDatabaseHepler.getHistoryCity(mType);

        City currentCity = null;
        if (mHistoryCityList != null && mHistoryCityList.size() > 0) {
            currentCity = mHistoryCityList.get(0);
        }

        mCityAdapter = new CityListAdapter(this, mAllCities, mHistoryCityList, currentCity);
        mCityAdapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                mDatabaseHepler.addToHistoryCity(city);
                back(city);
            }

            @Override
            public void onLocateClick() {
                mCityAdapter.updateLocateState(LocateState.LOCATING, null);
            }

            @Override
            public void location() {
                Global.locator.start();
            }
        });
        mCityAdapter.setOnChooesCountyListener(city -> {


            Observable
                    .create(new Observable.OnSubscribe<List<City>>() {
                        @Override
                        public void call(Subscriber<? super List<City>> subscriber) {
                            List<City> countyList = mDbManager.searchByP(city.i);
                            LogUtil.e("countLIst" + countyList.size());
                            subscriber.onNext(countyList);
                            subscriber.onCompleted();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<City>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(List<City> result) {
                            mCityAdapter.updateCounty(result);
                        }
                    });


        });

    }

    private void back(City city) {
        switch (mType) {
            case City.LEVEL_PROVINCE:
                RxBus.post(new ProvinceSelectEvent(city));

                break;
            case City.LEVEL_CITY:
                RxBus.post(new CitySelectEvent(city));

                break;
        }

        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            binding.btnClear.setVisibility(View.GONE);
            binding.noResult.setVisibility(View.GONE);
            binding.searchResultList.setVisibility(View.GONE);
        } else {
            binding.btnClear.setVisibility(View.VISIBLE);
            binding.searchResultList.setVisibility(View.VISIBLE);

            Observable
                    .create(new Observable.OnSubscribe<List<City>>() {
                        @Override
                        public void call(Subscriber<? super List<City>> subscriber) {
                            List<City> result = mDbManager.searchCity(mType, keyword);
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<City>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(List<City> result) {
                            if (result == null || result.size() == 0) {
                                binding.noResult.setVisibility(View.VISIBLE);
                            } else {
                                binding.noResult.setVisibility(View.GONE);
                                mResultAdapter.changeData(result);
                            }
                        }
                    });

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {


    }
}
