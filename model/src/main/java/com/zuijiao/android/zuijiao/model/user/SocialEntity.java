package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 4/28/15.
 */
public class SocialEntity extends TinyUser {

    @SerializedName("isFollower")
    private Boolean isFollower;

    @SerializedName("isFollowing")
    private Boolean isFollowing;

    @SerializedName("provinceID")
    private Integer provinceId;

    @SerializedName("cityID")
    private Integer cityId;

    @SerializedName("cuisineCount")
    private Integer recommendationGourmetCount;

    public Boolean isFollower() {
        return isFollower;
    }

    public Boolean isFollowing() {
        return isFollowing;
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
