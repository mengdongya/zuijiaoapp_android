package com.zuijiao.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.entity.SimpleImage;

import net.zuijiao.android.zuijiao.GourmetDisplayFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * manager some list and file in sd-card
 */
public class FileManager {
    public final static String APP_FOLDER_NAME = "Zuijiao";
    public final static String THIRD_PARTY_HEAD = "third_party_head.jpg";
    public final static String COMPRESS_FOLDER = "compressedImage" + File.separator;
    public static final String CHOOSE_IMAGE = Environment.getDataDirectory()
            .getAbsolutePath() + File.separator + "head.jpg";
    private static final int COPY_BLOCK_SIZE = 4096;
    public static String APP_FOLDER_PATH = "";
    //main fragment data
    public static Optional<List<Gourmet>> mainGourmet = Optional.empty();
    //my favor fragment data
    public static Optional<List<Gourmet>> favorGourmets = Optional.empty();
    public static Optional<List<Gourmet>> recommendList = Optional.empty();
    public static Gourmet tmpMessageGourmet = null;

    public User getFullUser() {
        return mFullUser;
    }

    public void setFullUser(User mFullUser) {
        this.mFullUser = mFullUser;
    }

    //current login user ; for edit user info
    private User mFullUser = null;

    private static FileManager mInstance = null;
    private Context mContext = null;
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.SIZE};

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
        if (type == GourmetDisplayFragment.MAIN_PAGE) {
            mainGourmet = gourmets;
        } else if (type == GourmetDisplayFragment.FAVOR_PAGE) {
            favorGourmets = gourmets;
        } else if (type == GourmetDisplayFragment.RECOMMEND_PAGE) {
            recommendList = gourmets;
        }
    }

    public Gourmet getItem1(boolean bFavor, int index) {
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

    public Bitmap getImageBmpById(String id, ContentResolver resolver) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    resolver,
                    Integer.parseInt(id),
                    MediaStore.Images.Thumbnails.MINI_KIND, options);
            options.outWidth = 200;
            options.inJustDecodeBounds = false;
            int width = 200;
            int height = 200;
            final int minSideLength = Math.min(width, height);
            options.inSampleSize = computeSampleSize(options, minSideLength,
                    width * height);
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    resolver,
                    Integer.parseInt(id),
                    MediaStore.Images.Thumbnails.MINI_KIND, options);
            Log.i("multiImageChoose", "bitmapSize= " + bitmap.getByteCount());
            return bitmap;
        } catch (Throwable t) {
            t.printStackTrace();
            Log.i("FilManager", "getImageBmpById crushed");
            return null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    public static List<SimpleImage> getImageList(Context context) {
        Date beginDate = new Date();
       //System.out.println("getimagebegin :" + beginDate.getTime());
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, MediaStore.Images.Media.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<SimpleImage>();
        List<SimpleImage> list = new ArrayList<SimpleImage>();
        SimpleImage image = null;
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            String displayname = cursor.getString(0);
            String data = cursor.getString(2);
            long size = cursor.getLong(3);
            if (size < 1024 * 10) {
                continue;
            }
            image = new SimpleImage();
            image.name = displayname;
            image.id = id;
            image.data = data;
            list.add(0,image);
        }
        cursor.close();
        System.out.println("getimageend :" + new Date().getTime() + "duration == " + (new Date().getTime() - beginDate.getTime()));
        return list;
    }


    /**
     * order banquet list ,
     * order-able items appears in front as positive time sequence ,
     * time out items display later and order by negative time sequence .
     * @param banquetList
     */
    public void orderBanquetList(ArrayList<Banquent> banquetList){
        if(banquetList == null)
            return ;
        if(banquetList.size() <2)
            return;
        Collections.sort(banquetList, new Comparator<Banquent>() {
            @Override
            public int compare(Banquent banquet1, Banquent banquet2) {
                BanquentStatus status1 = BanquentStatus.fromString(banquet1.getStatus()) ;
                BanquentStatus status2 =  BanquentStatus.fromString(banquet2.getStatus()) ;
                if(status1.equals(BanquentStatus.Selling) || status2.equals(BanquentStatus.Selling)){
                    int result = compareStatus(status1 , status2) ;
                    if(result == 0){
                        return compareDate(banquet1.getTime() , banquet2.getTime()) ;
                    }else {
                        return result ;
                    }
                }else{
                    return compareDate(banquet2.getTime() ,banquet1.getTime()) ;
                }
            }

            private int compareStatus(BanquentStatus status1, BanquentStatus status2){
                return  status1.compareTo(status2) ;
            }

            private int compareDate(Date date1 , Date date2){
                return  date1.compareTo(date2) ;
            }
        });
    }
}
