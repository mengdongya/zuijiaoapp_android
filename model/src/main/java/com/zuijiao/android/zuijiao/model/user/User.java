package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.ImageUrlUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chen Hao on 3/13/15.
 */
public class User implements Serializable, Cloneable {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("imageUrl")
    private String avatarURL;
    @SerializedName("profile")
    private Profile profile;
    @SerializedName("foods")
    private Food food;


    @SerializedName("friendships")
    private FriendShip friendShip;


    // Optional only avialabel for self
    @SerializedName("contracts")
    private ContactInfo contactInfo;
    @SerializedName("feast")
    private PrivateCuisineInfo privateCuisineInfo;

    public Integer getIdentifier() {
        return identifier;
    }

    public Optional<String> getNickname() {
        return Optional.ofNullable(nickname);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Optional<String> getAvatarURL() {
//        return Optional.ofNullable(avatarURL);
//        if (avatarURL != null) {
//            if (avatarURL.length() > 0 && avatarURL.startsWith("http"))
//                return Optional.ofNullable(avatarURL);
//            else {
//                return Optional.of(Router.PicBaseUrl + avatarURL);
//            }
//        }

        return Optional.ofNullable(ImageUrlUtil.imageUrl(avatarURL));
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    /**
     * @deprecated Using getProfile().getGender()
     */
    @Deprecated
    public String getGender() {
        return profile.getGender();
    }

    @Deprecated
    public void setGender(String gender) {
        profile.setGender(gender);
    }

    @Deprecated
    public Optional<Date> getBirthday() {
        return profile.getBirthday();
    }

    @Deprecated
    public void setBirthday(Date birthday) {
        profile.setBirthday(birthday);
    }

    @Deprecated
    public Integer getProvinceId() {
        return profile.getProvinceId();
    }

    @Deprecated
    public Integer getCityId() {
        return profile.getCityId();
    }

    @Deprecated
    public Optional<String> getStory() {
        return profile.getStory();
    }

    @Deprecated
    public void setStory(String story) {
        profile.setStory(story);
    }

    @Deprecated
    public Optional<ArrayList<String>> getTasteTags() {
        return profile.getTasteTags();
    }

    @Deprecated
    public void setTasteTags(ArrayList<String> tasteTags) {
        profile.setTasteTags(tasteTags);
    }

    @Deprecated
    public Integer getCollectionCount() {
        return food.getCollectionCount();
    }

    @Deprecated
    public Integer getRecommendationCount() {
        return food.getRecommendationCount();
    }

    @Deprecated
    public Integer getFollowerCount() {
        return friendShip.getFollowerCount();
    }

    @Deprecated
    public Integer getFollowingCount() {
        return friendShip.getFollowingCount();
    }

    @Deprecated
    public Boolean getIsFollowing() {
        return friendShip.isFollowing();
    }

    // update methods

    @Deprecated
    public Boolean getIsFollower() {
        return friendShip.isFollower();
    }

    public Profile getProfile() {
        return profile;
    }

    public Food getFood() {
        return food;
    }

    public FriendShip getFriendShip() {
        return friendShip;
    }

    public Optional<ContactInfo> getContactInfo() {
        return Optional.ofNullable(contactInfo);
    }

    public Optional<PrivateCuisineInfo> getPrivateCuisineInfo() {
        return Optional.ofNullable(privateCuisineInfo);
    }

    @Deprecated
    public void setProvinceAndCity(Integer provinceId, Integer cityId) {
        profile.setProvinceAndCity(provinceId, cityId);
    }

    public void setFriendShip(FriendShip friendShip) {
        this.friendShip = friendShip;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public User clone() {
        try {
            User user = null;
            user = (User) super.clone();
            if (this.profile != null) {
                user.profile = (Profile) profile.clone();
            }
            if (this.food != null) {
                user.food = (Food) food.clone();
            }
            if (this.friendShip != null) {
                user.friendShip = (FriendShip) friendShip.clone();
            }
            if (this.contactInfo != null) {
                user.contactInfo = (ContactInfo) contactInfo.clone();
            }
            if (this.privateCuisineInfo != null) {
                user.privateCuisineInfo = (PrivateCuisineInfo) privateCuisineInfo.clone();
            }
            return user;
        } catch (Exception e) {
            return this;
        }
    }

}










