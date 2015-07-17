package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

/**
 * Created by user on 6/19/15.
 */
public class Master extends TinyUser {
    @SerializedName("userID")
    private Integer userId ;
    @SerializedName("commentCount")
    private Integer commentCount ;
    @SerializedName("score")
    private Float score ;

    public Integer getCommentCount() {
        return commentCount;
    }

    public Float getScore() {
        return score;
    }

    public int getUserId(){
        return userId ;
    }

}
