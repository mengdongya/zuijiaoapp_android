package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 3/13/15.
 * <p>
 * 美食
 */

public class Gourmets {

    @SerializedName("items")
    private List<Gourmet> gourmets;
    @SerializedName("itemTotalCount")
    private Integer totalCount;

    public List<Gourmet> getGourmets() {
        return gourmets;
    }

    public Integer getTotalCount() {
        return totalCount;
    }
}

