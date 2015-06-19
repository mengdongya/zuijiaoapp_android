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
    final String RootURL = "/oauth";

    @FormUrlEncoded
    @POST(RootURL + "/v1/login")
    void login(@Field("openId") String openID
            , @Field("platform") String platform
            , @Field("deviceToken") String deviceToken
            , @Field("openToken") String openToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/v1/login")
    OAuthModel login(@Field("openId") String openID
            , @Field("platform") String platform
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
    );

    @FormUrlEncoded
    @POST(RootURL + "/v2/ios/register")
    void register(@Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("gender") String gender
            , @Field("openId") String openID
            , @Field("platform") String platform
            , @Field("deviceToken") String deviceToken
            , @Field("openToken") String openToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/v1/ios/register")
    OAuthModel register(@Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("openId") String openID
            , @Field("platform") String platform
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
    );

    @FormUrlEncoded
    @POST(RootURL + "/v1/visitor")
    void visitor(@FieldMap Map<String, String> oauthParam, Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/v1/visitor")
    OAuthModel visitor(@FieldMap Map<String, String> oauthParam);

    @FormUrlEncoded
    @POST(RootURL + "/v1/ios/account-login")
    void loginEmailRoutine(@Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback);

    @FormUrlEncoded
    @POST(RootURL + "/v1/ios/account-login")
    OAuthModel loginEmailRoutine(@Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam);

    @FormUrlEncoded
    @POST(RootURL + "/v2/ios/register/account")
    void registerEmailRoutine(
            @Field("nickname") String name
            , @Field("imageUrl") String avatarURL
            , @Field("email") String email
            , @Field("password") String password
            , @Field("openToken") String openToken
            , @Field("gender") String gender
            , @Field("deviceToken") String deviceToken
            , @FieldMap Map<String, String> oauthParam
            , Callback<OAuthModel> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/v1/ios/account-register")
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
