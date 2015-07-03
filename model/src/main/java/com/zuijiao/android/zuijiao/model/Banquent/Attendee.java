package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.ImageUrlUtil;
import com.zuijiao.android.zuijiao.model.user.Profile;

import java.util.ArrayList;

/**
 * Created by user on 6/19/15.
 */
public class Attendee {

    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("imageUrl")
    private String avatarURL; //: ?

    @SerializedName("profile")
    private Profile profile;
    @SerializedName("host")
    private OrganizerInfo organizerInfo;

    class OrganizerInfo {
        @SerializedName("skill")
        private String qualification;
        @SerializedName("culinary")
        private String cookingSkill;
        @SerializedName("imageUrls")
        private ArrayList<String> imageUrls;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public String getNickname() {
        return nickname;
    }

    public Optional<String> getAvatarURL() {
//        return Optional.ofNullable(avatarURL);
        return Optional.ofNullable(ImageUrlUtil.imageUrl(avatarURL));
    }

    public Profile getProfile() {
        return profile;
    }

    public String getQualification() {
        if (organizerInfo != null)
            return organizerInfo.qualification;
        return null;
    }

    public String getCookingSkill() {
        if (organizerInfo != null)
            return organizerInfo.cookingSkill;
        return null;
    }

    //    public ArrayList<String> getImageUrls() {
//        if (organizerInfo != null)
//            ?
//            return organizerInfo.imageUrls;
//        return null;
//
//    }
    public ArrayList<String> getImageUrls() {
        if (organizerInfo == null || organizerInfo.imageUrls == null) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (String imageUrl : organizerInfo.imageUrls) {
            imageUrl = ImageUrlUtil.imageUrl(imageUrl);
            arrayList.add(imageUrl);
        }
        return arrayList;
    }
}
