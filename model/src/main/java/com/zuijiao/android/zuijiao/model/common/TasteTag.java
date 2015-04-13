package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 3/30/15.
 */
public class TasteTag {

    @SerializedName("title")
    private String name;
    @SerializedName("imageUrl")
    private String imageURL;

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }
}
