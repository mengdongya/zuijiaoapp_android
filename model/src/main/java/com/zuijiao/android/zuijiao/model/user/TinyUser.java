package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;

/**
 * Created by Chen Hao on 3/16/15.
 */
public class TinyUser {

    @SerializedName("nickname")
    private String nickName;
    @SerializedName("imageUrl")
    private String avatarURL;



    @SerializedName("ID")
    private Integer identifier;

    public String getNickName() {
        return nickName;
    }

    public Optional<String> getAvatarURL() {
        return Optional.ofNullable(avatarURL);
    }

    public Integer getIdentifier() {
        return identifier;
    }

    //20150401 qbxia add begin
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
    //20150401 qbxia add end
}
