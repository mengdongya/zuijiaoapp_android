package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 3/18/15.
 * <p>
 * 喜欢菜品用户列表
 */
public class WouldLikeToEatUsers {
    @SerializedName("items")
    private List<WouldLikeToEatUser> users;
    @SerializedName("itemTotalCount")
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public List<WouldLikeToEatUser> getUsers() {
        return users;
    }
}
