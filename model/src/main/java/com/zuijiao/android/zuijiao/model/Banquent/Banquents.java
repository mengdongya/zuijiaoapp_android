package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Banquents implements Serializable{
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

    public ArrayList<Banner> getBanners() {
        return banners;
    }

    //    private Integer lastIdentifer ;
    @SerializedName("banners")
    private ArrayList<Banner> banners ;
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

    public class Banner implements  Serializable{
        @SerializedName("url")
        private String linkUrl;
        @SerializedName("imageUrl")
        private String imageUrl;

        public String getLinkUrl() {
            return linkUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
