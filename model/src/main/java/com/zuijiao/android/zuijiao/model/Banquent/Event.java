package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaqibo on 2015/7/16.
 */
public class Event {
    @SerializedName("ID")
    private Integer identify ;
    @SerializedName("title")
    private String title ;
    @SerializedName("imageUrl")
    private String imageUrl ;
    @SerializedName("time")
    private String date ;
    @SerializedName("price")
    private Float price ;
    @SerializedName("soldQuantity")
    private Integer soldCount ;
}
