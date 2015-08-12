package com.zuijiao.db;

/**
 * Created by xiaqibo on 2015/4/1.
 */
public interface DBConstans {

    String DB_NAME = "zuijiao_db";
    //tables
    String TABLE_GOURMET = "gourmet";
    String TABLE_USER = "users";
    String TABLE_GOURMET_IMAGE = "gourmet_image";
    //gourmet table columns
    String COLUMN_GOURMET_NAME = "food_name";
    String COLUMN_GOURMET_ID = "food_identifier";
    String COLUMN_GOURMET_TAG = "food_tag";
    String COLUMN_GOURMET_USER_ID = "user_id";
    String COLUMN_GOURMET_ADDRESS = "food_address";
    String COLUMN_GOURMET_PRICE = "food_price";
    String COLUMN_GOURMET_ISMARKED = "is_marked";
    String COLUMN_GOURMET_CREATE_TIME = "create_at";
    String COLUMN_GOURMET_ISPRIVATE = "is_private";
    //user table columns
    String COLUMN_USER_NAME = "user_name";
    String COLUMN_USER_HEAD_LOCAL = "user_head_local";
    String COLUMN_USER_HEAD_SERVICE = "user_head_service";
    String COLUMN_USER_ID = "user_id";
    //gourmet image table columns
    String COLUMN_GOURMET_IMAGE_LOCAL_PATH = "image_local_path";
    String COLUMN_GOURMET_IMAGE_SERVER_PATH = "image_service_path";
    // public static final String COLUMN_GOURMET_ID = "food_identifier";
    //attendee db
    String TABLE_ATTENDEE = "attendee" ;
    String COLUMN_ATTENDEE_ID  = "attendee_id";
    String COLUMN_ATTENDEE_INFO = "attendee_info";


}
