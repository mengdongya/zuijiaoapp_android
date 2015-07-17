package com.zuijiao.thirdopensdk;

import android.content.Intent;

import com.zuijiao.entity.AuthorInfo;

public abstract class AbsSDK {
    public static final String QQ = "qq";
    public static final String WEIBO = "weibo";
    public static final String WECHAT = "wechat";

    public abstract void Login(final LoginListener mListener) throws Throwable ;

    public abstract void logout();

    public abstract void getUserInfo();

    public abstract boolean isLogin(String token);

    public void onLoginResult(int requestCode, int resultCode, Intent data) {
    }

    protected LoginListener mListener;

    public interface LoginListener {
        void onLoginFinish(AuthorInfo userInfo);

        void onLoginFailed();
    }
}
