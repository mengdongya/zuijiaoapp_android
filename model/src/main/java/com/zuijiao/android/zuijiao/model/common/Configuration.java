package com.zuijiao.android.zuijiao.model.common;

/**
 * Created by Chen Hao on 5/18/15.
 */
public class Configuration {

    private Boolean notifyFollowed;
    private Boolean notifyLike;
    private Boolean notifyComment;

    public Configuration (Boolean notifyFollowed, Boolean notifyLike, Boolean notifyComment) {
        this.notifyComment = notifyComment;
        this.notifyFollowed = notifyFollowed;
        this.notifyLike = notifyLike;
    }

    public Boolean isNotifyFollowed () {
        return notifyFollowed;
    }

    public Boolean isNotifyLike () {
        return notifyLike;
    }

    public Boolean isNotifyComment () {
        return notifyComment;
    }

    @Override
    public String toString() {
        return String.format("follow: %s, like: %s, comment: %s", notifyFollowed, notifyLike, notifyComment);
    }
}
