package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 3/16/15.
 * <p>
 * 小型用户类(包含用户基本信息)
 */
public class TinyUsers {

    @SerializedName("items")
    private List<TinyUser> users;

    public List<TinyUser> getUsers() {
        return users;
    }
}
