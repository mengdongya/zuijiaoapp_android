package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.SocialEntities;

/**
 * Created by Chen Hao on 4/28/15.
 */
public enum RouterSocial {
    INSTANCE;

    private IRouterSocial service = Router.getInstance().restAdapter.create(IRouterSocial.class);

//    public void getFollowers(Integer count
//            , Integer toUserId
//            , OneParameterExpression<SocialEntities> successCallback
//            , OneParameterExpression<String> failureCallback)
//    {
//        service.getFollowers(toUserId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
//    }
//
//    public void getFollowings(Integer count
//            , Integer toUserId
//            , OneParameterExpression<SocialEntities> successCallback
//            , OneParameterExpression<String> failureCallback)
//    {
//        service.getFollowings(toUserId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
//    }

    public void getFollowingsOfUserId(Integer userId
    , Integer toUserId
    , Integer count
    , OneParameterExpression<SocialEntities> successCallback
            , OneParameterExpression<String> failureCallback) {
        service.getFollowingsOfUserId(userId, toUserId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void getFollowersOfUserId(Integer userId
            , Integer toUserId
            , Integer count
            , OneParameterExpression<SocialEntities> successCallback
            , OneParameterExpression<String> failureCallback) {
        service.getFollowersOfUserId(userId, toUserId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void unFollow(Integer userId, LambdaExpression successCallback
            , OneParameterExpression<String> failureCallback)
    {
        service.unFollow(userId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void follow(Integer userId, LambdaExpression successCallback
            , OneParameterExpression<String> failureCallback)
    {
        service.follow(userId, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

}
