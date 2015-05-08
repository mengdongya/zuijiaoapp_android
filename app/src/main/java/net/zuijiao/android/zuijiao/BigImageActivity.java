package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.entity.SimpleImage;
import com.zuijiao.view.MyViewPager;

import java.io.File;
import java.util.ArrayList;

@ContentView(R.layout.activity_big_image)
public class BigImageActivity extends BaseActivity {
    @ViewInject(R.id.big_image_container)
    private MyViewPager mViewPager = null;
    private ViewPagerAdapter mAdapter = null;
    private ArrayList<PictureFragment> contentFragment = null;
    private ArrayList<SimpleImage> mImages = null;
    private int mCurrentImageIndex = 0;
    @ViewInject(R.id.big_image_toolbar)
    private Toolbar mToolbar = null;
    private boolean mCanDelete = false;
    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            getSupportActionBar().setTitle((arg0 + 1) + File.separator + mImages.size());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCanDelete) {
            getMenuInflater().inflate(R.menu.big_image, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_big_image) {
            doDeleteOneImage();
        }
        return super.onOptionsItemSelected(item);
    }

    //delete current image
    private void doDeleteOneImage() {
        int currentIndex = mViewPager.getCurrentItem();
        contentFragment.remove(currentIndex);
        mImages.remove(currentIndex);
        if (contentFragment.size() == 0) {
            finish();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void registerViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (mTendIntent != null) {
            mImages = mTendIntent.getParcelableArrayListExtra("edit_images");
            mCurrentImageIndex = mTendIntent.getIntExtra("current_image_index", 0);
            mCanDelete = mTendIntent.getBooleanExtra("can_delete", false);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((mCurrentImageIndex + 1) + File.separator + mImages.size());
        contentFragment = new ArrayList();
        for (int i = 0; i < mImages.size(); i++) {
            contentFragment.add(new PictureFragment(mImages.get(i).data));
        }
        mAdapter = new ViewPagerAdapter(
                getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mPageListener);
        mViewPager.setCurrentItem(mCurrentImageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (PictureFragment f : contentFragment) {
            try {
                f.getBitmap().recycle();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("edit_images", mImages);
        setResult(RESULT_OK, intent);
//        super.onBackPressed();
        finish();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private int mCount = 0;
        private int destroyPosition = 0;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.mCount = count;
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= contentFragment.size()) {
                return null;
            }
            return contentFragment.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return contentFragment.size();
        }

        @Override
        public void destroyItem(View collection, int position, Object o) {
            View view = (View) o;
            destroyPosition = position;
            ((ViewPager) collection).removeView(view);
            view = null;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            getSupportActionBar().setTitle((mViewPager.getCurrentItem() + 1) + File.separator + mImages.size());
        }
    }
}
