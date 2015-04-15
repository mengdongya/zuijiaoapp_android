package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 3/30/15.
 */
public class Restaurant {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("title")
    private String title;
    @SerializedName("address")
    private String address;

    public Integer getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }
}
