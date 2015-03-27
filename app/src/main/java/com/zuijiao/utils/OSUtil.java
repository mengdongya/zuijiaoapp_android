package com.zuijiao.utils;

public class OSUtil {
	private static final String LOGTAG = "OSUtil";
	/**
	 * @return current OS API level
	 */
	public static int getAPILevel() {
		int version = android.os.Build.VERSION.SDK_INT;
		return version;
	}
}
