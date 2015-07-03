package net.zuijiao.android.zuijiao;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.controller.FileManager;
import com.zuijiao.entity.SimpleImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaqibo on 2015/4/13.
 */
@ContentView(R.layout.activity_image_chooser)
public class ImageChooseActivity extends BaseActivity {
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int PHOTO_ZOOM = 1001;
    public static final int PHOTO_RESULT = 2001;
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID,};
    private static final String FILE_NAME = "head.jpg";
    @ViewInject(R.id.image_chooser_container)
    private GridView mGdView = null;
    @ViewInject(R.id.image_chooser_toolbar)
    private Toolbar mToolbar;
    private List<SimpleImage> images = null;
    private List<SimpleImage> brokenImages = null;
    private HashMap<String, Bitmap> mCachedData = new HashMap<>();
    private ArrayList<String> mCachedId = new ArrayList<String>();
    private ProgressDialog mDialog = null;
    private ContentResolver mContentResolver = null;
    private final int maxSize = 40;

    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.image_chooser));
        mContentResolver = mContext.getContentResolver();
        images = FileManager.getImageList(mContext);
        mGdView.setOnItemClickListener(mListener);
        new Thread(new Runnable() {
            @Override
            public void run() {

                initCache();
            }
        }).start();
    }

    private ExecutorService mImageThreadPool = null;

    public ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    mImageThreadPool = Executors.newFixedThreadPool(1);
                }
            }
        }
        return mImageThreadPool;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_ZOOM) {
                photoZoom(data.getData());
            } else if (requestCode == PHOTO_RESULT) {
                Bundle extras = data.getExtras();
                if (extras != null) {
//                    createDialog();
                    Bitmap photo = extras.getParcelable("data");
                    File file = new File(getCacheDir().getPath() + File.separator + "head.jpg");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (Exception e) {
                            Toast.makeText(mContext, getString(R.string.error_read_file), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            finalizeDialog();
                            setResult(RESULT_CANCELED);
                            finish();
                            return;
                        }
                    }
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, getString(R.string.error_read_file), Toast.LENGTH_LONG).show();
                        finalizeDialog();
                        setResult(RESULT_CANCELED);
                        finish();
                        return;
                    }
                    if (photo.compress(Bitmap.CompressFormat.JPEG, 75, os)) {
                        try {
                            os.flush();
                            os.close();
                            os = null;
                            photo.recycle();
                        } catch (Exception e) {
                            Toast.makeText(mContext, getString(R.string.error_read_file), Toast.LENGTH_LONG).show();
                            finalizeDialog();
                            e.printStackTrace();
                            finish();
                            return;
                        }
                        setResult(RESULT_OK);
                    } else {
                        finalizeDialog();
                        setResult(RESULT_CANCELED);
                    }
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleBitmap();
    }

    private void getImages(List<SimpleImage> list) {
        for (SimpleImage image : list) {
            if (mCachedId.contains(image.id))
                continue;
            Bitmap bmp = mFileMng.getImageBmpById(image.id, mContentResolver);
//            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                    mContentResolver,
//                    Integer.parseInt(image.id),
//                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (bmp != null && !bmp.isRecycled()) {
                addToCache(bmp, image.id);
            }
        }
    }

    private void initCache() {
        if (images == null || images.size() == 0) {
            return;
        }
        for (SimpleImage image : images) {
            if (mCachedData.size() >= 2) {
                break;
            }
            try {
                Bitmap bitmap = mFileMng.getImageBmpById(image.id, mContentResolver);
//                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                        mContext.getContentResolver(),
//                        Integer.parseInt(image.id),
//                        MediaStore.Images.Thumbnails.MINI_KIND, null);
                if (bitmap != null) {
                    synchronized (mCachedData) {
                        mCachedData.put(image.id, bitmap);
                    }
                    synchronized (mCachedId) {
                        mCachedId.add(image.id);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        Message msg = mHandler.obtainMessage();
        msg.what = 0x10001;
        mHandler.sendMessage(msg);
    }

    private static final int CACHE_SIZE = 50;


    private void addToCache(Bitmap bitmap, String id) {
        if (mCachedData == null) {
            mCachedData = new HashMap<String, Bitmap>();
        }
        if (mCachedId == null) {
            mCachedId = new ArrayList<String>();
        }
        int i = 0;
        if (mCachedData.size() > maxSize) {
            while (mCachedData.size() > maxSize) {
                String strId = mCachedId.get(0);
                Bitmap bm = mCachedData.get(strId);
                if (bm != null && !bm.isRecycled()) {
                    bm.recycle();
                }
                bm = null;
                mCachedData.remove(strId);
                mCachedId.remove(0);
            }
            System.gc();
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            mCachedId.add(id);
            mCachedData.put(id, bitmap);
        }
    }
//    private void addToCache(Bitmap bitmap, String id) {
//        if (mCachedData == null) {
//            mCachedData = new HashMap<String, Bitmap>();
//        }
//        if (mCachedId == null) {
//            mCachedId = new ArrayList<String>();
//        }
//        int i = 0;
//        if (mCachedId.size() >= CACHE_SIZE) {
//
//            while (mCachedId.size() >= CACHE_SIZE) {
//                String strId = mCachedId.get(0);
//                Bitmap bm = mCachedData.get(strId);
//                if (bm != null && !bm.isRecycled()) {
//                    bm.recycle();
//                }
//                bm = null;
//                mCachedData.remove(strId);
//                mCachedId.remove(0);
//            }
//            System.gc();
//        }
//        mCachedId.add(id);
//        mCachedData.put(id, bitmap);
//    }

    private Bitmap getFromCache(String id) {
        if (mCachedData == null) {
            return null;
        }
        synchronized (mCachedData) {
            try {
                Bitmap bitmap = mCachedData.get(id);
                return bitmap;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return null;
    }

    private void recycleBitmap() {
        for (String id : mCachedId) {
            try {
                Bitmap bmp = mCachedData.get(id);
                if (bmp != null && !bmp.isRecycled())
                    bmp.recycle();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        System.gc();
    }

    public void photoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESULT);
    }

    class ViewHolder {
        ImageView image;
        TextView text;
    }

    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            Uri uri_temp = Uri.parse("content://media/external/images/media/"
                    + images.get(position).id);
            photoZoom(uri_temp);
        }
    };

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.head_chooser, null);
                convertView.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 2, parent.getWidth() / 2));
//                holder.image = (ImageView) convertView;
                holder.image = (ImageView) convertView.findViewById(R.id.choose_user_head_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SimpleImage image = images.get(position);
            holder.image.setTag(Integer.parseInt(image.id));
            Bitmap bitmap = getFromCache(image.id);
            if (bitmap == null || bitmap.isRecycled()) {
                holder.image.setImageResource(R.drawable.empty_view_greeting);
                getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = mFileMng.getImageBmpById(image.id, mContentResolver);
//                        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                                mContext.getContentResolver(),
//                                Integer.parseInt(image.id),
//                                MediaStore.Images.Thumbnails.MINI_KIND, null);
                        addToCache(bitmap, image.id);
                        Message msg = mHandler.obtainMessage();
                        msg.what = Integer.parseInt(image.id);
                        mHandler.sendMessage(msg);
                    }
                });
            } else {
                holder.image.setImageBitmap(bitmap);
            }
//            Bitmap bitmap = null;
//            try {
//                if ((bitmap = getFromCache(images.get(position).id)) == null
//                        || bitmap.getByteCount() <= 0 || bitmap.isRecycled()) {
//                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                            mContext.getContentResolver(),
//                            Integer.parseInt(images.get(position).id),
//                            MediaStore.Images.Thumbnails.MINI_KIND, null);
//
//                }
//                if (bitmap != null) {
////                    addToCache(bitmap, images.get(position).id);
//                    holder.image.setImageBitmap(bitmap);
//                } else {
//                    holder.image.setImageResource(R.drawable.empty_view_greeting);
//                    Log.e("imagechooseAcitivity", "bitmap == null !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                }
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
            return convertView;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    };
    ;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x10001) {
                mGdView.setAdapter(mAdapter);
            } else {
                try {
                    int imageId = msg.what;
                    View imageView = mGdView.findViewWithTag(imageId);
                    if (imageView != null) {
                        Bitmap bmp = getFromCache(imageId + "");
                        if (bmp != null && !bmp.isRecycled()) {
                            ((ImageView) imageView).setImageBitmap(bmp);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    };
}
