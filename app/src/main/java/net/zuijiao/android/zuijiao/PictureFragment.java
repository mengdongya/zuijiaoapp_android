package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

@SuppressLint("ValidFragment")
public class PictureFragment extends Fragment {
    private final String TAG = "PictrueFragment";
    private String mImageUrl;
    private boolean isLoaded = false;
    private String mFilePath = null;
    private String url = null;
    private int count = 1;

    @SuppressLint("ValidFragment")
    public PictureFragment(String imageUrl) {

        this.mImageUrl = imageUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "oncreateView");
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_picture, null);
        final ImageView imageView = (ImageView) view
                .findViewById(R.id.scale_pic_item);

        final ProgressBar progressbar = (ProgressBar) view
                .findViewById(R.id.big_image_progressbar);
//		try {
//			imageView.setImageBitmap(BitmapFactory
//					.decodeStream(new FileInputStream(new File(mFilePath))));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
        // if(!isLoaded|| !new File(mFilePath).exists()){
        // loadPic(url) ;
        // }
//		if (netMng.getCount() <= 6)
//			new Handler().postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					imageView.setImageResource(resId);
//					progressbar.setVisibility(View.GONE);
//					count++;
//				}
//			}, 2000);
//		else{
        //imageView.setImageResource(resId);
        Picasso.with(getActivity().getApplicationContext())
                .load(mImageUrl)
                .placeholder(R.drawable.empty_view_greeting)
                .into(imageView);
        progressbar.setVisibility(View.GONE);
//		}
        // imageView.setImageResource(resId);
        return view;
    }

    private void loadPic(String ur) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }
}
