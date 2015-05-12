package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 美食
 * <p>
 * Created by Chen Hao on 3/18/15.
 */
public class Gourmet implements Serializable, Cloneable {

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
        List<String> result = new ArrayList<>();
        for (String url : imageURLs) {
            if (url.length() > 0 && url.startsWith("http://"))
                result.add(url);
            else
                result.add(Router.PicBaseUrl + url);
        }
        return result;
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
        return wasMarked == null ? false : wasMarked;
    }

    public Date getDate() {
        return date;
    }

    public Boolean getIsPrivate() {
        return isPrivate == null ? false : isPrivate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    //20150401 qbxia add begin
    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageURLs(List<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser(TinyUser user) {
        this.user = user;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setWasMarked(Boolean wasMarked) {
        this.wasMarked = wasMarked;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    //20150401 qbxia add end
}
