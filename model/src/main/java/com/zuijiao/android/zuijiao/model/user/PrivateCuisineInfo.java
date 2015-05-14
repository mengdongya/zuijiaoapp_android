package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 4/28/15.
 */
public class PrivateCuisineInfo implements Cloneable {
    @SerializedName("attendCount")
    private Integer attendCount;
    @SerializedName("hostCount")
    private Integer hostCount;

    public Integer getAttendCount() {
        return attendCount;
    }

    public Integer getHostCount() {
        return hostCount;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
