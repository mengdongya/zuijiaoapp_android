package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.zuijiao.model.common.Restaurants;
import com.zuijiao.android.zuijiao.model.common.TasteTags;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Chen Hao on 3/30/15.
 */
public interface IRouterCommon {
    final String RootURL = "/oauth/v1";

    @GET(RootURL + "/taste_tags")
    void tasteTags(Callback<TasteTags> tasteTagsCallback);

    @GET(RootURL + "/cuisine_tags")
    void gourmetTags(Callback<List<String>> gourmetTagsCallback);

    @GET(RootURL + "/restaurant/search")
    void restaurantSearch(@Query("key") String key, @Query("count") Integer count, Callback<Restaurants> restaurantsCallback);

}
