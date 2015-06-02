package com.zuijiao.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zuijiao.android.zuijiao.model.common.Configuration;
import com.zuijiao.entity.AuthorInfo;

import java.util.Date;

public class PreferenceManager {
    private static final String LOGTAG = "PreferenceManager";
    private static PreferenceInfo mPreferInfo = null;
    private static PreferenceManager mPreferenceMng = null;
    private static Context mContext = null;
    private static AuthorInfo authInfo = null;
    private Configuration configs;

    private PreferenceManager() {

    }

    public static PreferenceManager getInstance(Context context) {
        if (mPreferenceMng == null) {
            mContext = context;
            mPreferenceMng = new PreferenceManager();
        }
        if (mPreferInfo == null) {
            mPreferInfo = initPreferenceInfo();
        }
        return mPreferenceMng;
    }

    /**
     * PreferenceInfo constructor , one instance
     *
     * @return
     */

    public static AuthorInfo getAuthInfo() {
        return authInfo;
    }

    /**
     * init from shared_preference
     *
     * @return
     */
    public static PreferenceInfo initPreferenceInfo() {
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        boolean bFirstLaunch = sp.getBoolean(
                PreferencesDef.IS_APP_FIRST_LAUNCH, true);
        String strUserKry = sp.getString(PreferencesDef.USER_KEY, "");
        if (mPreferInfo == null) {
            mPreferInfo = new PreferenceInfo();
        }
        mPreferInfo.setAppFirstLaunch(bFirstLaunch);
        mPreferInfo.setUserKey(strUserKry);
        return mPreferInfo;
    }

    public Configuration getConfigs() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        boolean notifyFollowed = sp.getBoolean("notify_followed", true);
        boolean notifyLike = sp.getBoolean("notify_like", true);
        boolean notifyComment = sp.getBoolean("notify_comment", true);
        return new Configuration(notifyFollowed, notifyLike, notifyComment);
    }

    public void saveConfig(Configuration config) {
        Editor editor = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean("notify_followed", config.isNotifyFollowed());
        editor.putBoolean("notify_like", config.isNotifyLike());
        editor.putBoolean("notify_comment", config.isNotifyComment());
        ;
        editor.commit();
    }

    public PreferenceInfo getPreferInfo() {
        return mPreferInfo;
    }

    /**
     * Deprecated
     *
     * @param mPreferInfo
     */
    public void setPreferInfo(PreferenceInfo mPreferInfo) {
        PreferenceManager.mPreferInfo = mPreferInfo;
    }

    public void saveFirstLaunch() {
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(PreferencesDef.IS_APP_FIRST_LAUNCH,
                mPreferInfo.isAppFirstLaunch());
        editor.commit();
    }

    public void writeAccessToken(Context context, Oauth2AccessToken token) {
        if (null == context || null == token) {
            return;
        }
        mPreferInfo.setUserId(token.getUid());
        mPreferInfo.setUserKey(token.getToken());
        SharedPreferences pref = context.getSharedPreferences(
                PreferencesDef.FILE_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(PreferencesDef.USER_ID, token.getUid());
        editor.putString(PreferencesDef.USER_KEY, token.getToken());
        editor.commit();
    }

    public void saveRefreshToken(String token) {
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(PreferencesDef.USER_KEY, mPreferInfo.getUserKey());
        editor.commit();
    }

    public void saveThirdPartyLoginMsg(AuthorInfo userInfo) {
        authInfo = userInfo;
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("nickname", userInfo.getUserName());
        editor.putString("headurl", userInfo.getHeadPath());
        editor.putString("openid", userInfo.getUid());
        editor.putString("platform", userInfo.getPlatform());
        editor.putString("token", userInfo.getToken());
        editor.putString("email", userInfo.getEmail());
        editor.putString("password", userInfo.getPassword());
        editor.putInt("user_id", userInfo.getUserId());
        editor.commit();
    }

    public void clearThirdPartyLoginMsg() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        authInfo = null;
        Editor editor = sp.edit();
        editor.putString("nickname", "");
        editor.putString("headurl", "");
        editor.putString("openid", "");
        editor.putString("platform", "");
        editor.putString("token", "");
        editor.putString("email", "");
        editor.putString("password", "");
        editor.putInt("user_id", -1);
        editor.commit();
    }

    public void saveAvatarPath(String avatarPath) {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        boolean b = sp.edit().putString("headurl", avatarPath).commit();
    }


    public void saveNickname(String nickname) {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        boolean b = sp.edit().putString("nickname", nickname).commit();
    }

    public AuthorInfo getThirdPartyLoginMsg() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        authInfo = new AuthorInfo();
        authInfo.setHeadPath(sp.getString("headurl", ""));
        authInfo.setPlatform(sp.getString("platform", ""));
        authInfo.setToken(sp.getString("token", ""));
        authInfo.setUid(sp.getString("openid", ""));
        authInfo.setUserName(sp.getString("nickname", ""));
        authInfo.setEmail(sp.getString("email", ""));
        authInfo.setPassword(sp.getString("password", ""));
        authInfo.setUserId(sp.getInt("user_id", -1));
        return authInfo;
    }

    public int getStoredUserId() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("user_id", -1);
    }

    public String getStoredBindEmail() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("email", "");
    }

    public void saveMainLastRefreshTime(Long time) {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        sp.edit().putLong("main_refresh_time", time).commit();
    }

    public void saveFavorLastRefreshTime(Long time) {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        sp.edit().putLong("favor_refresh_time", time).commit();
    }

    public void saveMsgLastRefreshTime(Long time) {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        sp.edit().putLong("msg_refresh_time", time).commit();
    }

    public long getMainLastRefreshTime() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getLong("main_refresh_time", new Date().getTime());
    }

    public long getFavorLastRefreshTime() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getLong("favor_refresh_time", new Date().getTime());
    }

    public long getMsgLastRefreshTime() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getLong("msg_refresh_time", new Date().getTime());
    }

    public interface PreferencesDef {
        public static final String FILE_NAME = "settings";
        public static final String IS_APP_FIRST_LAUNCH = "boolean_first_launch";
        public static final String USER_KEY = "str_user_key";
        public static final String USER_ID = "str_user_id";
    }

    /**
     * setting items
     *
     * @author Bobo
     */
    public static class PreferenceInfo {
        private boolean isAppFirstLaunch = true;
        private String userKey = "";
        private String userId = "";

        public String getUserKey() {
            return userKey;
        }

        public void setUserKey(String userKey) {
            this.userKey = userKey;
        }

        public boolean isAppFirstLaunch() {
            return isAppFirstLaunch;
        }

        public void setAppFirstLaunch(boolean isAppFirstLaunch) {
            this.isAppFirstLaunch = isAppFirstLaunch;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }
}
