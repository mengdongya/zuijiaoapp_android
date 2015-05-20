package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 5/19/15.
 */
public class Languages {
    @SerializedName("itemCount")
    private Integer count;

    @SerializedName("items")
    private List<Language> languages;

    public Integer getCount() {
        return count;
    }

    public List<Language> getLanguages() {
        return languages;
    }
}
