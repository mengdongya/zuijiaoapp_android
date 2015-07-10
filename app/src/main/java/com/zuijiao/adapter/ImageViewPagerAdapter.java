package com.zuijiao.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by xiaqibo on 2015/6/15.
 * image viewpager adapter ,used in banquet-detail-activity,gourmet-detail-activity and host-and-guest-activity
 */
public class ImageViewPagerAdapter extends PagerAdapter {
    private List<ImageView> data;

    public ImageViewPagerAdapter(List<ImageView> data) {
        super();
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(data.get(position));
        return data.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(data.get(position));
    }
}
