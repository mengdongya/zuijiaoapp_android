package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.util.Date;

/**
 * Created by Chen Hao on 3/23/15.
 */
public class Comment {

    @SerializedName("user")
    private TinyUser user;
    @SerializedName("createdAt")
    private Date postDate;

    public Comment(TinyUser user, Date postDate, String detail, TinyUser replyTo, Integer identifier) {
        this.user = user;
        this.postDate = postDate;
        this.detail = detail;
        this.replyTo = replyTo;
        this.identifier = identifier;
    }

    @SerializedName("text")
    private String detail;
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("replyUser")
    private TinyUser replyTo;

    public TinyUser getUser() {
        return user;
    }

    public Date getPostDate() {
        return postDate;
    }

    public String getDetail() {
        return detail;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public Optional<TinyUser> getReplyTo() {
        return Optional.ofNullable(replyTo);
    }
}
