package com.laoodao.caididi.retrofit.main;

/**
 * Created by WORK on 2017/1/3.
 */

public class Location {

    public String address;

    public String city;

    public String lon;

    public String lat;

    public boolean isSuccess;


    public Location(String address) {
        this.address = address;
    }

    public Location(String address, String city, String lon, String lat) {
        this.address = address;
        this.city = city;
        this.lon = lon;
        this.lat = lat;
    }

}
