package com.zuijiao.thirdopensdk;

public abstract class AbsSDK {
	
	public abstract void Login() ;
	
	public abstract void logout() ;
	
	public abstract void getUserInfo() ;
	
	public abstract boolean isLogin(String token) ;
	
}
