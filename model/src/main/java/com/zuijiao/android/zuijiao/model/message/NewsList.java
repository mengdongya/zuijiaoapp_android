package com.zuijiao.android.zuijiao.model.message;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 4/3/15.
 */
public class NewsList {
    @SerializedName("items")
    private List<News> news;

    public List<News> getNews() {
        return news;
    }
}
