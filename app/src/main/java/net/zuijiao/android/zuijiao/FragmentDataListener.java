package net.zuijiao.android.zuijiao;

import java.util.ArrayList;

public interface FragmentDataListener {
	public ArrayList<Object> initCache(int type)  ;
	
	public ArrayList<Object> getContentFromNetWork(String Url) ;
	
	public void NotifyData() ;
}
