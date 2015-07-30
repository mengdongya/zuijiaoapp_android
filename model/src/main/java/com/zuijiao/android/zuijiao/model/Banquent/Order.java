package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.functional.ImageUrlUtil;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 6/18/15.
 */
public class Order implements Serializable {

    @SerializedName("ID")
    private Integer identifier;
    //    @SerializedName("eventID")
//    private Integer banquentIdentifier;
    @SerializedName("tradeNo")
    private String serialNumber;
    //    @SerializedName("title")
//    private String title;
    @SerializedName("note")
    private String remark;
    @SerializedName("mobile")
    private String phoneNumber;
    //    @SerializedName("imageUrl")
//    private String imageUrl;
    @SerializedName("createdAt")
    private Date createTime;
    //    @SerializedName("address")
//    private String address;
    @SerializedName("price")
    private Float price;
    @SerializedName("status")
    private String status;
    //    @SerializedName("time")
//    private Date holdTime;
    @SerializedName("isCommented")
    private Boolean isCommented;
    @SerializedName("quantity")
    private Integer quantity;
    @SerializedName("realPrice")
    private Float realPrice;
    @SerializedName("totalPrice")
    private Float totalPrice;
    @SerializedName("deadline")
    private Date deadline;
    @SerializedName("event")
    private Event event;
    @SerializedName("seller")
    private TinyUser seller;

    public Integer getQuantity() {
        return quantity;
    }

    public Float getRealPrice() {
        return realPrice;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public Date getDeadline() {
        return deadline;
    }

    public Boolean getIsCommented() {
        return isCommented;
    }

    public Integer getIdentifier() {
        return identifier;
    }

//    public Integer getBanquentIdentifier() {
//        return banquentIdentifier;
//    }

    public String getSerialNumber() {
        return serialNumber;
    }

//    public String getTitle() {
//        return title;
//    }

    public String getRemark() {
        return remark;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

//    public String getImageUrl() {
//        return ImageUrlUtil.imageUrl(imageUrl);
////        return imageUrl;
//    }

//    public Date getHoldTime() {
//        return holdTime;
//    }

    public Date getCreateTime() {
        return createTime;
    }

//    public String getAddress() {
//        return address;
//    }

    public Float getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return OrderStatus.fromString(status);
    }

    public Event getEvent() {
        return event;
    }

    public TinyUser getSeller() {
        return seller;
    }
}
