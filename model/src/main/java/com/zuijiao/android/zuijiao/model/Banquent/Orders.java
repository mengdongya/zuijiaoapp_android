package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Orders {
    @SerializedName("items")
    private List<Order> orderList;
    @SerializedName("time")
    private Date currentServerTime;
    @SerializedName("data")
    private Order order;

    public Order getOrder() {
        return order;
    }

    private long pullTime = System.currentTimeMillis();

    public long getCurrentServerTime() {
        return (currentServerTime.getTime() + (System.currentTimeMillis() - pullTime))/1000;
    }

    public List<Order> getOrderList() {
        return orderList;
    }
}
