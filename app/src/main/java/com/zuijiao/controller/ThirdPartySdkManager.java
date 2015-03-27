package com.zuijiao.controller;

import android.content.Context;

import com.zuijiao.entity.ThirdPartyUserInfo;
import com.zuijiao.thirdopensdk.AbsSDK;
import com.zuijiao.thirdopensdk.QQApi;
import com.zuijiao.thirdopensdk.WeiboApi;
import com.zuijiao.thirdopensdk.WeixinApi;

public class ThirdPartySdkManager {
	public static final int CLOUD_TYPE_NONE = 0;
	public static final int CLOUD_TYPE_QQ = 1;
	public static final int CLOUD_TYPE_WEIXIN = 2;
	public static final int CLOUD_TYPE_WEIBO = 3;

	private Context mContext = null;
	private static ThirdPartySdkManager mCloudMng = null;
	private AbsSDK mCurrentApi = null;
	private AbsSDK mLoginApi = null;
	private ThirdPartyUserInfo mThirdPartyUser;

	private ThirdPartySdkManager(Context context) {
		this.mContext = context;
	}

	public static ThirdPartySdkManager getInstance(Context context) {
		if (mCloudMng == null) {
			mCloudMng = new ThirdPartySdkManager(context);
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
		mLoginApi.Login();
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

}
