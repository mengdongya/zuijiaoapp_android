package net.zuijiao.android.zuijiao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.view.PagerSlidingTab;

/**
 * Created by xiaqibo on 2015/4/21.
 */
@ContentView(R.layout.activity_friend)
public class FriendActivity extends BaseActivity {
    @ViewInject(R.id.friend_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.friend_tabs)
    private PagerSlidingTab mTabs = null;
    @ViewInject(R.id.friend_view_pager)
    private ViewPager mViewPager = null;
    private int mFirstShow = 0;
    private int mFollowCount = 10;
    private int mFansCount = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    protected void registerViews() {
        if (mTendIntent != null) {
            mFirstShow = mTendIntent.getIntExtra("friend_index", 0);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager.setAdapter(new FriendPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mFirstShow);
        mTabs.setViewPager(mViewPager);
        initTabsValue();
    }


    @Deprecated
    protected void findViews() {

    }

    private void initTabsValue() {
        //cursor color
        mTabs.setIndicatorColor(Color.parseColor("#a72232"));
        //tab divider color
        mTabs.setDividerColor(Color.parseColor("#373737"));
        //tab background
        mTabs.setBackgroundColor(Color.parseColor("#373737"));
        //tab bottom height
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        //cursor height
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics()));
        //selected tab text color
        mTabs.setSelectedTextColor(Color.parseColor("#eeeeee"));
        //unselected tab text color
        mTabs.setTextColor(Color.parseColor("#eeeeee"));
    }

    public class FriendPagerAdapter extends FragmentPagerAdapter {


        public FriendPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return String.format(getString(R.string.follow_count), mFollowCount);
            } else if (position == 1) {
                return String.format(getString(R.string.fans_count), mFansCount);
            }
            return "!";
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return FriendFragment.newInstance(position);
        }

    }
}
