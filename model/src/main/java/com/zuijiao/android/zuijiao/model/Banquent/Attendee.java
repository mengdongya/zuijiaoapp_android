package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.ImageUrlUtil;
import com.zuijiao.android.zuijiao.model.user.Profile;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 6/19/15.
 */
public class Attendee implements Serializable{

    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("imageUrl")
    private String avatarURL;
    @SerializedName("attendCount")
    private Integer attendCount ;//add
    @SerializedName("event")
    private Banquent lastAttendEvent ;//add
    @SerializedName("profile")
    private Profile profile;
    @SerializedName("host")
    private Seller sellerInfo;

    public Integer getIdentifier() {
        return identifier;
    }

    public String getNickname() {
        return nickname;
    }

    public Optional<String> getAvatarURLSmall() {
        String avatarUrlTest = ImageUrlUtil.imageUrl(avatarURL);
        if (avatarUrlTest != null && !avatarUrlTest.startsWith("http://pic.zuijiao"))
            avatarUrlTest = avatarUrlTest + "_avatar";
        return Optional.ofNullable(avatarUrlTest);
//        return Optional.ofNullable(ImageUrlUtil.imageUrl(avatarURL + "_avatar"));

    }

    public Optional<String> getAvatarURL() {
        return Optional.ofNullable(ImageUrlUtil.imageUrl(avatarURL));
    }

    public Profile getProfile() {
        return profile;
    }

    public Integer getAttendCount() {
        return attendCount;
    }

    public Banquent getLastAttendEvent() {
        return lastAttendEvent;
    }

    public Seller getSellerInfo() {
        return sellerInfo;
    }
    //    public ArrayList<String> getImageUrls() {
//        if (organizerInfo != null)
//            ?
//            return organizerInfo.imageUrls;
//        return null;
//
//    }
}
