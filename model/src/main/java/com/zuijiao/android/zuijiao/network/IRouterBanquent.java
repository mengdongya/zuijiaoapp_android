package com.zuijiao.android.zuijiao.network;

import com.squareup.okhttp.Response;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentForTheme;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.model.OrderAuth;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by user on 6/18/15.
 */
public interface IRouterBanquent {
    String RootURL = "/feast/v3";

    //MARK: - Order
    @POST(RootURL + "/ios/order/create")
    @FormUrlEncoded
    void createOrder(@Field("eventID") Integer themeId
            , @Field("mobile") String phoneNumber
            , @Field("code") String code
            , @Field("note") String remark
            , @Field("payWay") String payMethod
            , @Field("quantity") Integer quantity
            , Callback<OrderAuth> restaurantsCallback
    );

    @POST(RootURL + "/ios/order/create")
    @FormUrlEncoded
    void createOrder(@Field("eventID") Integer themeId
            , @Field("note") String remark
            , @Field("payWay") String payMethod
            , Callback<OrderAuth> restaurantsCallback
    );

    @POST(RootURL + "/ios/comment/create")
    @FormUrlEncoded
    void createComment(@Field("orderID") Integer orderId
            , @Field("content") String content
            , @Field("score") Integer score
            , Callback<Response> callback
    );

    @GET(RootURL + "/ios/orders")
    void orders(@Query("status") String status
            , @Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Orders> callback
    );

    //MARK: - Theme

    @GET(RootURL + "/ios/events")
    void themesOfPublic(@Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Banquents> callback
    );

    @GET(RootURL + "/ios/event/{identifier}")
    void theme(@Path("identifier") Integer identifier
            , Callback<BanquentForTheme> callback
    );


    @GET(RootURL + "/ios/seller/{identifier}/events")
    void themesOfMaster(@Path("identifier") Integer identifier
            , @Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Banquents> callback
    );

    @GET(RootURL + "/ios/user/{identifier}/events")
    void themesOfParticipator(@Path("identifier") Integer identifier
            , @Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Banquents> callback
    );

    @GET(RootURL + "/ios/seller/{id}/comments")
    void commentsofBanquent(@Path("id") Integer sellerId
            , @Query("maxID") Integer maxID
            , @Query("count") Integer count
            , Callback<Reviews> callback
    );


}
