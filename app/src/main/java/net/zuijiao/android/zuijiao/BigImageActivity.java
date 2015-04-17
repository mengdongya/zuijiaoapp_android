package net.zuijiao.android.zuijiao;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.view.MyViewPager;

import java.io.File;
import java.util.ArrayList;

@ContentView(R.layout.activity_big_image)
public class BigImageActivity extends BaseActivity {
    @ViewInject(R.id.big_image_container)
    private MyViewPager mViewPager = null;

    private ArrayList<Fragment> contentFragment = null;
    private ArrayList<String> mImageUrls = null;
    private int mCurrentImageIndex = 0;
    @ViewInject(R.id.big_image_toolbar)
    private Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registeViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (mTendIntent != null) {
            mImageUrls = getIntent().getStringArrayListExtra("image_url");
            if (mImageUrls == null || mImageUrls.size() == 0) {
                finish();
            }
            mCurrentImageIndex = mTendIntent.getIntExtra("current_image_index", 0);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((mCurrentImageIndex + 1) + File.separator + mImageUrls.size());
        contentFragment = new ArrayList<Fragment>();
        for (int i = 0; i < mImageUrls.size(); i++) {
            contentFragment.add(new PictureFragment(mImageUrls.get(i)));
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager(), mImageUrls.size());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mPageListener);
        mViewPager.setCurrentItem(mCurrentImageIndex);
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

    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            getSupportActionBar().setTitle((arg0 + 1) + File.separator + mImageUrls.size());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
}
