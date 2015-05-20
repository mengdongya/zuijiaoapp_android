package com.zuijiao.android.zuijiao.network;

import com.google.gson.JsonElement;
import com.squareup.okhttp.Response;
import com.zuijiao.android.zuijiao.model.common.GourmetTags;
import com.zuijiao.android.zuijiao.model.common.Restaurants;
import com.zuijiao.android.zuijiao.model.common.TasteTags;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

//import retrofit.client.Response;

/**
 * Created by Chen Hao on 3/30/15.
 */
public interface IRouterCommon {
    final String RootURL = "/public";

    @GET(RootURL + "/taste_tags")
    void tasteTags(Callback<TasteTags> tasteTagsCallback);

    @GET(RootURL + "/cuisine_tags")
    void gourmetTags(Callback<GourmetTags> gourmetTagsCallback);

    @GET(RootURL + "/restaurant/search")
    void restaurantSearch(@Query("key") String key, @Query("count") Integer count, Callback<Restaurants> restaurantsCallback);

    @GET(RootURL + "languages")
    void languages(Callback<Restaurants> restaurantsCallback);

    @FormUrlEncoded
    @POST("/service/v1/captcha/code/create")
    void requestSecurityCode(@Field("mobile") String phoneNumber, Callback<Response> callback);

    @GET("/account/v1/ios/settings")
    void currentSetting(Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/account/v1/setting/update")
    void updateConfiguration(@Field("item") String option, @Field("value") Integer setTo, Callback<Response> callback);
}
