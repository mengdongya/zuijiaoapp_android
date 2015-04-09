package com.zuijiao.thirdopensdk;

import com.zuijiao.entity.AuthorInfo;

public abstract class AbsSDK {
	public static final String QQ = "qq" ;
    public static final String WEIBO ="weibo" ;
    public static final String WECHAT ="wechat" ;
	public abstract void Login(final LoginListener mListener ) ;
	
	public abstract void logout() ;
	
	public abstract void getUserInfo() ;
	
	public abstract boolean isLogin(String token) ;

    protected LoginListener mListener ;

    public interface LoginListener{
        public void onLoginFinish(AuthorInfo userInfo) ;
        public void onLoginFailed() ;
    }
}
