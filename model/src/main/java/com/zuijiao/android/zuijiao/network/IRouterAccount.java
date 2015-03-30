package com.zuijiao.android.zuijiao.network;

import com.squareup.okhttp.Response;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Chen Hao on 3/23/15.
 */
public interface IRouterAccount {

    @FormUrlEncoded
    @POST("/account/v1/profiles/update")
    void update(@Field("gender") String gender
            , @Field("provinceID") Integer provinceId
            , @Field("cityId") Integer cityId
            , @Field("story") String story
            , @Field("year") Integer year
            , @Field("month") Integer month
            , @Field("day") Integer day
            , @Field("tasteTags") String tags
            , Callback<Response> callback
    );

}
