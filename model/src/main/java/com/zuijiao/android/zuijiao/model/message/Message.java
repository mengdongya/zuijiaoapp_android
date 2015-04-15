package com.zuijiao.android.zuijiao.model.message;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.util.Date;

/**
 * Created by user on 4/3/15.
 */
public class Message {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("type")
    private Integer type;
    @SerializedName("text")
    private String description;
    @SerializedName("isRead")
    private Boolean isRead;
    @SerializedName("createdAt")
    private Date createTime;
    @SerializedName("cuisine")
    private Gourmet gourmet;
    @SerializedName("fromUser")
    private TinyUser fromUser;

    public Integer getIdentifier() {
        return identifier;
    }

    public Type getType() {
        return Type.fromIndex(type);
    }

    public String getDescription() {
        return description;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Optional<Gourmet> getGourmet() {
        return Optional.ofNullable(gourmet);
    }

    public TinyUser getFromUser() {
        return fromUser;
    }

    public enum Type {
        Empty, Follower, Favorite, Comment;

        public Integer index() {
            switch (this) {
                case Follower:
                    return 1;
                case Favorite:
                    return 2;
                case Comment:
                    return 3;
                default:
                    return 0;
            }
        }

        public static Type fromIndex(Integer index) {
            switch (index) {
                case 1:
                    return Follower;
                case 2:
                    return Favorite;
                case 3:
                    return Comment;
                default:
                    return Empty;
            }
        }
    }
}
