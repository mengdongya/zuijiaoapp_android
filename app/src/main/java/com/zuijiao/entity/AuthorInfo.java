package com.zuijiao.entity;

public class AuthorInfo {
    private String mUid = null;
    private String mUserName = null;
    private String mToken = null;
    private String mPlatform = null;
    private String mHeadPath = null;
    private String mEmail = null;
    private String mPassword = null;

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    private int mUserId = -1;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getPlatform() {
        return mPlatform;
    }

    public void setPlatform(String mPlatform) {
        this.mPlatform = mPlatform;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public String getHeadPath() {
        return mHeadPath;
    }

    public void setHeadPath(String mHeadPath) {
        this.mHeadPath = mHeadPath;
    }

}
