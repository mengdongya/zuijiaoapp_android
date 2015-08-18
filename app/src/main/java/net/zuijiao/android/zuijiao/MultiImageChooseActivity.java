package net.zuijiao.android.zuijiao;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.upyun.block.api.listener.CompleteListener;
import com.zuijiao.adapter.PhotoSelectorAdapter;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;
import com.zuijiao.entity.SimpleImage;
import com.zuijiao.utils.OSUtil;
import com.zuijiao.utils.UpyunUploadTask;
import com.zuijiao.view.ImageItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
    private List<SimpleImage> images = new ArrayList<SimpleImage>();
    private int mMaxCount = DEFAULT_MAX_IMAGE_SIZE;
    // private static final int CACHE_SIZE = 20;
    //cached bitmap ;
    // private HashMap<String, Bitmap> mCachedData = new HashMap();
    //id of cached bitmap ;
    // private ArrayList<String> mCachedId = new ArrayList<String>();
    private ArrayList<SimpleImage> mSelectedImage = new ArrayList<>();
    //private ContentResolver mResolver = null;
    //private final int maxSize = 80;
    private PhotoSelectorAdapter mGridViewAdapter;

    private static final int DEFAULT_MAX_IMAGE_SIZE = 5 ;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private boolean mFromWeb = false ;
//    private ArrayList<SimpleImage> mImages = new ArrayList<>();
    @Override
    protected void registerViews() {


//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//                .defaultDisplayImageOptions(((ActivityTask) getApplication()).getDefaultDisplayImageOptions()).memoryCacheExtraOptions(100, 100)
//                .threadPoolSize(5).build();
//        ImageLoader.getInstance().init(config);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(100, 100)
                        // default = device screen dimensions
                .diskCacheExtraOptions(100, 100, null)
                .threadPoolSize(5)
                        // default Thread.NORM_PRIORITY - 1
                .threadPriority(Thread.NORM_PRIORITY)
                        // default FIFO
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                        // default
                .diskCache(
                        new UnlimitedDiscCache(StorageUtils.getCacheDirectory(
                                this, true)))
                        // default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                        // default
                .imageDownloader(new BaseImageDownloader(this))
                        // default
                .imageDecoder(new BaseImageDecoder(false))
                        // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        // default
                .defaultDisplayImageOptions(((ActivityTask) getApplication()).getDefaultDisplayImageOptions()).build();

        ImageLoader.getInstance().init(config);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mTendIntent != null){
            mFromWeb = mTendIntent.getBooleanExtra("from_web" , false) ;
            mMaxCount = mTendIntent.getIntExtra("max_images_size" , DEFAULT_MAX_IMAGE_SIZE) ;
        }
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mGridViewAdapter = new PhotoSelectorAdapter(mContext, new ArrayList<SimpleImage>(), mSelectedImage, dm.widthPixels);
        mGridView.setAdapter(mGridViewAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                images = FileManager.getImageList(mContext);
                Message msg = handler.obtainMessage();
                msg.obj = images;
                handler.sendMessage(msg);
            }
        }).start();

        mSureBtn.setEnabled(true);
        mSureBtn.setText(String.format(getString(R.string.sure_with_num), mSelectedImage.size(), mMaxCount));
        mSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putParcelableArrayListExtra("edit_images", mSelectedImage);
//                setResult(RESULT_OK, intent);
//                finish();
//                mImageUrls.addAll(UpyunUploadTask.gourmetImagePaths(
//                        mPreferMng.getStoredUserId(), mEditName, mImages.size(), "jpg"));
                mImageUrls.addAll(UpyunUploadTask.banquetImageUrls(mSelectedImage.size())) ;
                uploadOnCompressed();
            }
        });
        mGridView.setOnItemClickListener(mGridListener);
