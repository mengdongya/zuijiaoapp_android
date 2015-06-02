package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Chen Hao on 3/18/15.
 * <p>
 * 喜欢某菜品的用户信息
 */
final public class WouldLikeToEatUser extends TinyUser implements Serializable {

    @SerializedName("createdAt")
    private Date date;
    @SerializedName("cityID")
    private Integer cityID;
    @SerializedName("provinceID")
    private Integer provinceID;
    @SerializedName("cuisineCount")
    private Integer recommendationCount;

    public Date getDate() {
        return date;
    }

    public Integer getCityID() {
        return cityID;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public Integer getRecommendationCount() {
        return recommendationCount;
    }
}
