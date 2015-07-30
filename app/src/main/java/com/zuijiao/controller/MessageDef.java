package com.zuijiao.controller;

/**
 * definition of broadcast action
 */
public interface MessageDef {

    public static final String ACTION_LOGIN_FINISH = "net.zuijiao.android.login.finish";

    public static final String ACTION_GET_THIRD_PARTY_USER = "net.zuijiao.android.thirdparty.userinfo";

    public static final String ACTION_REFRESH_RECOMMENDATION = "net.zuijiao.android.refresh_recommendation";
    public static final String ACTION_PUSH_RECEIVED = "net.zuijiao.android.push_received";

    public static final String ACTION_ORDER_CREATED = "net.zuijiao.android.banquet.order.created";

    public static final String ACTION_ORDER_UPDATE = "android.intent.action.ACTION_ORDER_UPDATE";
}
