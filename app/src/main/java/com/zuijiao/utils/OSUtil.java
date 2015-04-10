package com.zuijiao.utils;

import java.util.Date;

public class OSUtil {
	private static final String LOGTAG = "OSUtil";
	/**
	 * @return current OS API level
	 */
	public static int getAPILevel() {
		int version = android.os.Build.VERSION.SDK_INT;
		return version;
	}


    public String getReadableDate(Date date){
        Date curDate = new Date() ;
        long gapMillions = curDate.getTime() - date.getTime() ;
//        date.getHours()*3600*
//        if()
        return null ;
    }
}
