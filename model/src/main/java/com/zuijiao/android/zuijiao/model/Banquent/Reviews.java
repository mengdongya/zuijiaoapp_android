package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yitianhao on 2015/7/13.
 */
public class Reviews implements Serializable {
    @SerializedName("itemTotalCount")
    private Integer totalCount;

    public Integer getTotalCount() {
        return totalCount;
    }

    @SerializedName("items")
    private List<Review> reviewList;

    public List<Review> getReviewList() {
        return reviewList;
    }
}
