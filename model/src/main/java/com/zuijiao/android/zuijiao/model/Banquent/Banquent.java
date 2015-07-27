package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.functional.ImageUrlUtil;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Banquent implements Serializable {

    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String desc;
    @SerializedName("requirement")
    private String requirement;
    @SerializedName("characteristic")
    private String characteristic;
    @SerializedName("imageUrl")
    private String surfaceImageUrl;
    @SerializedName("status")
    private String status;

    @SerializedName("address")
    private String address;
    @SerializedName("time")
    private Date time;

    @SerializedName("endTime")
    private Date endTime;
    @SerializedName("price")
    private Float price;

    @SerializedName("dishes")
    private ArrayList<String> menu;
    @SerializedName("imageUrls")
    private ArrayList<String> imageUrls;

    @SerializedName("partySize")
    private BanquentCapacity banquentCapacity;
    @SerializedName("seller")
    private Master master;
    @SerializedName("attendees")
    private List<TinyUser> attendees;

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
        return ImageUrlUtil.imageUrl(surfaceImageUrl);
    }

    public String getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public Date getTime() {
        return time;
    }

    // public Integer getPrice() {
//        return price.intValue();
//    }

    public Float getPrice() {
        return price;
    }

    public List<String> getMenu() {
        return menu;
    }

    public ArrayList<String> getImageUrls() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            imageUrl = ImageUrlUtil.imageUrl(imageUrl);
            arrayList.add(imageUrl);
        }
        return arrayList;
    }

    public Date getEndTime() {
        return endTime;
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
