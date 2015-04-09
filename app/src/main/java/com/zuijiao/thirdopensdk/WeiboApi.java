package com.zuijiao.thirdopensdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.open.t.Weibo;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.entity.AuthorInfo;

public class WeiboApi extends AbsSDK {
	protected final String WEIBO_PWD = "07bb6cb0e7a3db40fbb98ac8d2cf43d5";
	private AuthInfo mAuthInfo = null;
	private SsoHandler mSsoHandler;
	private Context mContext = null;
	private Oauth2AccessToken mAccessToken;
	private UsersAPI mUsersAPI;
	private AuthorInfo userInfo;
	protected final String WEIBO_ID = "2632486726";// key
	public static final String SCOPE = "all";
	protected static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    private Weibo mWeibo = null ;
	public WeiboApi(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	public void Login(final LoginListener mListener ) {
		if (mAuthInfo == null) {
			mAuthInfo = new AuthInfo(mContext, WEIBO_ID, REDIRECT_URL,
					SCOPE);
		}
		mSsoHandler = new SsoHandler((Activity) mContext, mAuthInfo);
		mSsoHandler.authorize(new WeiboAuthListener() {

            @Override
            public void onCancel() {
                Log.i("weibo", "oncancel");
            }

            @Override
            public void onComplete(Bundle values) {
                mAccessToken = Oauth2AccessToken.parseAccessToken(values);
                if (mAccessToken.isSessionValid()) {
                    String uid = values.getString("uid");
                    String token = values.getString("access_token");

                } else {
                    String code = values.getString("code");
                    String message = "weibo failed";
                    if (!TextUtils.isEmpty(code)) {
                        message = message + "\nObtained the code: " + code;
                    }
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onWeiboException(WeiboException arg0) {
                Log.i("weibo", "onWeiboException");
            }

        });

	}

	@Override
	public void logout() {

	}

	@Override
	public void getUserInfo() {
		mUsersAPI = new UsersAPI(mContext, WEIBO_ID, mAccessToken);
		long uid = Long.parseLong(mAccessToken.getUid());
		mUsersAPI.show(uid, new MyRequestListener());
	}

	private class MyRequestListener implements RequestListener {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				User user = User.parse(response);
				if (user != null) {
					userInfo = new AuthorInfo();
					userInfo.setUserName(user.screen_name);
					Intent intent = new Intent(
							MessageDef.ACTION_GET_THIRD_PARTY_USER);
					Bundle bundle = new Bundle();
					bundle.putString("name", user.screen_name);
					intent.putExtra("userinfo", bundle);
					mContext.sendBroadcast(intent);
				} else {
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
		}
	}

	@Override
	public boolean isLogin(String token) {
		boolean bLogin = false;
		try {
			bLogin = mAccessToken.isSessionValid();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return bLogin;
	};
}
