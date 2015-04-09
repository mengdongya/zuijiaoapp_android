package com.zuijiao.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.entity.AuthorInfo;
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
        mContext = context ;
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
	
	public void logout(Context context){
		if (mLoginApi  == null) {
           String platform =  PreferenceManager.getInstance(context).getThirdPartyLoginMsg().getPlatform() ;
            if(!isThirdParty(platform)){
                return ;
            }else {
                if(platform.equals(AbsSDK.QQ)){
                    mLoginApi = new QQApi(context) ;
                }else if(platform.equals(AbsSDK.WEIBO)){
                    mLoginApi = new WeiboApi(context) ;
                }else if(platform.equals(AbsSDK.WECHAT)){
                    mLoginApi = new WeixinApi(context) ;
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
    public void onLoginFinish(AuthorInfo user){
        Intent in = new Intent() ;
        in.setAction(MessageDef.ACTION_LOGIN_FINISH) ;
        mContext.sendBroadcast(in);
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
                        bundle.putBoolean("result" ,true);
                        bundle.putString("name", userName);
                        bundle.putString("head_url", imageurl);
                        intent.putExtra("userinfo", bundle);
                        mContext.sendBroadcast(intent);
                    }, () -> {
                        Intent intent = new Intent(
                                MessageDef.ACTION_GET_THIRD_PARTY_USER);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("result" ,false);
                        mContext.sendBroadcast(intent);
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

    public boolean isThirdParty(String platform){
        if(platform==null){
            return false ;
        }
        return platform.equals(AbsSDK.QQ) || platform.equals(AbsSDK.WECHAT) || platform.equals(AbsSDK.WEIBO) ;
    }
}
