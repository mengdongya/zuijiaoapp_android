package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 6/19/15.
 */
public class BanquentCapacity {

    @SerializedName("minValue") private Integer min;
    @SerializedName("maxValue") private Integer max;
    @SerializedName("attendeeCount") private Integer count;

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getCount() {
        return count;
    }
}
