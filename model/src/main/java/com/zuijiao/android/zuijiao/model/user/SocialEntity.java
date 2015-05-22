package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by user on 4/28/15.
 */
public class SocialEntity extends TinyUser implements Serializable {

    @SerializedName("isFollower")
    private Boolean isFollower = false;

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    @SerializedName("isFollowing")
    private Boolean isFollowing = false;

    @SerializedName("provinceID")
    private Integer provinceId;

    @SerializedName("cityID")
    private Integer cityId;

    @SerializedName("cuisineCount")
    private Integer recommendationGourmetCount;

    public Boolean isFollower() {
        return isFollower == null ? false : isFollower;
    }

    public Boolean isFollowing() {
        return isFollowing == null ? false : isFollowing;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public Integer getRecommendationGourmetCount() {
        return recommendationGourmetCount;
    }
}