//        mResolver = mContext.getContentResolver();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                initCache();
//            }
//        }).start();
    }

    private void uploadOnCompressed() {
        AsyncTask compressTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object o) {
                uploadImageContinuously(mSelectedImage.get(0), new LambdaExpression() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void action() {
                        if(mFromWeb){
                            if(CommonWebViewActivity.mWebView !=null){
                                ArrayList<String> testArray= new ArrayList<String>();
                                for(String url :mImageUrls){
                                    if(url != null){
                                        testArray.add("http://zuijiao-app.b0.upaiyun.com" + url) ;
                                    }
                                }
//                                String urls = Router.convertJsonFromList(testArray) ;

                                if(OSUtil.getAPILevel() >= android.os.Build.VERSION_CODES.KITKAT) {
                                    CommonWebViewActivity.mWebView.evaluateJavascript("fill_image_urls(" + Router.convertJsonFromList(testArray)+")" ,null);
                                }else
                                    CommonWebViewActivity.mWebView.loadUrl("javascript:fill_image_urls(" + Router.convertJsonFromList(testArray)  +")" );
                            }
                            finish();
                        }

                        finalizeDialog();
                        //todo general
                    }
                });
                super.onPostExecute(o);
            }

            @Override
            protected Object doInBackground(Object[] params) {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    Toast.makeText(mContext, getString(R.string.comment), Toast.LENGTH_LONG).show();
                    return null;
                }
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                for (SimpleImage simpleImage : mSelectedImage) {
                    BitmapFactory.decodeFile(simpleImage.data, options);
                    options.inSampleSize = calculateInSampleSize(options, 480, 800);
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(simpleImage.data, options);
                    if (FileManager.createRootFolder()) {
                        File desFile = new File(FileManager.getAppRootPath() + File.separator + FileManager.COMPRESS_FOLDER + new Date().getTime());
                        try {
                            if (!desFile.exists()) {
                                FileManager.createFolder(desFile.getParent());
                                desFile.createNewFile();
                            }
                            OutputStream os = null;
                            os = new FileOutputStream(desFile);
                            if (bm.compress(Bitmap.CompressFormat.JPEG, 75, os)) {
                                os.flush();
                                os.close();
                                os = null;
                                bm.recycle();
                                simpleImage.data = desFile.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };
        compressTask.execute();
    }


    /**
     * get a reasonable size of the image;
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    private void uploadImageContinuously(SimpleImage image, LambdaExpression lambdaExpression) {
        String imageUrl = mImageUrls.get(mSelectedImage.indexOf(image));
        String msg = String.format(getString(R.string.upload_image), mSelectedImage.indexOf(image) + 1, mSelectedImage.size());
        createDialog(msg);
        new UpyunUploadTask(image.data, imageUrl, null,
                new CompleteListener() {
                    @Override
                    public void result(boolean isComplete, String s, String s2) {
                        if (isComplete) {
//                        mImageUrls.add(imageUrl);
                            SimpleImage nextImage = getNextImage(image);
                            if (nextImage != null && new File(nextImage.data).exists()) {
                                uploadImageContinuously(nextImage, lambdaExpression);
                            } else {
                                lambdaExpression.action();

                            }
                        } else {
                            mImageUrls.clear();
                            finalizeDialog();
                        }
                    }
                }
        ).execute();
    }

    private SimpleImage getNextImage(SimpleImage currentImage) {
        try {
            return mSelectedImage.get(mSelectedImage.indexOf(currentImage) + 1);
        } catch (Throwable t) {
            return null;
        }
    }

    final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            onImagesLoaded((List<SimpleImage>) msg.obj);
        }
    };

    private void onImagesLoaded(List<SimpleImage> images) {
//        if (images == null || images.size() == 0)
//            return;
//        if (mGridViewAdapter != null) {
//            mGridView.setAdapter(mGridViewAdapter);
//            // mGridViewAdapter.notifyDataSetChanged();
//            // mGridView.smoothScrollToPosition(0);
//        }
        mGridViewAdapter.update(images);
        mGridView.smoothScrollToPosition(0);
    }

    private void removeItem(SimpleImage image) {
        for (SimpleImage si : mSelectedImage) {
            if (si.data.equals(image.data)) {
                mSelectedImage.remove(si);
                return;
            }
        }
    }

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
                    return;
                } else {
                    mSelectedImage.add(image);
                }
            }
            // mGridViewAdapter.getView(position, view, parent);

            // imageItem.setIndicatorImageVisibility(selectedImageContainsCurrentImage(image) ? View.VISIBLE : View.INVISIBLE);
            // imageItem.setImageFilter(selectedImageContainsCurrentImage(image));
            ((ImageItem) view).setIndicatorImageVisibility(selectedImageContainsCurrentImage(image) ? View.VISIBLE : View.INVISIBLE);
            ((ImageItem) view).setImageFilter(selectedImageContainsCurrentImage(image));
            mSureBtn.setText(String.format(getString(R.string.sure_with_num), mSelectedImage.size(), mMaxCount));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        for (String id : mCachedId) {
//            try {
//                Bitmap bmp = mCachedData.get(id);
//                if (bmp != null && !bmp.isRecycled())
//                    bmp.recycle();
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//        System.gc();
    }

//    private void initCache() {
//        Date date = new Date();
//        if (images == null || images.size() == 0) {
//            return;
//        }
//        for (SimpleImage image : images) {
//            if (mCachedData.size() >= 2) {
//                break;
//            }
//            try {
//                Bitmap bitmap = mFileMng.getImageBmpById(image.id, mResolver);
//                addToCache(bitmap, image.id);
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//        Message msg = mHandler.obtainMessage();
//        msg.what = 0x10001;
//        mHandler.sendMessage(msg);
//        Log.i(LOG, new Date().getTime() - date.getTime() + "");
//    }


//    private Bitmap getImageBitmap(String id) {
//
//    }


//    private void addToCache(Bitmap bitmap, String id) {
//        if (mCachedData == null) {
//            mCachedData = new HashMap<String, Bitmap>();
//        }
//        if (mCachedId == null) {
//            mCachedId = new ArrayList<String>();
//        }
//        int i = 0;
//        if (mCachedData.size() > maxSize) {
//            while (mCachedData.size() > maxSize) {
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
//        if (bitmap != null && !bitmap.isRecycled()) {
//            mCachedId.add(id);
//            mCachedData.put(id, bitmap);
//        }
//    }

    private BaseAdapter mGridViewAdapter1 = new BaseAdapter() {
        @Override
        public int getCount() {
            if (images == null) {
                return 0;
            }
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // ViewHolder holder = null;
            ImageItem imageItem = null;
            if (convertView == null) {
                imageItem = new ImageItem(mContext);
                imageItem.setLayoutParams(new AbsListView.LayoutParams(parent.getWidth() / 3, parent.getWidth() / 3));
                convertView = imageItem;
//                holder = new ViewHolder();
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.multi_select_image_item, null);
//                holder.indicatorImage = (ImageView) convertView.findViewById(R.id.multi_toggle_button);
//                holder.image = (ImageView) convertView.findViewById(R.id.multi_image_view);
//                convertView.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 3, parent.getWidth() / 3));
//                convertView.setTag(holder);
            } else {
                // holder = (ViewHolder) convertView.getTag();
                imageItem = (ImageItem) convertView;
            }
            SimpleImage image = images.get(position);
//            Bitmap bitmap = getFromCache(image.id);
            imageItem.setImageDrawable(image);
            // System.out.println("position:" + position + " " + image.data);
            //imageItem.setOnClickListener(MultiImageChooseActivity.this, position);
            imageItem.setIndicatorImageVisibility(selectedImageContainsCurrentImage(image) ? View.VISIBLE : View.INVISIBLE);
            imageItem.setImageFilter(selectedImageContainsCurrentImage(image));
            //imageItem.setIndicatorImageVisibility(View.INVISIBLE);


//            System.err.println("position == " + position + "   image id == " + image.id + "bitmap == null ? -> " + (bitmap == null || bitmap.isRecycled()));
//            holder.image.setTag(Integer.parseInt(image.id));
//            if (bitmap == null || bitmap.isRecycled()) {
//                holder.image.setImageResource(R.drawable.empty_view_greeting);
//                getThreadPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                       Bitmap bitmap = mFileMng.getImageBmpById(image.id, mResolver);
//                        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                                mResolver,
//                                Integer.parseInt(image.id),
//                                MediaStore.Images.Thumbnails.MINI_KIND, null);
//                        bitmap = compressImage(bitmap);
            //                       addToCache(bitmap, image.id);
//                        Message msg = mHandler.obtainMessage();
//                        msg.what = Integer.parseInt(image.id);
//                        mHandler.sendMessage(msg);
//                    }
//                });
//            } else {
//                holder.image.setImageBitmap(bitmap);
//            }
//            holder.indicatorImage.setVisibility(selectedImageContainsCurrentImage(image) ? View.VISIBLE : View.INVISIBLE);
            return convertView;
        }
    };


//    private ExecutorService mImageThreadPool = null;
//
//    public ExecutorService getThreadPool() {
//    if (mImageThreadPool == null) {
//        synchronized (ExecutorService.class) {
//            mImageThreadPool = Executors.newFixedThreadPool(5);
//        }
//    }
//    return mImageThreadPool;
//}
// 
//   private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 0x10001) {
//                mGridView.setAdapter(mGridViewAdapter);
//            } else {
//                try {
//                    int imageId = msg.what;
//                    View imageView = mGridView.findViewWithTag(imageId);
//                    if (imageView != null) {
//                        Bitmap bmp = getFromCache(imageId + "");
//                        if (bmp != null && !bmp.isRecycled()) {
//                            ((ImageView) imageView).setImageBitmap(bmp);
//                        }
//                    }
//                } catch (Throwable t) {
//                    t.printStackTrace();
//                }
//            }
//        }
//    };

    private Bitmap compressImage(Bitmap image) {
        if (image.getByteCount() / 1024 < 400) {
            return image;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 40;
            if (options < 50)
                break;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
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


//    private Bitmap getFromCache(String id) {
//        if (mCachedData == null) {
//            return null;
//        }
//        synchronized (mCachedData) {
//            try {
//                Bitmap bitmap = mCachedData.get(id);
//                if (bitmap == null || bitmap.isRecycled()) {
//                    mCachedId.remove(id);
//                    mCachedData.remove(id);
//                    return null;
//                }
//                return bitmap;
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//        return null;
//    }

//    private class ViewHolder {
//        ImageView image;
//        ImageView indicatorImage;
//    }
}
