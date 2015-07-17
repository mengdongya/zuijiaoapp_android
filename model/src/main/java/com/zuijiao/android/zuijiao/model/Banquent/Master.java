package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

/**
 * Created by user on 6/19/15.
 */
public class Master extends TinyUser {
    @SerializedName("userID")
    private Integer userId ;
    public int getUserId(){
        return userId ;
    }
}
