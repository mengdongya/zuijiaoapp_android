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
    @SerializedName("itemCount")
    private Integer itemCount ;
    @SerializedName("totalCount")
    private Integer totalCount ;
    @SerializedName("previousCursor")
    private Integer previousCursor ;
    @SerializedName("nextCursor")
    private Integer nextCursor ;
    private Integer lastIdentifer ;

    public String getBannerLinkUrl() {
        return banner.linkUrl;
    }

    public String getBannerImageUrl() {
        return banner.imageUrl;
    }

    public ArrayList<Banquent> getBanquentList() {
        return banquentList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public Integer getPreviousCursor() {
        return previousCursor;
    }

    public Integer getNextCursor() {
        return nextCursor;
    }

    class Banner {
        @SerializedName("url")
        private String linkUrl;
        @SerializedName("imageUrl")
        private String imageUrl;

    }
}
