package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Notifications;
import com.zuijiao.android.zuijiao.model.message.Messages;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.message.NewsList;

import java.util.List;

/**
 * Created by user on 4/3/15.
 */
public enum RouterMessage {
    INSTANCE;

    private IRouterMessage service = Router.getInstance().restAdapter.create(IRouterMessage.class);

    /**
     * @param successCallback
     * @param failureCallback
     */
    public void notifications(
            OneParameterExpression<NewsList> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.news(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    /**
     * @param successCallback
     * @param failureCallback
     */
    public void message(News.NotificationType type
            , Integer sinceID
            , Integer toID
            , Integer count
            , OneParameterExpression<Messages> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.message(type.index(), sinceID, toID, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    /**
     * @param successCallback
     * @param failureCallback
     */
    public void markAsRead(News.NotificationType type
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.markAsRead(type.index(), CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    /**
     * @param successCallback
     * @param failureCallback
     */
    public void markAsRead(
            LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.markAsRead(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }


    public void markBanquetMsgAsRead(List<Integer> ids
            , LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.markBanquetMsgAsRead(ids, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }


    public void banquetNotifications(String status
            , Integer nextCursor
            , Integer count
            , OneParameterExpression<Notifications> successCallback
            , OneParameterExpression<String> failureCallback) {
        service.banquetNotifications(status, nextCursor, count, CallbackFactory.getInstance().callback(successCallback, failureCallback)) ;

    }
}
