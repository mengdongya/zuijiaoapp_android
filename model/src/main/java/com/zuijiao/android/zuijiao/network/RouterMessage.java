package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.message.NewsList;

/**
 * Created by user on 4/3/15.
 */
public enum RouterMessage {
    INSTANCE;

    private IRouterMessage service = Router.INSTANCE.restAdapter.create(IRouterMessage.class);

    /**
     * 获取用户分组新消息信息
     *
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
     * 获取指定分组下的所有消息列表
     *
     * @param type  分组类型
     * @param sinceID   从几开始
     * @param toID  到几结束
     * @param count 拿多少条
     * @param successCallback
     * @param failureCallback
     */
    public void message(News.NotificationType type
            , Integer sinceID
            , Integer toID
            , Integer count
            , OneParameterExpression<Message> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.message(type.index(), sinceID, toID, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    /**
     * 标记指定类型为已读
     *
     * @param type  分组类型
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
     * 标记所有信息为已读
     *
     * @param successCallback
     * @param failureCallback
     */
    public void markAsRead(
            LambdaExpression successCallback
            , LambdaExpression failureCallback
    ) {
        service.markAsRead(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

}
