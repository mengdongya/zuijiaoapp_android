package com.zuijiao.controller;

/**
 * definition of broadcast action
 */
public interface MessageDef {

    String ACTION_LOGIN_FINISH = "net.zuijiao.android.login.finish";

    String ACTION_GET_THIRD_PARTY_USER = "net.zuijiao.android.thirdparty.userinfo";

    String ACTION_REFRESH_RECOMMENDATION = "net.zuijiao.android.refresh_recommendation";
    String ACTION_PUSH_RECEIVED = "net.zuijiao.android.push_received";

    String ACTION_ORDER_CREATED = "net.zuijiao.android.banquet.order.created";
    String ACTION_REQUEST_HOST = "net.zuijiao.android.banquet.request.host";

    String ACTION_ORDER_UPDATE = "android.intent.action.ACTION_ORDER_UPDATE";
    String ACTION_ORDER_SURPLUS_TIME = "android.intent.action.ACTION_ORDER_SURPLUS_TIME";
}
