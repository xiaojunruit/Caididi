package com.laoodao.caididi.event;

import com.laoodao.caididi.retrofit.main.City;

/**
 * Created by Administrator on 2017/2/23.
 */

public class CitySelectEvent {

    public City city;

    public CitySelectEvent(City city) {
        this.city = city;
    }
}
