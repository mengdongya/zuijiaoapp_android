package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 5/7/15.
 */
public class GourmetTags {
    @SerializedName("items")
    List<String> tags;

    public List<String> getTags() {
        return tags;
    }
}
