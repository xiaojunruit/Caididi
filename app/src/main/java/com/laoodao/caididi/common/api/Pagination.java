package com.laoodao.caididi.common.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ezy on 15-9-29.
 */
public class Pagination<ItemType> {
    public int total;
    @SerializedName("page_size")
    public int size;
    public int page;
    public int cursor;
    public List<ItemType> items;

    @SerializedName("total_balance")
    public String totalBalance;
}
