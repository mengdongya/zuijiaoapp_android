package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.network.Router;

/**
 * Created by Chen Hao on 3/16/15.
 */
public class TinyUser {

    @SerializedName("nickname")
    private String nickName = "test";
    @SerializedName("imageUrl")
    private String avatarURL = "testurl";
    @SerializedName("ID")
    private Integer identifier = 1;

    public String getNickName() {
        return nickName;
    }

    public Optional<String> getAvatarURL() {
        if (avatarURL != null) {
            if (avatarURL.length() > 0 && avatarURL.startsWith("http://"))
                return Optional.ofNullable(avatarURL);
            else {
                return Optional.of(Router.PicBaseUrl + avatarURL);
            }
        }
        return Optional.empty();
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
