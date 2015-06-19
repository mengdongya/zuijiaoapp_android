package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaqibo on 2015/5/4.
 */
@ContentView(R.layout.activity_multi_image_choose)
public class MultiImageChooseActivity extends BaseActivity {

    @ViewInject(R.id.multi_image_choose_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.multi_image_choose_grid_view)
    private GridView mGridView = null;
    @ViewInject(R.id.multi_image_choose_sure_btn)
    private Button mSureBtn = null;
    private List<SimpleImage> images = null;
    private int mMaxCount = 5;
    private static final int CACHE_SIZE = 50;
    //cached bitmap ;
    private HashMap<String, Bitmap> mCachedData = new HashMap<>();
    //id of cached bitmap ;
    private ArrayList<String> mCachedId = new ArrayList<String>();
    private ArrayList<SimpleImage> mSelectedImage = new ArrayList<>();


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
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 4;
        Log.i("memory>>>>>>>>>>>>>>>", "maxMemory == " + maxMemory);
        mGridView.setOnItemClickListener(mGridListener);
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
        if (images == null || images.size() == 0) {
            return;
        }
        for (SimpleImage image : images) {
            if (mCachedData.size() >= 20) {
                break;
            }
            try {
                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        mContext.getContentResolver(),
                        Integer.parseInt(image.id),
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
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


    private void addToCache(Bitmap bitmap, String id) {
        if (mCachedData == null) {
            mCachedData = new HashMap<String, Bitmap>();
        }
        if (mCachedId == null) {
            mCachedId = new ArrayList<String>();
        }
        int i = 0;
        if (mCachedId.size() >= CACHE_SIZE) {

            while (mCachedId.size() >= CACHE_SIZE) {
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
        mCachedId.add(id);
        mCachedData.put(id, bitmap);
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
                        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                                mContext.getContentResolver(),
                                Integer.parseInt(image.id),
                                MediaStore.Images.Thumbnails.MINI_KIND, null);
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
                    mImageThreadPool = Executors.newFixedThreadPool(2);
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
//            if(mGridView.getAdapter() == null){

//                mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                    }
//
//                    @Override
//                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                        int loadPreciousFrom = (firstVisibleItem - 20 )<0 ? 0 : firstVisibleItem - 20 ;
////                        int loadPreciousTo =  (firstVisibleItem - 10 )<0 ? 0 : firstVisibleItem - 10 ;
////                        mFileMng.addToLoadList(images.subList(loadPreciousFrom,loadPreciousTo));
////                        int loadMoreFrom = firstVisibleItem + visibleItemCount +1> images.size() -1 ? images.size() -1 :firstVisibleItem + visibleItemCount +1 ;
////                        int loadMoreTo = firstVisibleItem + visibleItemCount +11> images.size() -1 ? images.size() -1 :firstVisibleItem + visibleItemCount + 11 ;
////                        mFileMng.addToLoadList(images.subList( loadMoreFrom,loadMoreTo) );
//                    }
//                });
//            }else
//                mGridViewAdapter.notifyDataSetInvalidated();
//            SimpleImage image = (SimpleImage) msg.obj;
//            ImageView imageView = (ImageView) mGridView.findViewWithTag(image.id);
//            if(imageView!= null){
//                imageView.setImageBitmap(mFileMng.getBitmapFromMemCache(image.id));
//            }
//            int position = images.indexOf(image) ;
//            mGridViewAdapter.getView(position ,null ,mGridView) ;
//            mGridView.getOnItemClickListener().onItemClick(mGridView);
//            mGridViewAdapter.notifyDataSetChanged();
//            for(SimpleImage image : images){
//                if(image.id.equals(id)){
//                    mGridViewAdapter.getView(images.indexOf(image) ,mGridView.getChildAt(images.indexOf(image)) , mGridView) ;
//                    break ;
//                }
//            }

    //            if(mGridView.getAdapter() == null)
//                mGridView.setAdapter(mGridViewAdapter);
//            else
//                mGridViewAdapter.notifyDataSetChanged(); ;
//        }
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
