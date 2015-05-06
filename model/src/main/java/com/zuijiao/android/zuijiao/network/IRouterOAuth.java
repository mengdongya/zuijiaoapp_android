package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.zuijiao.model.common.OAuthModel;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Chen Hao on 3/16/15.
 * <p>
 */
public abstract interface IRouterOAuth {
    final String RootURL = "/oauth/v1";

    @FormUrlEncoded
    @POST(RootURL + "/login")
    void login(@Field("openId") String openID
            , @Field("platform") String platform
            , @Field("deviceToken") String deviceToken
            , @Field("openToken") String openToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/login")
    OAuthModel login(@Field("openId") String openID
            , @Field("platform") String platform
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
    );

    @FormUrlEncoded
    @POST(RootURL + "/ios/register")
    void register(@Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("openId") String openID
            , @Field("platform") String platform
            , @Field("deviceToken") String deviceToken
            , @Field("openToken") String openToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/ios/register")
    OAuthModel register(@Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("openId") String openID
            , @Field("platform") String platform
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
    );

    @FormUrlEncoded
    @POST(RootURL + "/visitor")
    void visitor(@FieldMap Map<String, String> oauthParam, Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/visitor")
    OAuthModel visitor(@FieldMap Map<String, String> oauthParam);

    @FormUrlEncoded
    @POST(RootURL + "/ios/account-login")
    void loginEmailRoutine(@Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/ios/account-login")
    OAuthModel loginEmailRoutine(@Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam);

    @FormUrlEncoded
    @POST(RootURL + "/ios/account-register")
    void registerEmailRoutine(
            @Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/ios/account-register")
    OAuthModel registerEmailRoutine(
            @Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
    );
}
