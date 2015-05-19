package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 5/19/15.
 */
public class Language {

    @SerializedName("value")
    private String name;

    @SerializedName("title")
    private String code;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
