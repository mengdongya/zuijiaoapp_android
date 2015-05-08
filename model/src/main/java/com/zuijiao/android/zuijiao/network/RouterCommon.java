package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.GourmetTags;
import com.zuijiao.android.zuijiao.model.common.Restaurants;
import com.zuijiao.android.zuijiao.model.common.TasteTags;

import java.util.List;

/**
 * Created by Chen Hao on 3/30/15.
 */
public enum RouterCommon {
    INSTANCE;

    private IRouterCommon service = Router.getInstance().restAdapter.create(IRouterCommon.class);

    public void tasteTags(OneParameterExpression<TasteTags> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.tasteTags(CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void gourmetTags(OneParameterExpression<List<String>> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        OneParameterExpression<GourmetTags> callback = tags -> {
            successCallback.action(tags.getTags());
        };
        service.gourmetTags(CallbackFactory.getInstance().callback(callback, failureCallback));
    }

    public void restaurantSearch(String keyword, Integer count, OneParameterExpression<Restaurants> successCallback
            , OneParameterExpression<String> failureCallback
    ) {
        service.restaurantSearch(keyword, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void requestSecurityCode(final String phoneNumber
            , final LambdaExpression successCallback
            , final LambdaExpression failureCallback) {
        service.requestSecurityCode(phoneNumber, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }
}
