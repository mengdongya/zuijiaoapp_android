package com.zuijiao.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zuijiao.db.DBConstans;

import java.io.File;

/**
 * Created by xiaqibo on 2015/8/7.
 */
public class CacheUtils {

    private static final String BANQUET_FILE_NAME= "banquet_data" ;
    private static final String BANQUET_KEY_NAME = "banquets" ;
    private static final String ATTENDEE_FILE_NAME = "attendee_data" ;
    private static SQLiteDatabase attendDb ;

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

    public static void saveAttendee(String info ,Integer attendeeId , Context context){
        try{
            if(attendDb == null)
                attendDb = getAttendeeDB(context) ;
            ContentValues values = new ContentValues() ;
            values.put(DBConstans.COLUMN_ATTENDEE_ID , attendeeId);
            values.put(DBConstans.COLUMN_ATTENDEE_INFO , info);
            attendDb.insert(DBConstans.TABLE_ATTENDEE,null ,values) ;
        }catch(Throwable t){
            Log.e("cacheUtil" , "saveAttendee crashed") ;
            t.printStackTrace();
        }
    }

    public static String getAttendee(Integer attendeeId , Context context){
        Cursor cursor = null ;
        try{
            if(attendDb == null) {
                attendDb = getAttendeeDB(context) ;
            }
            cursor = attendDb.query(DBConstans.TABLE_ATTENDEE, null, DBConstans.COLUMN_ATTENDEE_ID + " = ?", new String[]{attendeeId + ""}, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_ATTENDEE_INFO)) ;
            }
        }catch(Throwable t){
            t.printStackTrace();
            Log.e("cacheUtil" , "getAttendee crashed") ;
            return null ;
        }finally {
            try{
                cursor.close();
            }catch (Throwable t){}
        }
        return null ;
    }
    /**
     * clear banquet and attendee data ,called when app is updated ;
     * @param context
     */
    public static void clearData(Context context){
        SharedPreferences banquetsData = context.getSharedPreferences("banquets", 0) ;
        banquetsData.edit().clear().commit() ;
        try{
            if(attendDb == null) {
                attendDb = getAttendeeDB(context) ;
            }
            attendDb.delete(DBConstans.TABLE_ATTENDEE ,null ,null) ;
        }catch(Throwable t){
            t.printStackTrace();
        }
    }

    private static SQLiteDatabase getAttendeeDB(Context context){
        String cacheDir = getCacheDir(context) ;
        String dbPath = cacheDir+File.separator + "attendee.db" ;
        SQLiteDatabase db ;
        File file = new File(dbPath) ;
        db = SQLiteDatabase.openOrCreateDatabase(cacheDir + File.separator + "attendees.db", null) ;
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstans.TABLE_ATTENDEE + " ("
                + DBConstans.COLUMN_ATTENDEE_ID + " INTEGER PRIMARY KEY,"
                + DBConstans.COLUMN_ATTENDEE_INFO + " TEXT);");
        return db ;
    }

    private static String getCacheDir(Context context){
        return context.getCacheDir().getAbsolutePath() ;
    }
}
