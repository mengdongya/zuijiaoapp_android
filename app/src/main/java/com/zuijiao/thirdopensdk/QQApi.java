package com.zuijiao.thirdopensdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zuijiao.entity.AuthorInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QQApi extends AbsSDK {
    private static Tencent mTencent;
    private Context mContext;
    public static final String QQ_ID = "1103495820";
    public static final String QQ_PWD = "NlMKnzZYdhg4TmwE";

    public QQApi(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public void Login(final LoginListener mListener) {

        if (mTencent == null) {
            mTencent = Tencent.createInstance(QQ_ID, mContext);
        }
        if (!mTencent.isSessionValid()) {
            mTencent.login((Activity) mContext, "all", new IUiListener() {

                @Override
                public void onCancel() {
                    mListener.onLoginFailed();
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
                                          }

                                          @Override
                                          public void onComplete(final Object response) {
                                              try {
                                                  String openid = mTencent.getOpenId();
                                                  String token = mTencent.getAccessToken();
                                                  String userName = ((JSONObject) response).getString("nickname");
                                                  String imageurl = ((JSONObject) response).getString("figureurl_qq_2");
                                                  String platsform = "qq";
                                                  AuthorInfo userInfo = new AuthorInfo();
                                                  userInfo.setUserName(userName);
                                                  userInfo.setUid(openid);
                                                  userInfo.setToken(token);
                                                  userInfo.setPlatform("qq");
                                                  userInfo.setHeadPath(imageurl);
                                                  mListener.onLoginFinish(userInfo);
                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                              }
                                          }

                                          @Override
                                          public void onCancel() {

                                          }
                                      }

                    );
                }

                @Override
                public void onError(UiError arg0) {
                    mListener.onLoginFailed();
                }

            });
            Log.d("SDKQQAgentPref",
                    "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        } else {
            mTencent.logout(mContext);
            Login(mListener);
//            mListener.onLoginFailed();
            Log.e("qqLogin", "mTencent.isSessionValid()");
            Log.e("qqLogin", "mTencent.isSessionValid()");
            Log.e("qqLogin", "mTencent.isSessionValid()");
            Log.e("qqLogin", "mTencent.isSessionValid()");
            Log.e("qqLogin", "mTencent.isSessionValid()");
//			updateUserInfo();
        }

    }

    @Override
    public void logout() {

    }

    @Override
    public void getUserInfo() {
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
        return false;
    }
}
