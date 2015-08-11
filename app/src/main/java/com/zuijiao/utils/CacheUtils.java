package com.zuijiao.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xiaqibo on 2015/8/7.
 */
public class CacheUtils {

    private static final String BANQUET_FILE_NAME= "banquet_data" ;
    private static final String BANQUET_KEY_NAME = "banquets" ;
    private static final String ATTENDEE_FILE_NAME = "attendee_data" ;


    public static String getBanquets(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                BANQUET_FILE_NAME, 0);
        return pref.getString(BANQUET_KEY_NAME, "");
    }

    public static boolean saveBanquets(String info, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                BANQUET_FILE_NAME, 0);
        return pref.edit().putString(BANQUET_KEY_NAME, info).commit();
    }

    public static boolean saveAttendee(String info ,Integer attendeeId , Context context){
        SharedPreferences pref = context.getSharedPreferences(ATTENDEE_FILE_NAME , 0) ;
        return pref.edit().putString(String.valueOf(attendeeId) , info).commit() ;
    }

    public static String getAttendee(Integer attendeeId , Context context){
        SharedPreferences pref = context.getSharedPreferences( ATTENDEE_FILE_NAME , 0) ;
        return pref.getString(String.valueOf(attendeeId ),"") ;
    }
    /**
     * clear banquet and attendee data ,called when app is updated ;
     * @param context
     */
    public static void clearData(Context context){
        SharedPreferences banquetsData = context.getSharedPreferences("banquets" , 0) ;
        banquetsData.edit().clear().commit() ;
        SharedPreferences attendeesData = context.getSharedPreferences("attendees" , 0) ;
        attendeesData.edit().clear().commit() ;
    }
}
