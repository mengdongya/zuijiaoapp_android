package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Banquents {
    @SerializedName("banner")
    private Banner banner;
    @SerializedName("items")
    private ArrayList<Banquent> banquentList;

    public String getBannerLinkUrl() {
        return banner.linkUrl;
    }

    public String getBannerImageUrl() {
        return banner.imageUrl;
    }

    public ArrayList<Banquent> getBanquentList() {
        return banquentList;
    }

    class Banner {
        @SerializedName("url")
        private String linkUrl;
        @SerializedName("imageUrl")
        private String imageUrl;

    }
}
