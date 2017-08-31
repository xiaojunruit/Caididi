
package com.laoodao.caididi.common.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ezy on 15-9-29.
 */
public class FarlandPagination<ItemType> {
    public int total;
    @SerializedName("page_size")
    public int size;
    public int page;
    public int cursor;
    @SerializedName("land_num")
    public int landNum;
    public String acreage;
    public List<ItemType> items;
}
