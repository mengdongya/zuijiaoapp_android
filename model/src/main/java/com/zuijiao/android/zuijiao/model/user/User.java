package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Chen Hao on 3/13/15.
 */
public class User implements Serializable {
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

    public Optional<String> getAvatarURL() {
        return Optional.ofNullable(avatarURL);
    }

    /**
     * @deprecated Using getProfile().getGender()
     */
    @Deprecated
    public String getGender() {
        return profile.getGender();
    }

    @Deprecated
    public Optional<Date> getBirthday() {
        return profile.getBirthday();
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
    public Optional<List<String>> getTasteTags() {
        return profile.getTasteTags();
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

    // update methods

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Deprecated
    public void setGender(String gender) {
        profile.setGender(gender);
    }

    @Deprecated
    public void setBirthday(Date birthday) {
        profile.setBirthday(birthday);
    }

    @Deprecated
    public void setProvinceAndCity(Integer provinceId, Integer cityId) {
        profile.setProvinceAndCity(provinceId, cityId);
    }

    @Deprecated
    public void setStory(String story) {
        profile.setStory(story);
    }

    @Deprecated
    public void setTasteTags(List<String> tasteTags) {
        profile.setTasteTags(tasteTags);
    }

}










