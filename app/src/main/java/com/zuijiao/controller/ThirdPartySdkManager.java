package com.zuijiao.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.utils.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zuijiao.entity.ThirdPartyUserInfo;
import com.zuijiao.entity.ZuiJiaoUser;
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
