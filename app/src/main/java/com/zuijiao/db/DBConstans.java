package com.zuijiao.db;

/**
 * Created by xiaqibo on 2015/4/1.
 */
public interface DBConstans {

    public static final String DB_NAME= "zuijiao_db" ;
    //tables
    public static final String TABLE_GOURMET = "gourmet";
    public static final String TABLE_USER = "users";
    public static final String TABLE_GOURMET_IMAGE = "gourmet_image";
    //gourmet table columns
    public static final String COLUMN_GOURMET_NAME = "food_name";
    public static final String COLUMN_GOURMET_ID = "food_identifier";
    public static final String COLUMN_GOURMET_TAG = "food_tag";
    public static final String COLUMN_GOURMET_USER_ID = "user_id";
    public static final String COLUMN_GOURMET_ADDRESS = "food_address";
    public static final String COLUMN_GOURMET_PRICE = "food_price";
    public static final String COLUMN_GOURMET_ISMARKED = "is_marked";
    public static final String COLUMN_GOURMET_CREATE_TIME = "create_at";
    public static final String COLUMN_GOURMET_ISPRIVATE = "is_private";
    //user table columns
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_HEAD_LOCAL = "user_head_local";
    public static final String COLUMN_USER_HEAD_SERVICE = "user_head_service";
    public static final String COLUMN_USER_ID = "user_id";
    //gourmet image table columns
    public static final String COLUMN_GOURMET_IMAGE_LOCAL_PATH = "image_local_path";
    public static final String COLUMN_GOURMET_IMAGE_SERVER_PATH = "image_service_path";
   // public static final String COLUMN_GOURMET_ID = "food_identifier";


}
