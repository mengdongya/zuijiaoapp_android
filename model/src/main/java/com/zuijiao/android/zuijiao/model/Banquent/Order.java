package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 6/18/15.
 */
public class Order implements Serializable {

    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("eventID")
    private Integer banquentIdentifier;
    @SerializedName("tradeNo")
    private String serialNumber;
    @SerializedName("title")
    private String title;
    @SerializedName("note")
    private String remark;
    @SerializedName("mobile")
    private String phoneNumber;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("createdAt")
    private Date createTime;
    @SerializedName("address")
    private String address;
    @SerializedName("price")
    private Float price;
    @SerializedName("status")
    private String status;

    public Integer getIdentifier() {
        return identifier;
    }

    public Integer getBanquentIdentifier() {
        return banquentIdentifier;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getRemark() {
        return remark;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getAddress() {
        return address;
    }

    public Float getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return OrderStatus.fromString(status);
    }
}
