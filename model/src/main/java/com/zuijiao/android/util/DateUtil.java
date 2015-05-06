package com.zuijiao.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chen Hao on 5/6/15.
 */
public class DateUtil {

    public static String todayInString() {
        return dateInString(new Date());
    }

    public static String dateInString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return formatter.format(date);
    }

}
