package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Chen Hao on 3/13/15.
 */
public class User {
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

    class Profile {
        @SerializedName("gender")
        private String gender;
        @SerializedName("birthday")
        private String birthday;
        @SerializedName("provinceID")
        private Integer provinceId;
        @SerializedName("cityID")
        private Integer cityId;
        @SerializedName("story")
        private String story;
        @SerializedName("tasteTags")
        private List<String> tasteTags;

    }

    class Food {
        @SerializedName("collectionCount")
        private Integer collectionCount;
        @SerializedName("cuisineCount")
        private Integer recommendationCount;
    }

    class FriendShip {
        @SerializedName("followerCount")
        private Integer followerCount;
        @SerializedName("followingCount")
        private Integer followingCount;
        @SerializedName("isFollowing")
        private Boolean isFollowing;
        @SerializedName("isFollower")
        private Boolean isFollower;

    }

    public Integer getIdentifier() {
        return identifier;
    }

    public Optional<String> getNickname() {
        return Optional.ofNullable(nickname);
    }

    public Optional<String> getAvatarURL() {
        return Optional.ofNullable(avatarURL);
    }


    public String getGender() {
        return profile.gender == null ? "keepSecret" : profile.gender;
    }

    public Optional<Date> getBirthday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        try {
            birthday = sdf.parse(profile.birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(birthday);
    }

    public Integer getProvinceId() {
        return profile.provinceId;
    }

    public Integer getCityId() {
        return profile.cityId;
    }

    public Optional<String> getStory() {
        return Optional.ofNullable(profile.story);
    }

    public Optional<List<String>> getTasteTags() {
        return Optional.ofNullable(profile.tasteTags);
    }

    public Integer getCollectionCount() {
        return food.collectionCount;
    }

    public Integer getRecommendationCount() {
        return food.recommendationCount;
    }

    public Integer getFollowerCount() {
        return friendShip.followerCount;
    }

    public Integer getFollowingCount() {
        return friendShip.followingCount;
    }

    public Boolean getIsFollowing() {
        return friendShip.isFollowing == null ? false : friendShip.isFollowing;
    }

    public Boolean getIsFollower() {
        return friendShip.isFollower == null ? false : friendShip.isFollower;
    }

    // update methods

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public void setGender(String gender) {
        if ("male".equals(gender) || "female".equals(gender)) {
            this.profile.gender = gender;
        } else {
            this.profile.gender = "keepSecret";
        }
    }

    public void setBirthday(Date birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.profile.birthday = sdf.format(birthday);
    }

    public void setProvinceAndCity(Integer provinceId, Integer cityId) {
        this.profile.provinceId = provinceId;
        this.profile.cityId = cityId;
    }

    public void setStory(String story) {
        this.profile.story = story;
    }

    public void setTasteTags(List<String> tasteTags) {
        this.profile.tasteTags = tasteTags;
    }

}










