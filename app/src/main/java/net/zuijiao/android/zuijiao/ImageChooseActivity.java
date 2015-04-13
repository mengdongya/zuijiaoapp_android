package net.zuijiao.android.zuijiao;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.utils.UpyunUploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private List<Photo> photos = null;
    private Context mContext = null;
    private HashMap<String, Bitmap> data = null;
    private ArrayList<String> ids = null;
    private ProgressDialog mDialog = null;
    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            Uri uri_temp = Uri.parse("content://media/external/images/media/"
                    + photos.get(position).id);
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
                holder.image = (ImageView) convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Bitmap bitmap = null;
            if ((bitmap = getFromCache(photos.get(position).id)) == null
                    || bitmap.getByteCount() <= 0 || bitmap.isRecycled()) {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        mContext.getContentResolver(),
                        Integer.parseInt(photos.get(position).id),
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
                addToCache(bitmap, photos.get(position).id);
            }
            holder.image.setImageBitmap(bitmap);
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
            return photos.size();
        }
    };
    ;

    @Deprecated
    protected void findViews() {

    }

    protected void registeViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.image_chooser));
        mContext = getApplicationContext();
        photos = getPhotos();
        mGdView.setAdapter(mAdapter);
        mGdView.setOnItemClickListener(mListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addToCache(Bitmap bitmap, String id) {
        if (data == null) {
            data = new HashMap<String, Bitmap>();
        }
        if (ids == null) {
            ids = new ArrayList<String>();
        }
        int i = 0;
        while (ids.size() >= 100) {
            String strId = ids.get(0);
            Bitmap bm = data.get(strId);
            data.remove(bm);
            bm.recycle();
            bm = null;
            ids.remove(0);
        }
        ids.add(id);
        data.put(id, bitmap);
    }

    private Bitmap getFromCache(String id) {
        if (data == null) {
            return null;
        }
        synchronized (data) {
            try {
                Bitmap bitmap = data.get(id);
                return bitmap;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return null;
    }

    private List<Photo> getPhotos() {
        List<Photo> list = new ArrayList<Photo>();
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        Photo photo = null;
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            String displayname = cursor.getString(0);
            photo = new Photo();
            photo.name = displayname;
            photo.id = id;
            list.add(photo);
        }
        cursor.close();
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleBitmap();
    }

    private void recycleBitmap() {
        if (ids != null && data != null) {
            try {
                for (String str : ids) {
                    Bitmap bitmap = data.get(str);
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_ZOOM) {
                photoZoom(data.getData());
            }
            if (requestCode == PHOTO_RESULT) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mDialog = ProgressDialog.show(ImageChooseActivity.this, "", getString(R.string.on_uploading));
                    Bitmap photo = extras.getParcelable("data");
//                    String desPath = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + File.separator + FILE_NAME;
                    File file = new File(getCacheDir().getPath() + File.separator + "head.jpg");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (photo.compress(Bitmap.CompressFormat.JPEG, 75, os)) {
                        try {
                            os.flush();
                            os.close();
                            os = null;
                            photo.recycle();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        new UpyunUploadTask(getCacheDir().getPath() + File.separator + "head.jpg"
                                , UpyunUploadTask.avatarPath(Router.INSTANCE.getCurrentUser().get().getIdentifier(), "jpg")
                                , (long transferedBytes, long totalBytes) -> {
                        }
                                , (boolean isComplete, String result, String error) -> {
                            if (isComplete) {
                                String avatarPath = UpyunUploadTask.avatarPath(Router.INSTANCE.getCurrentUser().get().getIdentifier(), "jpg");
                                Router.INSTANCE.getCurrentUser().get().setAvatarURL(avatarPath);
                                mPreferMng.saveAvatarPath(UpyunUploadTask.avatarPath(avatarPath));

                                Router.getAccountModule().updateAvatar(avatarPath, () -> {
                                    Intent intent = new Intent();
                                    intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                                    sendBroadcast(intent);
                                }, () -> {
                                    // TODO: Avatar upload failed
                                });
                            }
                            if (mDialog == null) {
                                return;
                            }
                            mDialog.dismiss();
                            mDialog = null;
                        }
                        ).execute();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ViewHolder {
        ImageView image;
    }

    class Photo {
        public String name;
        public String id;
    }
}
