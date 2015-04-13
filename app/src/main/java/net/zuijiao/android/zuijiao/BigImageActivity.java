package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.view.MyViewPager;

import java.util.ArrayList;

@ContentView(R.layout.activity_big_image)
public class BigImageActivity extends BaseActivity {
    @ViewInject(R.id.big_image_container)
    private MyViewPager mViewPager = null;

    private ArrayList<Fragment> contentFragment = null;
    private int mImageCount = 3;
    private ArrayList<String> mImageUrls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registeViews() {
        if (mTendIntent != null) {
            mImageUrls = getIntent().getStringArrayListExtra("image_url");
            if (mImageUrls == null || mImageUrls.size() == 0) {
                finish();
            }
        }
        contentFragment = new ArrayList<Fragment>();
        for (int i = 0; i < mImageUrls.size(); i++) {
            contentFragment.add(new PictureFragment(mImageUrls.get(i)));
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager(), mImageUrls.size());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private int mCount = 0;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.mCount = count;
        }

        @Override
        public Fragment getItem(int position) {
            return contentFragment.get(position);
        }

        @Override
        public int getCount() {
            return mCount;
        }

    }

}
