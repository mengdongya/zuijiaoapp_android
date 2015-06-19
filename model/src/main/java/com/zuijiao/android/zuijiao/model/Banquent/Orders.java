package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Orders {
    @SerializedName("items")
    private List<Order> orderList;
}
