package com.zuijiao.android.zuijiao.network;

import com.squareup.okhttp.Response;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentForTheme;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.model.OrderAuth;
import com.zuijiao.android.zuijiao.model.OrderAuthV3;

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
    String RootURL = "/feast/v2";
    String RootURL_V3 = "/feast/v3";


    @POST(RootURL_V3 + "/ios/order/create")
    @FormUrlEncoded
    void createOrder(@Field("eventID") Integer themeId
            , @Field("note") String remark
            , @Field("quantity") Integer quantity
            , @Field("mobile") String phoneNumber
            , @Field("code") String code
            , Callback<Orders> restaurantsCallback
    );

    @POST(RootURL + "/ios/order/create")
    @FormUrlEncoded
    void createOrder(@Field("eventID") Integer themeId
            , @Field("note") String remark
            , @Field("payWay") String payMethod
            , Callback<OrderAuth> restaurantsCallback
    );

    @POST(RootURL_V3 + "/ios/order/{id}/cancel")
    void cancelOrder(@Path("id") Integer orderId
            , Callback<Response> callback);

    @POST(RootURL + "/ios/comment/create")
    @FormUrlEncoded
    void createComment(@Field("orderID") Integer orderId
            , @Field("content") String content
            , @Field("score") Integer score
            , Callback<Response> callback
    );

    @GET(RootURL_V3 + "/ios/orders")
    void orders(@Query("status") String status
            , @Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Orders> callback
    );

    //MARK: - Theme

    @GET(RootURL_V3 + "/ios/events")
    void themesOfPublic(@Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Banquents> callback
    );

    @GET(RootURL_V3 + "/ios/event/{id}")
    void theme(@Path("id") Integer identifier
            , Callback<BanquentForTheme> callback
    );


    @GET(RootURL_V3 + "/ios/seller/{id}/events")
    void themesOfMaster(@Path("id") Integer identifier
            , @Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Banquents> callback
    );

    @GET(RootURL_V3 + "/ios/user/{id}/events")
    void themesOfParticipator(@Path("id") Integer identifier
            , @Query("maxID") Integer sinceId
            , @Query("count") Integer count
            , Callback<Banquents> callback
    );

    @GET(RootURL + "/ios/seller/{id}/comments")
    void commentsOfBanquent(@Path("id") Integer sellerId
            , @Query("maxID") Integer maxID
            , @Query("count") Integer count
            , Callback<Reviews> callback
    );

    @GET(RootURL_V3 + "/ios/order/{orderID}/pay")
    void payOrder(@Path("orderID") Integer orderId
            , @Query("payWay") String payMethod
            , @Query("platform") String payPlatform
            , Callback<OrderAuthV3> restaurantsCallback
    );
}
