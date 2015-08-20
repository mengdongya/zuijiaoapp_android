package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 6/19/15.
 */
public class Banquents implements Serializable{
    @SerializedName("banner")
    private Banner banner;
    @SerializedName("items")
    private ArrayList<Banquent> banquentList;
    @SerializedName("itemCount")
    private Integer itemCount ;
    @SerializedName("totalCount")
    private Integer totalCount ;
    @SerializedName("previousCursor")
    private Integer previousCursor ;
    @SerializedName("nextCursor")
    private Integer nextCursor ;

    public ArrayList<Banner> getBanners() {
        //test
//        int randomCount = new Random().nextInt(5) ;
//        banners = new ArrayList<>() ;
//        for(int i= 0 ; i <= randomCount ; i++ ){
//            banners.add(new Banquents.Banner("www.baidu.com", "http://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=JELLY_BEAN&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3480012295,1633097787&os=1223343871,129863328&pn=2&rn=1&di=55491551940&ln=1288&fr=&fmq=1439980024859_R&ic=undefined&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&gsm=0&objurl=http%3A%2F%2Farticles.csdn.net%2Fuploads%2Fallimg%2F120306%2F118_120306100059_1_lit.jpg"));
//        }
        return banners;
    }

    //    private Integer lastIdentifer ;
    @SerializedName("banners")
    private ArrayList<Banner> banners ;
    public String getBannerLinkUrl() {
        return banner.linkUrl;
    }

    public String getBannerImageUrl() {
        return banner.imageUrl;
    }

    public ArrayList<Banquent> getBanquentList() {
        return banquentList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public Integer getPreviousCursor() {
        return previousCursor;
    }

    public Integer getNextCursor() {
        return nextCursor;
    }

    public class Banner implements  Serializable{
        @SerializedName("url")
        private String linkUrl;
        @SerializedName("imageUrl")
        private String imageUrl;

        public String getLinkUrl() {
            return linkUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Banner(String linkUrl, String imageUrl) {
            this.linkUrl = linkUrl;
            this.imageUrl = imageUrl;
        }
    }
}
