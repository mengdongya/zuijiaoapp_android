package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.ImageUrlUtil;

import java.io.Serializable;

/**
 * Created by Chen Hao on 3/16/15.
 */
public class TinyUser implements Serializable {

    @SerializedName("nickname")
    private String nickName = "";
    @SerializedName("imageUrl")
    private String avatarURL = null;
    @SerializedName("ID")
    private Integer identifier = -1;

    public String getNickName() {
        return nickName;
    }

    public Optional<String> getAvatarURLSmall() {
//        if (avatarURL != null) {
//            if (avatarURL.length() > 0 && avatarURL.startsWith("http"))
//                return Optional.ofNullable(avatarURL);
//            else {
//                return Optional.of(Router.PicBaseUrl + avatarURL);
//            }
//        }
//        return Optional.empty();
        String avatarUrlTest = ImageUrlUtil.imageUrl(avatarURL);
        if (avatarUrlTest != null && !avatarUrlTest.startsWith("http://pic.zuijiao"))
            avatarUrlTest = avatarUrlTest + "_avatar";
////        avatarURL += "_avatar" ;
//        System.out.println("avatar = " + avatarUrlTest );
//        Optional optional = Optional.ofNullable(ImageUrlUtil.imageUrl(avatarUrlTest));
//        System.out.println("optional = "+optional.get());
        return Optional.ofNullable(avatarUrlTest);
    }

    public Optional<String> getAvatarUrl() {
        return Optional.ofNullable(ImageUrlUtil.imageUrl(avatarURL));
    }

    public Integer getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return String.format("Nickname: %s\tAvatarUrl: %s\tID: %S", nickName, avatarURL, identifier);
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
