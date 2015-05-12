package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.controller.FileManager;
import com.zuijiao.entity.SimpleImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                holder.btn = (ToggleButton) convertView.findViewById(R.id.multi_toggle_button);
                holder.image = (ImageView) convertView.findViewById(R.id.multi_image_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Bitmap bitmap;
            if ((bitmap = getFromCache(images.get(position).id)) == null
                    || bitmap.getByteCount() <= 0 || bitmap.isRecycled()) {
                try {
                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                            mContext.getContentResolver(),
                            Integer.parseInt(images.get(position).id),
                            MediaStore.Images.Thumbnails.MINI_KIND, null);
                    addToCache(bitmap, images.get(position).id);
                    System.out.println("bitmap == " + bitmap.toString());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            try {
                holder.image.setImageBitmap(bitmap);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            SimpleImage image = images.get(position);
//            String selectData = images.get(position).data;
//            String selectId = images.get(position).id;
            holder.btn.setChecked(selectedImageContainsCurrentImage(image));
            holder.btn.setOnClickListener((View view) -> {
                if (selectedImageContainsCurrentImage(image)) {
                    removeItem(image);
                    ((ToggleButton) view).setChecked(false);
                } else {
                    if (mSelectedImage.size() >= mMaxCount) {
                        Toast.makeText(mContext, String.format(getString(R.string.image_count_upper_limit), mMaxCount), Toast.LENGTH_SHORT).show();
                        ((ToggleButton) view).setChecked(false);
                    } else {
                        mSelectedImage.add(image);
                        ((ToggleButton) view).setChecked(true);
                    }
                }
                mSureBtn.setText(String.format(getString(R.string.sure_with_num), mSelectedImage.size(), mMaxCount));
            });
            return convertView;
        }

        private boolean selectedImageContainsCurrentImage(SimpleImage image) {
            for (SimpleImage si : mSelectedImage) {
                if (si.data.equals(image.data)) {
                    return true;
                }
            }
            return false;
        }

        private void removeItem(SimpleImage image) {
            for (SimpleImage si : mSelectedImage) {
                if (si.data.equals(image.data)) {
                    mSelectedImage.remove(si);
                    return;
                }
            }
        }
    };
    //cached bitmap ;
    private HashMap<String, Bitmap> mCachedData = new HashMap<>();
    //id of cached bitmap ;
    private ArrayList<String> mCachedId = new ArrayList<String>();
    private ArrayList<SimpleImage> mSelectedImage = new ArrayList<>();
    //    private ArrayList<String> mSelectedId = new ArrayList<>();
//    private ArrayList<String> mSelectedPath = new ArrayList<>();
    private AdapterView.OnItemClickListener mGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mGridViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSelectedImage = mTendIntent.getParcelableArrayListExtra("edit_images");
        mMaxCount = mMaxCount - mTendIntent.getIntExtra("cloud_image_size", 0);
//        mSelectedPath = mTendIntent.getStringArrayListExtra("selected_image_path");
//        mSelectedId = mTendIntent.getStringArrayListExtra("selected_image_id");
        images = FileManager.getImageList(mContext);
        mSureBtn.setEnabled(true);
        mSureBtn.setText(String.format(getString(R.string.sure_with_num), mSelectedImage.size(), mMaxCount));
        mSureBtn.setOnClickListener((View v) -> {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("edit_images", mSelectedImage);
//            intent.putStringArrayListExtra("selected_image_id", mSelectedId);
//            intent.putStringArrayListExtra("selected_image_path", mSelectedPath);
            setResult(RESULT_OK, intent);
            finish();
        });
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(mGridListener);
        new Thread(() -> {
            initCache();
        }).start();
    }

    private void initCache() {
        if (images == null || images.size() == 0) {
            return;
        }
        for (SimpleImage image : images) {
            if (mCachedData.size() >= 100) {
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
        mHandler.sendEmptyMessage(0x1001);

    }

    private void addToCache(Bitmap bitmap, String id) {
        if (mCachedData == null) {
            mCachedData = new HashMap<String, Bitmap>();
        }
        if (mCachedId == null) {
            mCachedId = new ArrayList<String>();
        }
        int i = 0;
        while (mCachedId.size() >= 500) {
            String strId = mCachedId.get(0);
            Bitmap bm = mCachedData.get(strId);
            mCachedData.remove(bm);
            bm.recycle();
            bm = null;
            mCachedId.remove(0);
        }
        mCachedId.add(id);
        mCachedData.put(id, bitmap);
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
        ToggleButton btn;
    }
}
