package com.zuijiao.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zuijiao.entity.SimpleImage;

import net.zuijiao.android.zuijiao.R;

import java.util.List;
import java.util.Random;

/**
 * Created by yitianhao on 2015/7/8.
 */
public class ImageItem extends LinearLayout{

    private ImageView indicatorImage;
    private ImageView image;
    private onItemClickListener l;
    private int position;

    public ImageItem(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.multi_select_image_item, this, true);
       // setOnClickListener(this);
        indicatorImage = (ImageView) findViewById(R.id.multi_toggle_button);
        image = (ImageView) findViewById(R.id.multi_image_view);
    }

//    @Override
//    public void onClick(View view) {
//        if (l != null)
//            l.onItemClick(position);
//    }

    private void setDrawingable() {
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
    }

    public void setImageFilter(boolean isChecked){
        if (isChecked) {
            setDrawingable();
            image.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            image.clearColorFilter();
        }
    }

    public void setIndicatorImageVisibility(int i){
        indicatorImage.setVisibility(i);
    }

    /**
     * image clicked
     */
    public interface onItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnClickListener(onItemClickListener l, int position) {
        this.l = l;
        this.position = position;
    }

    /**
     * set image
     * @param images
     */
    public void setImageDrawable(final SimpleImage images) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageLoader.getInstance().displayImage("file://" + images.data, image);
            }
        }, new Random().nextInt(10));
    }
}
