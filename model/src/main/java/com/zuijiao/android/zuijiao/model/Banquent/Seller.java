package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.functional.ImageUrlUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xiaqibo on 2015/7/16.
 */
public class Seller implements Serializable{
    @SerializedName("ID")
    private Integer identifier ;
    @SerializedName("skill")
    private String skill;
    @SerializedName("culinary")
    private String culinary;
    @SerializedName("score")
    private Float score ;
    @SerializedName("commentCount")
    private Integer commentCount ;
    @SerializedName("eventCount")
    private Integer eventCount ;
    @SerializedName("soldCount")
    private Integer soldCount ;
    @SerializedName("event")
    private Banquent lastHoldEvent ;
    //        private Event lastHostEvent ;
    @SerializedName("place")
    private SellerPlace place ;
    @SerializedName("comment")
    private Review lastReview ;
    public Integer getIdentifier() {
        return identifier;
    }

    public String getCulinary() {
        return culinary;
    }

    public String getSkill() {
        return skill;
    }

    public Float getScore() {
        return score;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public Banquent getLastHoldEvent() {
        return lastHoldEvent;
    }

    public SellerPlace getPlace() {
        return place;
    }

    public Review getLastReview() {
        return lastReview;
    }


    public class SellerPlace implements Serializable{
        @SerializedName("type")
        private String placeType ;
        @SerializedName("provinceID")
        private Integer provinceId ;
        @SerializedName("cityID")
        private Integer cityID ;
        @SerializedName("address")
        private String address ;

        @SerializedName("imageUrls")
        private ArrayList<String> placeImages;

        public String getAddress() {
            return address;
        }

        public Integer getCityID() {
            return cityID;
        }

        public Integer getProvinceId() {
            return provinceId;
        }

        public String getPlaceType() {
            return placeType;
        }


        public ArrayList<String> getPlaceImages() {
            if (placeImages == null) {
                return null;
            }
            ArrayList<String> arrayList = new ArrayList<>();
            for (String imageUrl : placeImages) {
                imageUrl = ImageUrlUtil.imageUrl(imageUrl);
                arrayList.add(imageUrl);
            }
            return arrayList;
        }

    }
}
