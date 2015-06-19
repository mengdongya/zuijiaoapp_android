package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class BanquentForTheme {

    @SerializedName("data") private Banquent helper;

    public Banquent getHelper() {
        return helper;
    }
}
