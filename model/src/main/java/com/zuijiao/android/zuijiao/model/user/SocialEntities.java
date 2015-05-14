package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 4/28/15.
 */
public class SocialEntities {

    @SerializedName("items")
    private List<SocialEntity> entities;

    public List<SocialEntity> getUsers() {
        return entities;
    }
}
