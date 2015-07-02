package net.zuijiao.android.zuijiao;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.controller.FileManager;
import com.zuijiao.entity.SimpleImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaqibo on 2015/5/4.
 */
@ContentView(R.layout.activity_multi_image_choose)
public class MultiImageChooseActivity extends BaseActivity {
    private final String LOG = "multiImage";
    @ViewInject(R.id.multi_image_choose_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.multi_image_choose_grid_view)
    private GridView mGridView = null;
    @ViewInject(R.id.multi_image_choose_sure_btn)
    private Button mSureBtn = null;
    private List<SimpleImage> images = null;
    private int mMaxCount = 5;
    private static final int CACHE_SIZE = 20;
    //cached bitmap ;
    private HashMap<String, Bitmap> mCachedData = new HashMap();
    //id of cached bitmap ;
    private ArrayList<String> mCachedId = new ArrayList<String>();
    private ArrayList<SimpleImage> mSelectedImage = new ArrayList<>();
    private ContentResolver mResolver = null;
    private long cacheSize = 0;
    private long maxSize = 0;

    static {
//        if((int) Runtime.getRuntime().maxMemory())
    }
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSelectedImage = mTendIntent.getParcelableArrayListExtra("edit_images");
        mMaxCount = mMaxCount - mTendIntent.getIntExtra("cloud_image_size", 0);
        images = FileManager.getImageList(mContext);
        mSureBtn.setEnabled(true);
        mSureBtn.setText(String.format(getString(R.string.sure_with_num), mSelectedImage.size(), mMaxCount));
        mSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("edit_images", mSelectedImage);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        maxSize = (int) Runtime.getRuntime().maxMemory() / 4;
        Log.i("memory", "maxMemory == " + maxSize);
//        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(mGridListener);
        mResolver = mContext.getContentResolver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCache();
            }
        }).start();
    }


    private void removeItem(SimpleImage image) {
        for (SimpleImage si : mSelectedImage) {
            if (si.data.equals(image.data)) {
                mSelectedImage.remove(si);
                return;
            }
        }
    }

    //    private ArrayList<String> mSelectedId = new ArrayList<>();
//    private ArrayList<String> mSelectedPath = new ArrayList<>();
    private AdapterView.OnItemClickListener mGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SimpleImage image = images.get(position);
            if (selectedImageContainsCurrentImage(image)) {
                removeItem(image);
//                ((ToggleButton) view).setChecked(false);
            } else {
                if (mSelectedImage.size() >= mMaxCount) {
                    Toast.makeText(mContext, String.format(getString(R.string.image_count_upper_limit), mMaxCount), Toast.LENGTH_SHORT).show();
//                    ((ToggleButton) view).setChecked(false);
                } else {
                    mSelectedImage.add(image);
//                    ((ToggleButton) view).setChecked(true);
                }
            }
            mGridViewAdapter.getView(position, view, parent);
            mSureBtn.setText(String.format(getString(R.string.sure_with_num), mSelectedImage.size(), mMaxCount));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void initCache() {
        Date date = new Date();
        if (images == null || images.size() == 0) {
            return;
        }
        for (SimpleImage image : images) {
            if (mCachedData.size() >= 2) {
                break;
            }
            try {
                Bitmap bitmap = getImageBitmap(image.id);
//                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                        mResolver,
//                        Integer.parseInt(image.id),
//                        MediaStore.Images.Thumbnails.MINI_KIND, null);
//                bitmap = compressImage(bitmap);
//                if (bitmap != null) {
//                    synchronized (mCachedData) {
//                        mCachedData.put(image.id, bitmap);
//                    }
//                    synchronized (mCachedId) {
//                        mCachedId.add(image.id);
//                    }
//                }
                addToCache(bitmap, image.id);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        Message msg = mHandler.obtainMessage();
        msg.what = 0x10001;
        mHandler.sendMessage(msg);
        Log.i(LOG, new Date().getTime() - date.getTime() + "");
    }


    private Bitmap getImageBitmap(String id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                mResolver,
                Integer.parseInt(id),
                MediaStore.Images.Thumbnails.MINI_KIND, options);
//        int height = options.outHeight * 200 / options.outWidth;
        options.outWidth = 200;
//        options.outHeight = height;
        options.inJustDecodeBounds = false;
//        int inSampleSize = options.outWidth / 200;
//        options.inSampleSize = inSampleSize ;
        int width = 200;
        int height = 200;
        final int minSideLength = Math.min(width, height);
        options.inSampleSize = computeSampleSize(options, minSideLength,
                width * height);
        bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                mResolver,
                Integer.parseInt(id),
                MediaStore.Images.Thumbnails.MINI_KIND, options);
        Log.i("multiImageChoose", "bitmapSize= " + bitmap.getByteCount());
        return bitmap;
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
    private void addToCache(Bitmap bitmap, String id) {
        if (mCachedData == null) {
            mCachedData = new HashMap<String, Bitmap>();
        }
        if (mCachedId == null) {
            mCachedId = new ArrayList<String>();
        }
        int i = 0;
        if (mCachedId.size() >= CACHE_SIZE) {

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
            while (cacheSize > maxSize) {
                String strId = mCachedId.get(0);
                Bitmap bm = mCachedData.get(strId);
                cacheSize -= bm.getByteCount();
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

    private BaseAdapter mGridViewAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (images == null) {
                return 0;
            }
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.multi_select_image_item, null);
                holder.indicatorImage = (ImageView) convertView.findViewById(R.id.multi_toggle_button);
                holder.image = (ImageView) convertView.findViewById(R.id.multi_image_view);
                convertView.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 3, parent.getWidth() / 3));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SimpleImage image = images.get(position);
            Bitmap bitmap = getFromCache(image.id);
            System.err.println("position == " + position + "   image id == " + image.id + "bitmap == null ? -> " + (bitmap == null || bitmap.isRecycled()));
            holder.image.setTag(Integer.parseInt(image.id));
            if (bitmap == null || bitmap.isRecycled()) {
                holder.image.setImageResource(R.drawable.empty_view_greeting);
                getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = getImageBitmap(image.id);
//                        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                                mResolver,
//                                Integer.parseInt(image.id),
//                                MediaStore.Images.Thumbnails.MINI_KIND, null);
//                        bitmap = compressImage(bitmap);
                        addToCache(bitmap, image.id);
                        Message msg = mHandler.obtainMessage();
                        msg.what = Integer.parseInt(image.id);
                        mHandler.sendMessage(msg);
                    }
                });
            } else {
                holder.image.setImageBitmap(bitmap);
            }
            holder.indicatorImage.setVisibility(selectedImageContainsCurrentImage(image) ? View.VISIBLE : View.INVISIBLE);
            return convertView;
        }
    };


    private ExecutorService mImageThreadPool = null;

    public ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    mImageThreadPool = Executors.newFixedThreadPool(5);
                }
            }
        }
        return mImageThreadPool;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x10001) {
                mGridView.setAdapter(mGridViewAdapter);
            } else {
                try {
                    int imageId = msg.what;
                    View imageView = mGridView.findViewWithTag(imageId);
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

    private Bitmap compressImage(Bitmap image) {
        if (image.getByteCount() / 1024 < 400) {
            return image;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 40;//每次都减少10
            if (options < 50)
                break;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    private boolean selectedImageContainsCurrentImage(SimpleImage image) {
        for (SimpleImage si : mSelectedImage) {
            if (si.data.equals(image.data)) {
                return true;
            }
        }
        return false;
    }

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

    private class ViewHolder {
        ImageView image;
        ImageView indicatorImage;
    }
}
