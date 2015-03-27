package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 3/13/15.
 */
public class Restaurants {

    private List<Restaurant> restaurants;

}

class Restaurant {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("title")
    private String title;
    @SerializedName("address")
    private String address;
}
