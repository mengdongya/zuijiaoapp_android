package com.zuijiao.controller;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {
	private static final String LOGTAG = "PreferenceManager";
	private static PreferenceInfo mPreferInfo = null;
	private static PreferenceManager mPreferenceMng = null;
	private static Context mContext = null;

	private PreferenceManager() {

	}

	public static PreferenceManager getInstance(Context context) {
		if (mPreferenceMng == null) {
			mContext = context;
			mPreferenceMng = new PreferenceManager();
		}
		return mPreferenceMng;

	}

	/**
	 * PreferenceInfo constructor , one instance
	 * 
	 * @return
	 */

	/**
	 * init from shared_preference
	 * 
	 * @return
	 */
	public PreferenceInfo initPreferenceInfo() {
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

	/**
	 * setting items
	 * 
	 * @author Bobo
	 * 
	 */
	public class PreferenceInfo {
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

	/**
	 * Deprecated
	 * 
	 * @param mPreferInfo
	 */
	public void setPreferInfo(PreferenceInfo mPreferInfo) {
		PreferenceManager.mPreferInfo = mPreferInfo;
	}

	public PreferenceInfo getPreferInfo() {
		return mPreferInfo;
	}

	public interface PreferencesDef {
		public static final String FILE_NAME = "settings";
		public static final String IS_APP_FIRST_LAUNCH = "boolean_first_launch";
		public static final String USER_KEY = "str_user_key";
		public static final String USER_ID = "str_user_id";
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
}
