package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Banquent {

    @SerializedName("ID") private Integer identifier;
    @SerializedName("title") private String title;
    @SerializedName("description") private String desc;
    @SerializedName("requirement") private String requirement;
    @SerializedName("characteristic") private String characteristic;
    @SerializedName("imageUrl") private String surfaceImageUrl;
    @SerializedName("status") private BanquentStatus status;

    @SerializedName("address") private String address;
    @SerializedName("time") private Date time;
    @SerializedName("price") private Float price;

    @SerializedName("dishes") private List<String> menu;
    @SerializedName("imageUrls") private List<String>  imageUrls;

    @SerializedName("partySize") private BanquentCapacity banquentCapacity;
    @SerializedName("seller") private Master master;
    @SerializedName("attendees") private List<TinyUser> attendees;

    public Integer getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getRequirement() {
        return requirement;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public String getSurfaceImageUrl() {
        return surfaceImageUrl;
    }

    public BanquentStatus getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public Date getTime() {
        return time;
    }

    public Integer getPrice() {
        return price.intValue();
    }

    public List<String> getMenu() {
        return menu;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public BanquentCapacity getBanquentCapacity() {
        return banquentCapacity;
    }

    public Master getMaster() {
        return master;
    }

    public List<TinyUser> getAttendees() {
        return attendees;
    }
}
