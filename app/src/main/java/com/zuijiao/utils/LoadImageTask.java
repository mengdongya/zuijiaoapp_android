package com.zuijiao.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import net.zuijiao.android.zuijiao.R;

/**
 * Created by xiaqibo on 2015/7/15.
 */
public class LoadImageTask extends AsyncTask<String, Void, Void>{

    private Context mContext ;
    private String mImagePath ;
    private Bitmap mBmp ;
    private ImageView mTargetImageView ;
    private int mHeight = 1080 , mWidth = 720 ;
    private int mResId = -1 ;

    public LoadImageTask(Context context ,String imagePath ,ImageView imageView){
        this.mContext = context ;
        this.mImagePath = imagePath ;
        this.mTargetImageView = imageView ;
    }

    public LoadImageTask(Context context ,int resId ,ImageView imageView , int height , int width  ){
        this.mContext = context ;
        this.mResId = resId ;
        this.mTargetImageView = imageView ;
        this.mHeight = height ;
        this.mWidth = width  ;
    }

    @Override
    protected Void doInBackground(String... params) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if(mImagePath != null)
            BitmapFactory.decodeFile(mImagePath, options);
        else
            BitmapFactory.decodeResource(mContext.getResources(),mResId , options) ;
        options.inSampleSize = calculateInSampleSize(options, mWidth, mHeight);
        options.inJustDecodeBounds = false;
        try {
//            mBmp = BitmapFactory
//                    .decodeFile(mImagePath, options);
            if(mImagePath != null)
                mBmp =BitmapFactory.decodeFile(mImagePath , options);
            else
                mBmp = BitmapFactory.decodeResource(mContext.getResources() , mResId , options) ;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        mTargetImageView.setImageResource(R.drawable.empty_view_greeting);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("pictureFragment", "LoadImageTask executed");
        if (mBmp != null && !mBmp.isRecycled()) {
            mTargetImageView.setImageBitmap(mBmp);
        } else {
            Toast.makeText(mContext, "out of memory , try later ", Toast.LENGTH_SHORT).show();
//                mImageView.setImageResource(R.drawable.empty_view_greeting);
        }
    }


    /**
     * calculate the rate of compression
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
