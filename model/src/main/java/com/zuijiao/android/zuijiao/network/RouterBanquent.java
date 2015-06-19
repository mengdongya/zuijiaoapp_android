package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentForTheme;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;

/**
 * Created by user on 6/18/15.
 */
public enum RouterBanquent {
    INSTANCE;

    private IRouterBanquent service = Router.getInstance().restAdapter.create(IRouterBanquent.class);

    public void orders(OrderStatus status
            , Integer maxId
            , Integer count
            , final OneParameterExpression<Orders> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.orders(status.toString(), maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void themesOfPublic(Integer maxId
            , Integer count
            , final OneParameterExpression<Banquents> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.themesOfPublic(maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void theme(Integer identifier
            , final OneParameterExpression<Banquent> successCallback
            , final OneParameterExpression<String> failureCallback) {
        final OneParameterExpression<BanquentForTheme> helper = banquentForTheme -> successCallback.action(banquentForTheme.getHelper());
        service.theme(identifier, CallbackFactory.getInstance().callback(helper, failureCallback));
    }

    public void themesOfMaster(
            Integer identifier
            , Integer maxId
            , Integer count
            , final OneParameterExpression<Banquents> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.themesOfMaster(identifier, maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

    public void themesOfParticipator(
            Integer identifier
            , Integer maxId
            , Integer count
            , final OneParameterExpression<Banquents> successCallback
            , final OneParameterExpression<String> failureCallback) {
        service.themesOfParticipator(identifier, maxId, count, CallbackFactory.getInstance().callback(successCallback, failureCallback));
    }

}
