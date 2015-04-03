package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

/**
 * Created by Chen Hao on 3/16/15.
 */
public class OAuthModel {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("isNew")
    private Boolean isNew;
    @SerializedName("user")
    private TinyUser user;

    @Override
    public String toString() {
        return String.format("{ accessToken: %s, token_type: %s, isNew: %s, user: %s }"
                , accessToken
                , tokenType
                , isNew
                , user);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Boolean getIsNew() {
        return isNew == null ? false : isNew;
    }

    public Optional<TinyUser> getUser() {
        return Optional.ofNullable(user);
    }
}
