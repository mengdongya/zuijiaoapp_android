package com.zuijiao.android.zuijiao.network;

import com.squareup.okhttp.Response;
import com.zuijiao.android.zuijiao.model.Comments;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUsers;
import retrofit.Callback;
import retrofit.http.*;

/**
 * Created by Chen Hao on 3/18/15.
 */
public interface IRouterGourmet {
    final String RootURL = "/foods/v1";

    @FormUrlEncoded
    @POST(RootURL + "/collection/create")
    void addFavorite(@Field("ID") Integer gourmetId, Callback<Response> callback);

    @FormUrlEncoded
    @POST(RootURL + "/collection/destroy")
    void removeFavorite(@Field("ID") Integer gourmetId, Callback<Response> callback);

    @FormUrlEncoded
    @GET(RootURL + "/collected/cuisines")
    void fetchFavorites(@Query("sinceID") Integer sinceId
            , @Query("maxID") Integer toId
            , @Query("count") Integer count
            , Callback<Gourmets> callback
    );

    @GET(RootURL + "/user/{userID}/collected/cuisines")
    void fetchFavoritesByUserId(@Path("userID") Integer userId
            , @Query("sinceID") Integer sinceId
            , @Query("maxID") Integer toId
            , @Query("count") Integer count
            , Callback<Gourmets> callback
    );

    @GET(RootURL + "/cuisine/{gourmetID}/collectors")
    void fetchWouldLikeToListByGourmetId(@Path("gourmetID") Integer gourmetId
            , @Query("count") Integer count
            , Callback<WouldLikeToEatUsers> callback
    );

    @GET(RootURL + "/cuisine/{gourmetID}/comments")
    void fetchComments(@Path("gourmetID") Integer gourmetId
            , @Query("sinceID") Integer sinceId
            , @Query("maxID") Integer toId
            , @Query("count") Integer count
            , Callback<Comments> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/cuisine/comment/create")
    void postComment(@Field("cuisineID") Integer gourmetId
            , @Field("text") String comment
            , Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/comment/destroy")
    void removeComment(@Field("ID") Integer commentId
            , Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/comment/reply/create")
    void replyCommentTo(@Field("replyID") Integer commentId
            , @Field("text") String comment
            , Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/cuisine/create")
    void addGourmet(@Field("title") String name
            , @Field("address") String address
            , @Field("price") String price
            , @Field("description") String description
            , @Field("imageUrls") String imageUrlList
            , @Field("tags") String tagList
            , @Field("provinceID") Integer provinceId
            , @Field("cityID") Integer cityId
            , @Field("isFeast") Boolean isPrivate
            , Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/cuisine/update")
    void updateGourmet(@Field("ID") Integer gourmetId
            , @Field("address") String address
            , @Field("price") String price
            , @Field("description") String description
            , @Field("imageUrls") String imageUrlList
            , @Field("tags") String tagList
            , @Field("isFeast") Boolean isPrivate
            , Callback<Response> callback
    );

    @GET(RootURL + "/cuisine/{gourmetID}")
    void fetchGourmetInformation(@Path("gourmetID") Integer gourmetId
            , Callback<Gourmet> callback
    );

    @GET(RootURL + "/cuisines")
    void fetchMyRecommendationList(@Query("maxID") Integer toGourmetId
            , @Query("count") Integer count
            , Callback<Gourmets> callback
    );

    @GET(RootURL + "/user/{userID}/cuisines")
    void fetchRecommendationListByUserId(@Path("userID") Integer userId
            , @Query("sinceID") Integer fromGourmetId
            , @Query("maxID") Integer toGourmetId
            , @Query("count") Integer count
            , Callback<Gourmets> callback
    );

    @GET(RootURL + "/selected/cuisines")
    void fetchOurChoice(@Query("sinceID") Integer fromGourmetId
            , @Query("maxID") Integer toGourmetId
            , @Query("count") Integer count
            , Callback<Gourmets> callback
    );

    @FormUrlEncoded
    @POST(RootURL + "/cuisine/destroy")
    void removeMyRecommendation(@Field("ID") Integer gourmetId, Callback<Response> callback);


}
