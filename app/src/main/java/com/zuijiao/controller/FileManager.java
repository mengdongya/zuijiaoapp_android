package com.zuijiao.controller;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUsers;
import com.zuijiao.entity.SimpleImage;

import net.zuijiao.android.zuijiao.MainFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public final static String APP_FOLDER_NAME = "Zuijiao";
    public final static String THIRD_PARTY_HEAD = "third_party_head.jpg";
    public static final String CHOOSE_IMAGE = Environment.getDataDirectory()
            .getAbsolutePath() + File.separator + "head.jpg";
    private static final int COPY_BLOCK_SIZE = 4096;
    public static String APP_FOLDER_PATH = "";
    //main fragment data
    public static Optional<List<Gourmet>> mainGourmet = Optional.empty();
    //my favor fragment data
    public static Optional<List<Gourmet>> favorGourmets = Optional.empty();
    public static Optional<WouldLikeToEatUsers> tmpWouldLikeList = Optional.empty();
    //
    public static Gourmet tmpMessageGourmet = null;
    private static FileManager mInstance = null;
    private Context mContext = null;
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID,};

    private FileManager(Context context) {
        this.mContext = context;
    }

    public static FileManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FileManager(context);
        }
        return mInstance;
    }

    public static String getThirdPartyUserHeadPath() {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + APP_FOLDER_NAME
                + File.separator + THIRD_PARTY_HEAD;
        return path;
    }

    public static boolean createRootFolder() {
        return createFolder(getAppRootPath());
    }

    public static String getAppRootPath() {
        if (APP_FOLDER_PATH == null || APP_FOLDER_PATH.equals("")) {
            APP_FOLDER_PATH = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + APP_FOLDER_NAME;
        }
        return APP_FOLDER_PATH;
    }

    public static boolean createFolder(String path) {
        if (checkMount()) {
            return false;
        }
        File desFile = new File(path);
        if (desFile.exists() && desFile.isDirectory()) {
            return true;
        }
        return desFile.mkdirs();
        // path.trim() ;
        // String[] folderNames = path.split(File.separator) ;
        // for(int i = 0 ;i < folderNames.length ; i++){
        //
        // }
    }

    //
    public static boolean checkMount() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            return true;
        } else {
            String dataPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            if (dataPath == null || dataPath.equals("")
                    || !(new File(dataPath)).exists()) {
                return true;
            }
        }
        return false;
    }

    public static void saveImageToDisk(InputStream inputStream)
            throws IOException {
        byte[] data = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(getThirdPartyUserHeadPath());
            while ((len = inputStream.read(data)) != -1) {
                fileOutputStream.write(data, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void setGourmets(int type, Optional<List<Gourmet>> gourmets) {
        if (type == MainFragment.MAIN_PAGE) {
//            if(mainGourmet.isPresent()){
//                mainGourmet.get().clear();
//            }
            mainGourmet = gourmets;
        } else {
//            if(favorGourmets.isPresent()){
//                favorGourmets.get().clear();
//            }
            favorGourmets = gourmets;
        }
    }

    public Gourmet getItem(boolean bFavor, int index) {
        if (bFavor) {
            synchronized (favorGourmets) {
                if (!favorGourmets.isPresent()) {
                    return null;
                }
                return favorGourmets.get().get(index);
            }
        } else {
            synchronized (mainGourmet) {
                if (!mainGourmet.isPresent()) {
                    return null;
                }
                return mainGourmet.get().get(index);
            }
        }
    }

    public static List<SimpleImage> getImageList(Context context) {
        List<SimpleImage> list = new ArrayList<SimpleImage>();
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        SimpleImage image = null;
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            String displayname = cursor.getString(0);
            image = new SimpleImage();
            image.name = displayname;
            image.id = id;
            list.add(image);
        }
        cursor.close();
        return list;
    }


}
