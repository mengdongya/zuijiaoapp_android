package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yitianhao on 2015/7/13.
 */
public class CommentEvent implements Serializable {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("title")
    private String title;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("tiem")
    private String tiem;

    public Integer getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTiem() {
        return tiem;
    }
}
