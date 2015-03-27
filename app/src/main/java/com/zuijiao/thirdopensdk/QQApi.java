package com.zuijiao.thirdopensdk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zuijiao.entity.ThirdPartyUserInfo;
import com.zuijiao.entity.ZuiJiaoUser;

public class QQApi extends AbsSDK {
	private static Tencent mTencent;
	private Context mContext ;
	protected final String QQ_ID = "1103495820";
	protected final String QQ_PWD = "NlMKnzZYdhg4TmwE";
	
	public QQApi(Context context){
		super() ;
		this.mContext = context ;
	}
	
	@Override
	public void Login() {

		if (mTencent == null) {
			mTencent = Tencent.createInstance(QQ_ID, mContext);
		}
		if (!mTencent.isSessionValid()) {
			mTencent.login((Activity) mContext, "all", new IUiListener() {

				@Override
				public void onCancel() {

				}

				@Override
				public void onComplete(Object response) {
					JSONObject jsonObject = (JSONObject) response;
					try {
						String token = jsonObject
								.getString(Constants.PARAM_ACCESS_TOKEN);
						String expires = jsonObject
								.getString(Constants.PARAM_EXPIRES_IN);
						String openId = jsonObject
								.getString(Constants.PARAM_OPEN_ID);
						if (!TextUtils.isEmpty(token)
								&& !TextUtils.isEmpty(expires)
								&& !TextUtils.isEmpty(openId)) {
							mTencent.setAccessToken(token, expires);
							mTencent.setOpenId(openId);
						}
					} catch (Exception e) {

					}
					UserInfo mInfo = new UserInfo(mContext, mTencent
							.getQQToken());
					mInfo.getUserInfo(new IUiListener() {

						@Override
						public void onError(UiError arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onComplete(final Object response) {
							new Thread() {

								@Override
								public void run() {

									ZuiJiaoUser zuijiaoUser = ZuiJiaoUser
											.getZuiJiaoUser();
									JSONObject json = (JSONObject) response;
									try {
										zuijiaoUser
												.setUserName(((JSONObject) response)
														.getString("nickname"));
										if (json.has("figureurl")) {
											Bitmap bitmap = getbitmap(json
													.getString("figureurl_qq_2"));
											zuijiaoUser.setHead(bitmap);
										}

									} catch (JSONException e) {
									}

								}

							}.start();

						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub

						}
					});
				}

				@Override
				public void onError(UiError arg0) {
					// TODO Auto-generated method stub

				}

			});
			Log.d("SDKQQAgentPref",
					"FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
			mTencent.logout(mContext);
//			updateUserInfo();
		}
	
	}

	@Override
	public void logout() {

	}

	@Override
	public void getUserInfo( ) {
	}
	
	
	public static Bitmap getbitmap(String imageUri) {
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	@Override
	public boolean isLogin(String token) {
		// TODO Auto-generated method stub
		return false;
	}
}
