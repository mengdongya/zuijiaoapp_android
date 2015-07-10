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
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.entity.SimpleImage;

import net.zuijiao.android.zuijiao.GourmetDisplayFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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


//    public boolean load = true;
//    private Thread loadImageThread;
//    private LinkedList<SimpleImage> images = new LinkedList<>();
//    private LinkedList<String> ids = new LinkedList<>();
//    HashMap<String, Bitmap> cachedBitmap = null;
//    private static final int MAX_CACHE_BITMAP_COUNT = 200;
//    private Handler imageHandler;

//    public void getImageBitmap(Handler handler) {
//        load = true;
//        imageHandler = handler;
//        int maxMemory = (int) Runtime.getRuntime().maxMemory();
//        int mCacheSize = maxMemory / 4;
//        cachedBitmap = new HashMap<>();
//        loadImageThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean firstInit = true;
//                while (load) {
//                    while (images.size() != 0) {
//                        String imageId = images.get(0).id;
//                        Bitmap bmp = cachedBitmap.get(imageId);
//                        if (bmp == null || bmp.isRecycled()) {
//                            cachedBitmap.remove(imageId);
//                            ids.remove(imageId);
//                            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                                    mContext.getContentResolver(),
//                                    Integer.parseInt(imageId),
//                                    MediaStore.Images.Thumbnails.MINI_KIND, null);
//                            if (bitmap != null) {
////                                synchronized (cachedBitmap) {
//                                while (ids.size() > MAX_CACHE_BITMAP_COUNT) {
//                                    String strId = ids.get(0);
//                                    Bitmap bm = cachedBitmap.get(strId);
////                                    cachedBitmap.remove(bm);
//                                    bm.recycle();
//                                    bm = null;
//                                    cachedBitmap.remove(strId);
//                                    ids.remove(0);
//                                }
//                                cachedBitmap.put(imageId, bitmap);
//                                ids.add(imageId);
////                                }
//                            }
//                        }
//                        synchronized (images) {
//                            images.removeFirst();
//                        }
//                        if (!load)
//                            break;
//                    }
//                    if (firstInit) {
//                        Message message = handler.obtainMessage();
//                        handler.sendMessage(message);
//                        firstInit = false;
//                    }
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        loadImageThread.start();
//    }
//
//    public void addToLoadList(List<SimpleImage> images) {
////        synchronized (this.images){
//        for (SimpleImage image : images) {
//            if (!ids.contains(image.id)) {
//                this.images.add(image);
//            }
////            }
//        }
//    }
//
//    public void addToLoadList(SimpleImage image) {
//        images.add(image);
//    }
//
//    public void stopLoadImage(List<SimpleImage> imagePaths) {
//        load = false;
//        for (SimpleImage image : imagePaths) {
//            Bitmap bmp = cachedBitmap.get(image.id);
//            if (bmp != null) {
//                bmp.recycle();
//            }
//            cachedBitmap.remove(image.id);
//        }
//        images.clear();
//        ids.clear();
//    }
//
//    public Bitmap getBitmapFromMemCache(String key) {
//        return cachedBitmap.get(key);
//    }

    public static List<SimpleImage> getImageList(Context context) {
        Date beginDate = new Date();
        System.out.println("getimagebegin :" + beginDate.getTime());
        List<SimpleImage> list = new ArrayList<SimpleImage>();
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, MediaStore.Images.Media.DATE_ADDED);
        SimpleImage image = null;
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            String displayname = cursor.getString(0);
            String data = cursor.getString(2);
            long size = cursor.getLong(3);
            if (size < 10000) {
                continue;
            }
            image = new SimpleImage();
            image.name = displayname;
            image.id = id;
            image.data = data;
            list.add(0, image);
        }
        cursor.close();
        System.out.println("getimageend :" + new Date().getTime() + "duration == " + (new Date().getTime() - beginDate.getTime()));
        return list;
    }

}
