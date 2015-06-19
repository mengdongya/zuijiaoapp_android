package com.zuijiao.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.entity.SimpleLocation;
import com.zuijiao.utils.StrUtil;

import net.zuijiao.android.zuijiao.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static Context mContext;
    private static final String TAG = "DBOpenHelper";

    public static DBOpenHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBOpenHelper(context);
            db = mInstance.getWritableDatabase();
        }
        mContext = context;
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
                    + DBConstans.COLUMN_GOURMET_ID + " INTEGER,"
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

    static String DB_PATH = null;

    public static boolean copyLocationDb(Context context) {
        DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator + context.getPackageName() + File.separator + "location.db";
        try {
            if (!(new File(DB_PATH).exists())) {
                InputStream is = context.getResources().openRawResource(
                        R.raw.location);
                FileOutputStream fos = new FileOutputStream(DB_PATH);
                byte[] buffer = new byte[400000];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private SQLiteDatabase getLocationDb() {
        DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator + mContext.getPackageName() + File.separator + "location.db";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH,
                null);
        return db;
    }

    public ArrayList<SimpleLocation> getProvinceList() {
        SQLiteDatabase db = getLocationDb();
        ArrayList<SimpleLocation> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("location", null, "type = ?", new String[]{1 + ""}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SimpleLocation location = new SimpleLocation();
                    location.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    location.setName(cursor.getString(cursor.getColumnIndex("name")));
                    location.setP_id(cursor.getInt(cursor.getColumnIndex("p_id")));
                    location.setType(cursor.getInt(cursor.getColumnIndex("type")));
                    result.add(location);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            updateLocationDb();
            result = getProvinceList();
        } finally {
            try {
                cursor.close();
                db.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return result;
    }

    public ArrayList<SimpleLocation> getCitiesByProvinceId(int id) {
        SQLiteDatabase db = getLocationDb();
        ArrayList<SimpleLocation> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("location", null, "p_id = ?", new String[]{id + ""}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SimpleLocation location = new SimpleLocation();
                    location.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    location.setName(cursor.getString(cursor.getColumnIndex("name")));
                    location.setP_id(cursor.getInt(cursor.getColumnIndex("p_id")));
                    location.setType(cursor.getInt(cursor.getColumnIndex("type")));
                    result.add(location);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            updateLocationDb();
            result = getProvinceList();
        } finally {
            try {
                cursor.close();
                db.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return result;
    }

    public void updateLocationDb() {
        DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator + mContext.getPackageName() + File.separator + "location.db";
        try {
            new File(DB_PATH).delete();
        } catch (Throwable t) {

        }
        copyLocationDb(mContext);
    }

    public String getLocationByIds(int provinceId, int cityId) {
        SQLiteDatabase db = getLocationDb();
        String province = "";
        String city = "";
        synchronized (db) {
            try {
                Cursor cursor = db.query("location", null, "id = ? and type =?", new String[]{provinceId + "", 1 + ""}, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        province = cursor.getString(cursor.getColumnIndex("name"));
                        break;
                    }
                    cursor.close();
                    cursor = null;
                }
                cursor = db.query("location", null, "id = ? and (type =? or ?) ", new String[]{cityId + "", 2 + "", 3 + ""}, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        city = cursor.getString(cursor.getColumnIndex("name"));
                        break;
                    }
                    cursor.close();
                }
            } catch (Throwable t) {
                t.printStackTrace();
                return "";
            }
        }
        if (province == null) {
            province = "";
        }
        if (city == null) {
            city = "";
        }
        if (province.equals(city)) {
            return city;
        }
        db.close();
        return province + city;
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
                Cursor cursor = db.query(DBConstans.TABLE_GOURMET, null, DBConstans.COLUMN_GOURMET_ID + " = ?", new String[]{gourmet.getIdentifier() + ""}, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.close();
                    db.update(DBConstans.TABLE_GOURMET, values, DBConstans.COLUMN_GOURMET_ID + " = ?", new String[]{gourmet.getIdentifier() + ""});
                } else {
                    db.insert(DBConstans.TABLE_GOURMET, null, values);
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    public boolean insertGourmets(Gourmets gourmets) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.delete(DBConstans.TABLE_GOURMET, null, null);
                synchronized (gourmets) {
                    for (Gourmet gourmet : gourmets.getGourmets()) {
                        if (!insertGourmet(gourmet)) {
                            break;
                        } else {
                            insertUserInfo(gourmet.getUser());
                            insertImageUrl(gourmet.getIdentifier(), gourmet.getImageURLs());
                            continue;
                        }
                    }
                }
            }
        }).start();
        return true;
    }

    private void insertImageUrl(int id, List<String> urls) {
        try {
            synchronized (db) {
                db.delete(DBConstans.TABLE_GOURMET_IMAGE, DBConstans.COLUMN_GOURMET_ID + "= ?", new String[]{id + ""});
                for (String url : urls) {
                    ContentValues values = new ContentValues();
                    values.put(DBConstans.COLUMN_GOURMET_ID, id);
                    values.put(DBConstans.COLUMN_GOURMET_IMAGE_SERVER_PATH, url);
                    db.insert(DBConstans.TABLE_GOURMET_IMAGE, null, values);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
                    Collections.sort(tmpGourmets, new Comparator<Gourmet>() {
                        @Override
                        public int compare(Gourmet g1, Gourmet g2) {
                            return (int) (g2.getDate().getTime() - g1.getDate().getTime());
                        }
                    });
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
        List<String> imageUrl = new ArrayList<String>();
        Cursor c = db.query(DBConstans.TABLE_GOURMET_IMAGE, new String[]{DBConstans.COLUMN_GOURMET_IMAGE_SERVER_PATH}, DBConstans.COLUMN_GOURMET_ID + "= ?", new String[]{gourmetId + ""}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String url = c.getString(c.getColumnIndex(DBConstans.COLUMN_GOURMET_IMAGE_SERVER_PATH));
                if (url != null) {
                    imageUrl.add(url);
                }
                c.moveToNext();
            }
        }
        return imageUrl;
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
                    String avatar = "";
                    if (user.getAvatarURL().isPresent()) {
                        avatar = user.getAvatarURL().toString();
                        values.put(DBConstans.COLUMN_USER_HEAD_SERVICE, user.getAvatarURL().toString());
                    }
                    values.put(DBConstans.COLUMN_USER_NAME, user.getNickName());
                    db.insert(DBConstans.TABLE_USER, null, values);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }


    public int getLocationIdByName(String name) {
        String subStr = name.substring(0, 2);
        SQLiteDatabase db = getLocationDb();
        int id = 0;
        Cursor cursor = null;
        synchronized (db) {
            try {
                cursor = db.query("location", new String[]{"id"}, "name LIKE ?", new String[]{name.substring(0, 2) + "%"}, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        id = cursor.getInt(0);
                        return id;
                    }
                    cursor.close();
                    cursor = null;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    cursor.close();
                    db.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        return 0;
    }
}
