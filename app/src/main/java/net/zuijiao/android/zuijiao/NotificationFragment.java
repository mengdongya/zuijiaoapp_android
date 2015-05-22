package net.zuijiao.android.zuijiao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zuijiao.view.PagerSlidingTab;

/**
 * Created by xiaqibo on 2015/5/5.
 */
public class NotificationFragment extends Fragment implements MessageFragment.OnMessageFetch {
    private ViewPager mViewPager = null;
    private PagerSlidingTab mTabs = null;
    private MessageFragment mMsgFragment = null;
    private MessageFragment mNotifyFragment = null;
    private FriendPagerAdapter mPagerAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification, null);
        mViewPager = (ViewPager) contentView.findViewById(R.id.notification_view_pager);
        if (mPagerAdapter == null) {
            mPagerAdapter = new FriendPagerAdapter(getActivity().getSupportFragmentManager());
        }
        mViewPager.setAdapter(mPagerAdapter);
        mTabs = (PagerSlidingTab) contentView.findViewById(R.id.notification_tabs);
        initTabsValue();
        mTabs.setViewPager(mViewPager);
        return contentView;
    }

    public void clearMessage() {
        try {
            mMsgFragment.clearMessage();
            mNotifyFragment.clearMessage();
        } catch (Throwable t) {
            System.err.println("no message");
            ;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initTabsValue() {
        //cursor color
        mTabs.setIndicatorColor(Color.WHITE);
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

    public void setAllRead() {
        messageRead();
        notificationRead();
    }

    public void messageRead() {
        if (mMsgFragment != null) {
            mMsgFragment.markRead();
        }
    }

    public void notificationRead() {
        if (mNotifyFragment != null) {
            mNotifyFragment.markRead();
        }
    }

    public class FriendPagerAdapter extends FragmentPagerAdapter {


        public FriendPagerAdapter(FragmentManager fm) {
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
                mNotifyFragment = new MessageFragment(getActivity().getApplicationContext(), NotificationFragment.this, 0);
            }
            if (mMsgFragment == null) {
                mMsgFragment = new MessageFragment(getActivity().getApplicationContext(), NotificationFragment.this, 1);
            }
            if (position == 0) {
                return mNotifyFragment;
            } else if (position == 1) {
                return mMsgFragment;
            } else {
                return new MessageFragment(getActivity().getApplicationContext(), NotificationFragment.this);
            }
//            return FriendFragment.newInstance(position);
        }
    }


    @Override
    public void onFetch() {
        mTabs.setTabText(0, String.format(getString(R.string.notification_with_count), mNotifyFragment.getSize()));
        mTabs.setTabText(1, String.format(getString(R.string.comment_with_count), mMsgFragment.getSize()));
    }
}
