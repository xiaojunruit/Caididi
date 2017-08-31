package com.laoodao.caididi.retrofit.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CityNode {
    @SerializedName(value = "id", alternate = {"cityId"})
    public int id;
    public String name;
    @Expose(serialize = false)
    public int bdId;
    @Expose(serialize = false)
    public List<CityNode> child = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    public boolean isCity;


    public CityNode() {

    }

    public CityNode(String name) {
        this.name = name;
    }

    public CityNode(int id, String name, int bdId) {
        this.id = id;
        this.name = name;
        this.bdId = bdId;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CityNode && id == ((CityNode) obj).id;
    }

    @Override
    public String toString() {
        return name + "(" + id + "," + bdId + ")";
    }
}