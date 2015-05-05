package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 4/28/15.
 */
public class Food {
    @SerializedName("collectionCount")
    private Integer collectionCount;
    @SerializedName("cuisineCount")
    private Integer recommendationCount;

    public Integer getCollectionCount() {
        return collectionCount;
    }

    public Integer getRecommendationCount() {
        return recommendationCount;
    }
}
