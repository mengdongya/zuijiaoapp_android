package com.zuijiao.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaqibo on 2015/3/31.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "zuijiao_db" ;
    private final static String GOURMET_TABLE = "gourmet" ;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
