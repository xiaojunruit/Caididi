package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by WORK on 2017/1/7.
 */

public class Plants implements Serializable {

    public String name;
    public String cover;
    public int id;
    @SerializedName("is_insect")
    public boolean isInsect;

    public Plants() {
    }

    public Plants(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
