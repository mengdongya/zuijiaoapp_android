package com.zuijiao.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.utils.StrUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/3/31.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "zuijiao_db";
    private final static String GOURMET_TABLE = "gourmet";
    private static SQLiteDatabase db = null;

    private static DBOpenHelper mInstance = null;
    private static int dbVersion = 1;

    private static final String TAG = "DBOpenHelper";

    public static DBOpenHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBOpenHelper(context);
            db = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    private DBOpenHelper(Context context) {
        super(context, DB_NAME, null, dbVersion);
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE  " + DBConstans.TABLE_GOURMET + " ("
                    + DBConstans.COLUMN_GOURMET_NAME + " TEXT,"
                    + DBConstans.COLUMN_GOURMET_ID + " INTEGER PRIMARY KEY,"
                    + DBConstans.COLUMN_GOURMET_ADDRESS + " TEXT,"
                    + DBConstans.COLUMN_GOURMET_CREATE_TIME + " LONG,"
                    + DBConstans.COLUMN_GOURMET_ISMARKED + " INTEGER,"
                    + DBConstans.COLUMN_GOURMET_ISPRIVATE + " INTEGER,"
                    + DBConstans.COLUMN_GOURMET_PRICE + " REAL,"
                    + DBConstans.COLUMN_GOURMET_TAG + " TEXT,"
                    + DBConstans.COLUMN_GOURMET_USER_ID + " INTEGER);");
            db.execSQL("CREATE TABLE " + DBConstans.TABLE_GOURMET_IMAGE + " ("
                    + DBConstans.COLUMN_USER_ID + " INTEGER,"
                    + DBConstans.COLUMN_GOURMET_IMAGE_LOCAL_PATH + " TEXT,"
                    + DBConstans.COLUMN_GOURMET_IMAGE_SERVER_PATH + " TEXT);");
            db.execSQL("CREATE TABLE " + DBConstans.TABLE_USER + " ("
                    + DBConstans.COLUMN_USER_ID + " INTEGER,"
                    + DBConstans.COLUMN_USER_NAME + " TEXT,"
                    + DBConstans.COLUMN_USER_HEAD_LOCAL + " TEXT,"
                    + DBConstans.COLUMN_USER_HEAD_SERVICE + " TEXT);");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDatabase() throws Throwable {
        try {
            if (db != null) {
                db.close();
                db = null;
            }
            if (mInstance != null) {
                mInstance.close();
                mInstance = null;
            }
        } catch (Throwable e) {
            Log.e(TAG, "Failed to close the DB.", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            closeDatabase();
        } catch (Throwable e) {
            Log.e(TAG, "Failed to close the DB.", e);
            e.printStackTrace();
        }
        super.finalize();
    }


    public boolean insertGourmet(Gourmet gourmet) {
        ContentValues values = new ContentValues();
        values.put(DBConstans.COLUMN_GOURMET_NAME, gourmet.getName());
        values.put(DBConstans.COLUMN_GOURMET_ADDRESS, gourmet.getAddress());
        values.put(DBConstans.COLUMN_GOURMET_CREATE_TIME, gourmet.getDate().getTime());
        values.put(DBConstans.COLUMN_GOURMET_ID, gourmet.getIdentifier());
        values.put(DBConstans.COLUMN_GOURMET_ISMARKED, gourmet.getWasMarked());
        values.put(DBConstans.COLUMN_GOURMET_ISPRIVATE, gourmet.getIsPrivate());
        values.put(DBConstans.COLUMN_GOURMET_PRICE, gourmet.getPrice());
        values.put(DBConstans.COLUMN_GOURMET_TAG, StrUtil.buildTags(gourmet.getTags()));
        values.put(DBConstans.COLUMN_GOURMET_USER_ID, gourmet.getUser().getIdentifier());
        synchronized (db) {
            try {
                db.insert(DBConstans.TABLE_GOURMET, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public boolean insertGourmets(Gourmets gourmets) {
        for (Gourmet gourmet : gourmets.getGourmets()) {
            if (!insertGourmet(gourmet)) {
                return false;
            } else {
                insertUserInfo(gourmet.getUser());
                continue;
            }
        }
        return true;
    }


    public boolean initGourmet(String identify) {
        try {

            synchronized (db) {
                Cursor cursor = db.query(DBConstans.TABLE_GOURMET, null, DBConstans.COLUMN_GOURMET_ID + "=?", new String[]{identify}, null, null, null);
                if (cursor == null || cursor.getCount() <= 0) {
                    return false;
                }
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Gourmet gourmet = new Gourmet();
                    String id = identify;
                    String name = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_NAME));
                    String address = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ADDRESS));
                    long createTime = cursor.getLong(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_CREATE_TIME));
                    boolean isMarked = cursor.getInt(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ISMARKED)) == 1;
                    boolean isPrivate = cursor.getInt(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ISPRIVATE)) == 1;
                    String price = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_PRICE));
                    List<String> tags = StrUtil.retriveTags(cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_TAG)));
                    String userId = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_USER_ID));


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Gourmet> initGourmets() {
        try {

            synchronized (db) {
                Cursor cursor = db.query(DBConstans.TABLE_GOURMET, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    List<Gourmet> tmpGourmets = new ArrayList<Gourmet>();
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        Gourmet gourmet = new Gourmet();
                        int identify = cursor.getInt(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ID));
                        gourmet.setIdentifier(identify);
                        String name = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_NAME));
                        gourmet.setName(name);
                        String address = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ADDRESS));
                        gourmet.setAddress(address);
                        long createTime = cursor.getLong(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_CREATE_TIME));
                        gourmet.setDate(new Date(createTime));
                        boolean isMarked = cursor.getInt(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ISMARKED)) == 1;
                        gourmet.setWasMarked(isMarked);
                        boolean isPrivate = cursor.getInt(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_ISPRIVATE)) == 1;
                        gourmet.setIsPrivate(isPrivate);
                        String price = cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_PRICE));
                        gourmet.setPrice(price);
                        List<String> tags = StrUtil.retriveTags(cursor.getString(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_TAG)));
                        gourmet.setTags(tags);
                        int userId = cursor.getInt(cursor.getColumnIndex(DBConstans.COLUMN_GOURMET_USER_ID));
                        gourmet.setUser(getUserInfo(userId));
                        gourmet.setImageURLs(getImageUrls(identify));
                        tmpGourmets.add(gourmet);
                        cursor.moveToNext();
                    }
                    return tmpGourmets;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private List<String> getImageUrls(int gourmetId) {
        //todo
        return new ArrayList<String>();
    }


    public TinyUser getUserInfo(int userId) {
        try {
            Cursor c = db.query(DBConstans.TABLE_USER, null, DBConstans.COLUMN_USER_ID + "=?", new String[]{userId + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    TinyUser user = new TinyUser();
                    user.setIdentifier(c.getInt(c.getColumnIndex(DBConstans.COLUMN_USER_ID)));
                    user.setNickName(c.getString(c.getColumnIndex(DBConstans.COLUMN_USER_NAME)));
                    user.setAvatarURL(c.getString(c.getColumnIndex(DBConstans.COLUMN_USER_HEAD_SERVICE)));
                    c.moveToNext();
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertUserInfo(TinyUser user) {
        try {
            synchronized (db) {
                Cursor cursor = db.query(DBConstans.TABLE_USER, null, DBConstans.COLUMN_GOURMET_USER_ID + " = ?", new String[]{user.getIdentifier() + ""}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    //TODO Modify user info ;
                    return true;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(DBConstans.COLUMN_USER_ID, user.getIdentifier());
                    values.put(DBConstans.COLUMN_USER_HEAD_SERVICE, user.getAvatarURL().toString());
                    values.put(DBConstans.COLUMN_USER_NAME, user.getNickName());
                    db.insert(DBConstans.TABLE_USER, null, values);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

}
