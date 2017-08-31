package com.laoodao.caididi.retrofit.main;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/4.
 */

public class City implements Serializable {


    public static final String LEVEL_PROVINCE = "1";
    public static final String LEVEL_CITY = "2";
    public static final String LEVEL_COUNTY = "3";


    public String i;
    public String n;
    public String py;
    public String d;
    public String p;
    public String loc;

    @Override
    public String toString() {
        return n;
    }


    public City() {
    }

    public City(String i, String n) {
        this.i = i;
        this.n = n;
    }

    public City(String n, String i, String py) {
        this.i = i;
        this.n = n;
        this.py = py;
    }


    public City(String d, String i, String n, String p, String py) {
        this.i = i;
        this.n = n;
        this.py = py;
        this.p = p;
        this.d = d;
    }


    public City(String n, String i, String loc, String py) {
        this.i = i;
        this.n = n;
        this.loc = loc;
        this.py = py;
    }
}
