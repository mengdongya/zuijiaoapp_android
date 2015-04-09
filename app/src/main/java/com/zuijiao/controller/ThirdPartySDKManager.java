package com.zuijiao.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.entity.ThirdPartyUserInfo;
import com.zuijiao.thirdopensdk.AbsSDK;
import com.zuijiao.thirdopensdk.QQApi;
import com.zuijiao.thirdopensdk.WeiboApi;
import com.zuijiao.thirdopensdk.WeixinApi;

import net.zuijiao.android.zuijiao.R;

public class ThirdPartySDKManager implements AbsSDK.LoginListener{
	public static final int CLOUD_TYPE_NONE = 0;
	public static final int CLOUD_TYPE_QQ = 1;
	public static final int CLOUD_TYPE_WEIXIN = 2;
	public static final int CLOUD_TYPE_WEIBO = 3;

	private Context mContext = null;
	private static ThirdPartySDKManager mCloudMng = null;
	private AbsSDK mCurrentApi = null;
	private AbsSDK mLoginApi = null;
	private ThirdPartyUserInfo mThirdPartyUser;

	private ThirdPartySDKManager(Context context) {
		this.mContext = context;
	}

	public static ThirdPartySDKManager getInstance(Context context) {
		if (mCloudMng == null) {
			mCloudMng = new ThirdPartySDKManager(context);
		}
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
	
	public void logout(){
		if (mLoginApi != null) {
			mLoginApi.logout();
		}
	}

	// public void onLoginResult(int requestCode, int resultCode, Intent data) {
	// if (mSsoHandler != null) {
	// mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	// }
	// }

	public ThirdPartyUserInfo getThirdPartyUser() {
		return mThirdPartyUser;
	}

	public void setThirdPartyUser(ThirdPartyUserInfo ThirdPartyUser) {
		this.mThirdPartyUser = ThirdPartyUser;
	}
    public void onLoginFinish(ThirdPartyUserInfo user){
        String userName = user.getUserName() ;
        String openid = user.getUid() ;
        String imageurl = user.getHeadPath() ;
        String platsform = user.getPlatform() ;
        String token = user.getToken() ;
        Router.getOAuthModule().register(userName, imageurl, openid, platsform, Optional.<String>empty(), Optional.of(token), () -> {
                    Router.getOAuthModule().login(openid, "qq", Optional.empty(), Optional.of(token), () -> {
                        PreferenceManager.getInstance(mContext).saveThirdPartyLoginMsg(user);
                        Intent intent = new Intent(
                                MessageDef.ACTION_GET_THIRD_PARTY_USER);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", userName);
                        bundle.putString("head_url", imageurl);
                        intent.putExtra("userinfo", bundle);
                        mContext.sendBroadcast(intent);
                    }, () -> {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    });
                }
                , () ->

                {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                }

        );
    } ;
    public void onLoginFailed() {

    };
}
