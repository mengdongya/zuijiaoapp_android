package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chen Hao on 4/28/15.
 */
public class FriendShip implements Cloneable {
    @SerializedName("followerCount")
    private Integer followerCount;
    @SerializedName("followingCount")
    private Integer followingCount;
    @SerializedName("isFollowing")
    private Boolean isFollowing;
    @SerializedName("isFollower")
    private Boolean isFollower;

    public Integer getFollowerCount() {
        return followerCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public Boolean isFollowing() {
        return isFollowing == null ? false : isFollowing;
    }

    public Boolean isFollower() {
        return isFollower == null ? false : isFollower;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
