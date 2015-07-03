package net.zuijiao.android.zuijiao;

import android.graphics.Color;
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
 * Created by xiaqibo on 2015/6/9.
 */
@ContentView(R.layout.activity_message)
public class MessageActivity extends BaseActivity implements MessageFragment.OnMessageFetch {
    @ViewInject(R.id.message_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.message_tabs)
    private PagerSlidingTab mTabs = null;
    @ViewInject(R.id.message_view_pager)
    private ViewPager mViewPager = null;
    private MessageFragment mMsgFragment = null;
    private MessageFragment mNotifyFragment = null;
    private MessagePagerAdapter mPagerAdapter = null;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initTabsValue();
        mPagerAdapter = new MessagePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabs.setViewPager(mViewPager);
    }

    private void initTabsValue() {
        //cursor color
        mTabs.setIndicatorColor(Color.WHITE);
        //tab divider color
        mTabs.setDividerColor(getResources().getColor(R.color.toolbar));
        //tab background
//        mTabs.setBackgroundColor(Color.parseColor("#373737"));
        mTabs.setBackgroundColor(getResources().getColor(R.color.toolbar));
        //tab bottom height
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        //cursor height
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics()));
        //selected tab text color
        mTabs.setSelectedTextColor(Color.parseColor("#eeeeee"));
        //unselected tab text color
        mTabs.setTextColor(getResources().getColor(R.color.unselected_tab_text_color));
    }

    @Override
    public void onFetch(int tabIndex, int unReadCount) {

    }


    public class MessagePagerAdapter extends FragmentPagerAdapter {


        public MessagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.comment);
            } else if (position == 0) {
                return getString(R.string.notification);
            }
            return "!";
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (mNotifyFragment == null) {
                mNotifyFragment = new MessageFragment(mContext, MessageActivity.this, 0);
            }
            if (mMsgFragment == null) {
                mMsgFragment = new MessageFragment(mContext, MessageActivity.this, 1);
            }
            if (position == 0) {
                return mNotifyFragment;
            } else if (position == 1) {
                return mMsgFragment;
            } else {
                return new MessageFragment(mContext, MessageActivity.this);
            }
//            return FriendFragment.newInstance(position);
        }
    }
}
