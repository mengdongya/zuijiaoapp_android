package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.util.Date;
import java.util.List;

/**
 * 美食
 * <p>
 * Created by Chen Hao on 3/18/15.
 */
public class Gourmet {

    @SerializedName("title")
    private String name;
    @SerializedName("imageUrls")
    private List<String> imageURLs;
    @SerializedName("tags")
    private List<String> tags;
    @SerializedName("description")
    private String description;
    @SerializedName("user")
    private TinyUser user;
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("address")
    private String address;
    @SerializedName("price")
    private String price;
    @SerializedName("isCollected")
    private Boolean wasMarked;
    @SerializedName("createdAt")
    private Date date;
    @SerializedName("isFeast")
    private Boolean isPrivate;

    public String getName() {
        return name;
    }

    public List<String> getImageURLs() {
        return imageURLs;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public TinyUser getUser() {
        return user;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public Boolean getWasMarked() {
        return wasMarked;
    }

    public Date getDate() {
        return date;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

}
