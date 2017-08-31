package com.laoodao.caididi.ui.widget.citySelector.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityCitySelectorBinding;
import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.ui.widget.citySelector.adapter.AreaListAdapter;
import com.laoodao.caididi.ui.widget.citySelector.adapter.ResultListAdapter;
import com.laoodao.caididi.ui.widget.citySelector.db.DBManager;
import com.laoodao.caididi.ui.widget.citySelector.db.DatabaseHelper;
import com.laoodao.caididi.ui.widget.citySelector.utils.LocateState;
import com.laoodao.caididi.ui.widget.citySelector.utils.PinyinUtils;
import com.laoodao.caididi.ui.widget.citySelector.view.SideLetterBar;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/2/28.
 */

public class AreaSelectorActivity extends BaseActivity implements TextWatcher {


    public final static int REQUEST_AREA = 65;
    public static String RESULT = "RESULT";

    public String mType = City.LEVEL_CITY;
    private ActivityCitySelectorBinding binding;
    private DBManager mDbManager;
    private List<City> mAllCities;
    private List<City> mHistoryCityList = new ArrayList<>();
    private AreaListAdapter mCityAdapter;
    private DatabaseHelper mDatabaseHepler;
    private ResultListAdapter mResultAdapter;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AreaSelectorActivity.class);
        activity.startActivityForResult(intent, REQUEST_AREA);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_selector);
        mDatabaseHepler = new DatabaseHelper(this);
//        mType = getIntent().getStringExtra("type");
        initData();
        initView();
        initLocation();
    }

    /**
     * 定位
     */
    private void initLocation() {

        Global.locator.registerLocationListener(mLocationListener);
        Global.locator.start();
        Global.locator.requestLocation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.locator.unRegisterLocationListener(mLocationListener);
        Global.locator.stop();
    }

    /**
     * 定位
     */
    BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null) {
                mCityAdapter.updateLocateState(LocateState.FAILED, null);
                return;
            }
            Global.locator.stop();
            Global.setLocation(location);
            LogUtil.e("定位成功" + location.getLongitude() + "," + location.getLatitude() + "location");
            City city = new City();
            String pos = location.getLongitude() + "," + location.getLatitude();
            API.main().getPos(pos).compose(transform()).subscribe(result -> {
                if (result.data.city == null || result.data.province == null) {
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
                mCityAdapter.updateLocateState(LocateState.SUCCESS, result.data);

                initCityById(city.i);
            });
        }
    };


    private void initCityById(String id) {
        Observable
                .create(new Observable.OnSubscribe<List<City>>() {
                    @Override
                    public void call(Subscriber<? super List<City>> subscriber) {
                        List<City> countyList = mDbManager.searchByP(id);
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
                        mCityAdapter.setHistory(result);
                    }
                });
    }

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
    private boolean isScroll;

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
        });

    }


    public void back(City province, City city, City county, City xiang) {

        Intent intent = new Intent();
        intent.putExtra("province", province);
        intent.putExtra("city", city);
        intent.putExtra("county", county);
        intent.putExtra("xiang", xiang);
        setResult(RESULT_OK, intent);
        finish();

    }

    private void initData() {
        mDbManager = new DBManager(this);
        mDbManager.copyDBFile();
        mAllCities = mDbManager.getAllArea(City.LEVEL_PROVINCE);
        mCityAdapter = new AreaListAdapter(this, mAllCities, mHistoryCityList, null);
        mCityAdapter.setOnCityClickListener(new AreaListAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                if ("0".equals(city.p)) {
                    mCityAdapter.clearHistory();
                    binding.getRoot().postDelayed(() -> {
                        binding.allCityList.setSelection(0);
                    }, 100);
                    mCityAdapter.setProvince(city);
                    mCityAdapter.setCity(null);
                    mCityAdapter.setCounty(null);
                    mCityAdapter.setTownship(null);


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
                                    mCityAdapter.setIsOpen(true);
                                }
                            });

                }
            }

            @Override
            public void onLocateClick() {
                mCityAdapter.updateLocateState(LocateState.LOCATING, null);
            }

            @Override
            public void location() {
                Global.locator.start();
                Global.locator.requestLocation();
            }
        });
        mCityAdapter.setOnChooesCountyListener(new AreaListAdapter.OnChooesCountyListener() {
            @Override
            public void onChooesCounty(City city) {
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
            }

            @Override
            public void onCountyClick(City city) {

                LogUtil.e("===================>>>>" + city.n);
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
//                                mCityAdapter.updateCounty(result);
                                mCityAdapter.setCity(city);
                                mCityAdapter.setCounty(null);
                                mCityAdapter.setTownship(null);
                                mCityAdapter.setHistory(result);

                                LogUtil.e(result.size() + "======================");
                            }
                        });
            }
        });

        mCityAdapter.setOnCountyClick(county -> {
            List<City> xian = mDbManager.searchByP(county.i);
            mCityAdapter.setTownship(null);
            if ("3".equals(county.d)) {
                mCityAdapter.setCounty(county);
            } else {
                mCityAdapter.setTownship(county);
            }
            if (xian != null && xian.size() > 0) {
                mCityAdapter.setHistory(xian);
            } else {
                back(mCityAdapter.getmProvince(), mCityAdapter.getmCity(), mCityAdapter.getmCounty(), mCityAdapter.getmTownship());
            }

        });
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
