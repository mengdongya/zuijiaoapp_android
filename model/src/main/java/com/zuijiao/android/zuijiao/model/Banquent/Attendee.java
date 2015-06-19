package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.Profile;

import java.util.List;

/**
 * Created by user on 6/19/15.
 */
public class Attendee {

    @SerializedName("ID") private Integer identifier;
    @SerializedName("nickname") private String nickname;
    @SerializedName("imageUrl") private String avatarURL; //: ?

    @SerializedName("profile") private Profile profile;
    @SerializedName("host") private OrganizerInfo organizerInfo;

    class OrganizerInfo {
        @SerializedName("skill") private String  qualification;
        @SerializedName("culinary") private String cookingSkill;
        @SerializedName("imageUrls") private List<String> imageUrls;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public String getNickname() {
        return nickname;
    }

    public Optional<String> getAvatarURL() {
        return Optional.ofNullable(avatarURL);
    }

    public Profile getProfile() {
        return profile;
    }

    public String getQualification() {
        return organizerInfo.qualification;
    }

    public String getCookingSkill() {
        return organizerInfo.cookingSkill;
    }

    public List<String> getImageUrls() {
        return organizerInfo.imageUrls;
    }
}
