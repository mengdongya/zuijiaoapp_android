package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yitianhao on 2015/7/13.
 */
public class Review implements Serializable {
    @SerializedName("ID")
    private Integer identifier;
    @SerializedName("content")
    private String content;
    @SerializedName("score")
    private int score;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("event")
    private CommentEvent event;
    @SerializedName("user")
    private TinyUser reviewer;

    public Integer getIdentifier() {
        return identifier;
    }

    public String getContent() {
        return content;
    }

    public int getScore() {
        return score;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public CommentEvent getEvent() {
        return event;
    }

    public TinyUser getReviewer() {
        return reviewer;
    }
}
