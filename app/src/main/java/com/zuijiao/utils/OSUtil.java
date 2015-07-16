package com.zuijiao.utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Date;

/**
 * os util
 */
public class OSUtil {
    private static final String LOGTAG = "OSUtil";


    public static int[] getDeviceDimension(Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        int densityDpi = metric.densityDpi;
        Log.i("OSUtil" ,"width = " + width + " height = " + height + " density = " + density + " densityDpi = " + densityDpi) ;
        return new int[]{width , height} ;
    }
    /**
     * @return current OS API level
     */
    public static int getAPILevel() {
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    @Deprecated
    public String getReadableDate(Date date) {
        Date curDate = new Date();
        long gapMillions = curDate.getTime() - date.getTime();
//        date.getHours()*3600*
//        if()
        return null;
    }

}
