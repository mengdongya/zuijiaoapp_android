package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chen Hao on 3/23/15.
 */
public class Comments {
    @SerializedName("items")
    private List<Comment> commentList;
    @SerializedName("itemTotalCount")
    private Integer count;

    public List<Comment> getCommentList() {
        return commentList;
    }

    public Integer count() {
        return count;
    }
}
