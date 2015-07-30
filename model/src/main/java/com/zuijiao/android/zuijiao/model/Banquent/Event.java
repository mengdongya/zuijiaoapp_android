package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.functional.ImageUrlUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yitianhao on 2015/7/13.
 */
public class Event implements Serializable {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("title")
    private String title;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("time")
    private Date time;
    @SerializedName("price")
    private Float price;
    @SerializedName("realPrice")
    private Float realPrice;
    @SerializedName("endTime")
    private Date endTime;
    @SerializedName("deadline")
    private Date deadline;
    @SerializedName("address")
    private String address;

    public Integer getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return ImageUrlUtil.imageUrl(imageUrl);
    }

    public Date getTime() {
        return time;
    }

    public Float getPrice() {
        return price;
    }

    public Float getRealPrice() {
        return realPrice;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getAddress() {
        return address;
    }
}
