package com.zuijiao.android.zuijiao.network;

import com.squareup.okhttp.Response;
import com.zuijiao.android.zuijiao.model.Banquent.Notifications;
import com.zuijiao.android.zuijiao.model.message.Messages;
import com.zuijiao.android.zuijiao.model.message.NewsList;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by user on 4/3/15.
 */
public interface IRouterMessage {
    String RootURL = "/messages/v1";
    String RootURL_V4 = "/messages/v4";

    @GET(RootURL + "/ios/notifications/info")
    void news(Callback<NewsList> callback);

    @GET(RootURL + "/ios/notifications")
    void message(@Query("showGroupID") Integer newsID
            , @Query("maxID") Integer sinceID
            , @Query("sinceID") Integer toID
            , @Query("count") Integer count
            , Callback<Messages> callback);

    @FormUrlEncoded
    @POST(RootURL + "/ios/notifications/read")
    void markAsRead(@Field("showGroupID") Integer groupID, Callback<Response> callback);

    @POST(RootURL + "/notifications/read")
    void markAsRead(Callback<Response> callback);

    @FormUrlEncoded
    @POST(RootURL_V4 + "/web/notifications/read")
    void markBanquetMsgAsRead(@Field("IDs") String ids, Callback<Response> callback);


    @GET("/messages/v4/web/notifications")
    void banquetNotifications(@Query("status") String status
            , @Query("nextCursor") Integer nextCursor
            , @Query("count") Integer count
            , Callback<Notifications> callback);

}
