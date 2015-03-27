package com.zuijiao.thirdopensdk;

import android.os.Handler;

import com.zuijiao.entity.ThirdPartyUserInfo;
import com.zuijiao.entity.ZuiJiaoUser;

public abstract class AbsSDK {
	
	public abstract void Login() ;
	
	public abstract void logout() ;
	
	public abstract void getUserInfo() ;
	
	public abstract boolean isLogin(String token) ;
	
}
