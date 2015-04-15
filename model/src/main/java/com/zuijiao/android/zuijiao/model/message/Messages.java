package com.zuijiao.android.zuijiao.model.message;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 4/3/15.
 */
public class Messages {
    @SerializedName("items")
    private List<Message> allMessage;

    public List<Message> getAllMessage() {
        return allMessage;
    }
}
