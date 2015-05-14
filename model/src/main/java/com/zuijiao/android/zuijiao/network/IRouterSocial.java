package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.zuijiao.model.user.SocialEntities;

import javax.swing.plaf.RootPaneUI;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by user on 4/28/15.
 */
public interface IRouterSocial {
    final String RootURL = "/friendships/v1";

//    @GET(RootURL + "/followers")
//    void getFollowers(@Query("maxID") Integer thresholdID, @Query("count") Integer count, Callback<SocialEntities> callback);
//
//    @GET(RootURL + "/followings")
//    void getFollowings(@Query("maxID") Integer thresholdID, @Query("count") Integer count, Callback<SocialEntities> callback);

    @GET(RootURL + "/user/{userId}/followings")
    void getFollowingsOfUserId(@Path("userId") Integer userId
            , @Query("maxID") Integer thresholdID
            , @Query("count") Integer count
            , Callback<SocialEntities> callback);

    @GET(RootURL + "/user/{userId}/followers")
    void getFollowersOfUserId(@Path("userId") Integer userId
            , @Query("maxID") Integer thresholdID
            , @Query("count") Integer count
            , Callback<SocialEntities> callback);

    @FormUrlEncoded
    @POST(RootURL + "/following/destroy")
    void unFollow(@Field("ID") Integer userId, Callback<Response> callback);

    @FormUrlEncoded
    @POST(RootURL + "/following/create")
    void follow(@Field("ID") Integer userId, Callback<Response> callback);


}
