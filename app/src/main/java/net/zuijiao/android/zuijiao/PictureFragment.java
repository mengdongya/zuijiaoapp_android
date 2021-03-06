package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zuijiao.utils.LoadImageTask;

import java.io.File;

/**
 * show one image in the whole screen ,included in big-image-activity
 */
@SuppressLint("ValidFragment")
public class PictureFragment extends Fragment {
    private final String TAG = "PictrueFragment";
    private String mImageUrl = null ;
    private Bitmap mBmp = null;
    private ImageView mImageView = null;
    /**
     * sign the image is local or cloud ,and use different way to load
     */
    private boolean isLocalImage = false;
    public PictureFragment(){
        super();
    }
    @SuppressLint("ValidFragment")
    public PictureFragment(String imageUrl) {
        super();
        this.mImageUrl = imageUrl;
    }

    public PictureFragment setType(boolean bLocal) {
        isLocalImage = bLocal;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        if(mImageUrl == null && savedInstanceState!=null )
            mImageUrl = savedInstanceState.getString("image_url") ;
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_picture, null);
        mImageView = (ImageView) view
                .findViewById(R.id.scale_pic_item);
        File image = new File(mImageUrl);

        //if is local exist image ,load it with a async-task
        //if not , load by picasso
        if (isLocalImage && image.exists()) {
            if (mBmp != null) {
                mImageView.setImageBitmap(mBmp);
            } else if(getActivity() != null){
                new LoadImageTask(getActivity().getApplicationContext(),mImageUrl ,mImageView).execute(mImageUrl);
            }
        } else {
            Picasso.with(getActivity().getApplicationContext())
                    .load(mImageUrl)
                    .placeholder(R.drawable.empty_view_greeting)
//                    .fit().centerCrop()
                    .into(mImageView);
        }
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.i(TAG, "onViewStateRestored");
//        if(savedInstanceState!=null)
//            mImageUrl = savedInstanceState.getString("image_url") ;
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        outState.putString("image_url" ,mImageUrl) ;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    public Bitmap getBitmap() {
        return mBmp;
    }


//    /**
//     * async task for loading local image
//     */
//    private class LoadImageTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            Bitmap bmp = BitmapFactory.decodeFile(mImageUrl, options);
//            options.inSampleSize = calculateInSampleSize(options, 720, 1080);
//            options.inJustDecodeBounds = false;
//            try {
//                mBmp = BitmapFactory
//                        .decodeFile(mImageUrl, options);
//            } catch (OutOfMemoryError error) {
//                error.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mImageView.setImageResource(R.drawable.empty_view_greeting);
//        }
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Log.d("pictureFragment", "LoadImageTask executed");
//            if (mBmp != null && !mBmp.isRecycled()) {
//                mImageView.setImageBitmap(mBmp);
//            } else {
//                Toast.makeText(getActivity().getApplicationContext(), "out of memory , try later ", Toast.LENGTH_SHORT).show();
////                mImageView.setImageResource(R.drawable.empty_view_greeting);
//            }
//        }
//    }
//
//    /**
//     * calculate the rate of compression
//     *
//     * @param options
//     * @param reqWidth
//     * @param reqHeight
//     * @return
//     */
//    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        int height = options.outHeight;
//        int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        return inSampleSize;
//    }

}
