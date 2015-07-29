package com.zuijiao.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.zuijiao.entity.SimpleImage;
import com.zuijiao.view.ImageItem;

import net.zuijiao.android.zuijiao.R;

import java.util.ArrayList;

/**
 * Created by yitianhao on 2015/7/27.
 */
public class PhotoSelectorAdapter extends MBaseAdapter<SimpleImage> {

    private int itemWidth;
    private int horizontalNum = 3;
    private ArrayList<SimpleImage> mSelectedImage;
    private AbsListView.LayoutParams itemLayoutParams;

    public PhotoSelectorAdapter(Context context, ArrayList<SimpleImage> models) {
        super(context, models);
    }

    public PhotoSelectorAdapter(Context context, ArrayList<SimpleImage> models, ArrayList<SimpleImage> selectedImage, int screenWidth) {
        this(context, models);
        this.mSelectedImage = selectedImage;
        setItemWidth(screenWidth);
    }

    public void setItemWidth(int screenWidth) {
        int horizontalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
        this.itemWidth = (screenWidth - (horizontalSpace * (horizontalNum - 1))) / horizontalNum;
        this.itemLayoutParams = new AbsListView.LayoutParams(itemWidth, itemWidth);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageItem imageItem = null;
        if (convertView == null) {
            imageItem = new ImageItem(context);
            imageItem.setLayoutParams(itemLayoutParams);
            convertView = imageItem;
        } else {
            imageItem = (ImageItem) convertView;
        }
        imageItem.setImageDrawable(models.get(position));
        imageItem.setIndicatorImageVisibility(selectedImageContainsCurrentImage(models.get(position)) ? View.VISIBLE : View.INVISIBLE);
        imageItem.setImageFilter(selectedImageContainsCurrentImage(models.get(position)));

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
}
