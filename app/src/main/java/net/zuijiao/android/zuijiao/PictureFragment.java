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

import com.squareup.picasso.Picasso;

import java.io.File;

@SuppressLint("ValidFragment")
public class PictureFragment extends Fragment {
    private final String TAG = "PictrueFragment";
    private String mImageUrl;
    private Bitmap mBmp = null;
    private ImageView mImageView = null;
    private boolean isLocalImage = false;

    @SuppressLint("ValidFragment")
    public PictureFragment(String imageUrl) {
        super();
        this.mImageUrl = imageUrl;
    }

    //if the content image is at local
    public PictureFragment setType(boolean bLocal) {
        isLocalImage = bLocal;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_picture, null);
        mImageView = (ImageView) view
                .findViewById(R.id.scale_pic_item);

//        final ProgressBar progressbar = (ProgressBar) view
//                .findViewById(R.id.big_image_progressbar);
        File image = new File(mImageUrl);
        if (isLocalImage && image.exists()) {
            if (mBmp != null) {
                mImageView.setImageBitmap(mBmp);
            } else {
                new LoadImageTask().execute(mImageUrl);
            }
        } else {
            Picasso.with(getActivity().getApplicationContext())
                    .load(mImageUrl)
                    .placeholder(R.drawable.empty_view_greeting)
                    .into(mImageView);
        }
        return view;
    }

    private void loadPic(String ur) {

    }

    ;

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    public Bitmap getBitmap() {
        return mBmp;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(mImageUrl, options);
            long height = options.outHeight * 1000 / options.outWidth;
            options.outWidth = 1000;
            options.outHeight = (int) height;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inPurgeable = true;
            options.inInputShareable = true;
            mBmp = BitmapFactory
                    .decodeFile(mImageUrl, options);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImageView.setImageResource(R.drawable.empty_view_greeting);
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mBmp != null) {
                mImageView.setImageBitmap(mBmp);
            } else {
                mImageView.setImageResource(R.drawable.empty_view_greeting);
            }
        }
    }

}
