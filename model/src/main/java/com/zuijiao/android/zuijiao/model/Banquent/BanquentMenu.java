package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mengdongya on 2015/7/29.
 */
public class BanquentMenu implements Serializable{
    @SerializedName("categoryID")
    private Integer categoryID;
    @SerializedName("categoryName")
    private String categoryName;
    @SerializedName("dishes")
    private ArrayList<String> dishes;

    public Integer getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ArrayList<String> getDishes() {
        return dishes;
    }
}
