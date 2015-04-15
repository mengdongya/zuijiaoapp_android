package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 3/13/15.
 */
public class TasteTags {

    @SerializedName("items")
    private List<TasteTag> tags;
    @SerializedName("itemCount")
    private Integer count;

    public List<TasteTag> getTags() {
        return tags;
    }

    public Integer getCount() {
        return count;
    }
}

