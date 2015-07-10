package com.zuijiao.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.thirdopensdk.AbsSDK;
import com.zuijiao.thirdopensdk.QQApi;
import com.zuijiao.thirdopensdk.WeiboApi;
import com.zuijiao.thirdopensdk.WeixinApi;

import net.zuijiao.android.zuijiao.R;

/**
 * management of third-party login
 */
public class ThirdPartySDKManager implements AbsSDK.LoginListener {
    public static final int CLOUD_TYPE_NONE = 0;
    public static final int CLOUD_TYPE_QQ = 1;
    public static final int CLOUD_TYPE_WEIXIN = 2;
    public static final int CLOUD_TYPE_WEIBO = 3;

    private static Context mContext = null;
    private static ThirdPartySDKManager mCloudMng = null;
    private AbsSDK mCurrentApi = null;

    public AbsSDK getmLoginApi() {
        return mLoginApi;
    }

    private AbsSDK mLoginApi = null;
    private AuthorInfo mThirdPartyUser;

    private ThirdPartySDKManager() {
    }

    public static ThirdPartySDKManager getInstance(Context context) {
        if (mCloudMng == null) {
            mCloudMng = new ThirdPartySDKManager();
        }
        mContext = context;
        return mCloudMng;
    }

    public void login(int apiType) {
        switch (apiType) {
            case CLOUD_TYPE_QQ:
                mLoginApi = new QQApi(mContext);
                break;
            case CLOUD_TYPE_WEIBO:
                mLoginApi = new WeiboApi(mContext);
                break;
            case CLOUD_TYPE_WEIXIN:
                mLoginApi = new WeixinApi(mContext);
                break;
            default:
                mLoginApi = null;
                return;
        }
        mLoginApi.Login(this);
    }

    public void refreshThirdPartyUserInfo() {
        if (mLoginApi != null && mLoginApi.isLogin(null)) {
            mLoginApi.getUserInfo();
        }
    }

    public void logout(Context context) {
        if (mLoginApi == null) {
            String platform = PreferenceManager.getInstance(context).getThirdPartyLoginMsg().getPlatform();
            if (!isThirdParty(platform)) {
                return;
            } else {
                if (platform.equals(AbsSDK.QQ)) {
                    mLoginApi = new QQApi(context);
                } else if (platform.equals(AbsSDK.WEIBO)) {
                    mLoginApi = new WeiboApi(context);
                } else if (platform.equals(AbsSDK.WECHAT)) {
                    mLoginApi = new WeixinApi(context);
                }
            }
        }
        mLoginApi.logout();
    }

    // public void onLoginResult(int requestCode, int resultCode, Intent data) {
    // if (mSsoHandler != null) {
    // mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    // }
    // }

    public AuthorInfo getThirdPartyUser() {
        return mThirdPartyUser;
    }

    public void setThirdPartyUser(AuthorInfo ThirdPartyUser) {
        this.mThirdPartyUser = ThirdPartyUser;
    }

    public void onLoginFinish(AuthorInfo authInfo) {
        Intent in = new Intent();
        in.setAction(MessageDef.ACTION_LOGIN_FINISH);
        mContext.sendBroadcast(in);
        String userName = authInfo.getUserName();
        String openid = authInfo.getUid();
        String imageurl = authInfo.getHeadPath();
        String platsform = authInfo.getPlatform();
        String token = authInfo.getToken();
        Router.getOAuthModule().register(userName, imageurl, null, openid, platsform, Optional.<String>empty(), Optional.of(token), new OneParameterExpression<Boolean>() {
            @Override
            public void action(Boolean isNew) {
                Toast.makeText(mContext, mContext.getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                if (!isNew) {
                    String avataUrl = null;
                    if (Router.getInstance().getCurrentUser().get().getAvatarURLSmall().isPresent()) {
                        avataUrl = Router.getInstance().getCurrentUser().get().getAvatarURLSmall().get();
                        authInfo.setHeadPath(avataUrl);
                    }
                    authInfo.setUserName(Router.getInstance().getCurrentUser().get().getNickName());
                }
                authInfo.setUserId(Router.getInstance().getCurrentUser().get().getIdentifier());
                PreferenceManager.getInstance(mContext).saveThirdPartyLoginMsg(authInfo);
                Intent intent = new Intent(
                        MessageDef.ACTION_GET_THIRD_PARTY_USER);
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", true);
                bundle.putString("name", Router.getInstance().getCurrentUser().get().getNickName());
                bundle.putString("head_url", authInfo.getHeadPath());
                intent.putExtra("userinfo", bundle);
                mContext.sendBroadcast(intent);
            }
        }
                , new OneParameterExpression<Integer>() {
            @Override
            public void action(Integer integer) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ;

    public void onLoginFailed() {

    }

    ;

    public boolean isThirdParty(String platform) {
        if (platform == null) {
            return false;
        }
        return platform.equals(AbsSDK.QQ) || platform.equals(AbsSDK.WECHAT) || platform.equals(AbsSDK.WEIBO);
    }

    public void onLoginResult(int requestCode, int resultCode, Intent data) {
        mLoginApi.onLoginResult(requestCode, resultCode, data);
    }
}
