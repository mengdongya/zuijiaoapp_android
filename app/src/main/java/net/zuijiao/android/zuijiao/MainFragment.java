package net.zuijiao.android.zuijiao;

import android.content.Context;
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


public class MainFragment extends Fragment {
    private ViewPager mViewPager = null;
    private PagerSlidingTab mTabs = null;
    private MainPagerAdapter mPagerAdapter = null;
    private GourmetDisplayFragment gourmetFragment;
    private BanquetDisplayFragment banquetFragment;
    private Context mContext;

    public MainFragment() {
        super();
    }

    public MainFragment(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification, null);
        mViewPager = (ViewPager) contentView.findViewById(R.id.notification_view_pager);

        if (mPagerAdapter == null) {
            FragmentManager manager = getChildFragmentManager();
            mPagerAdapter = new MainPagerAdapter(manager);
        }
        mViewPager.setAdapter(mPagerAdapter);
        mTabs = (PagerSlidingTab) contentView.findViewById(R.id.notification_tabs);
        initTabsValue();
        mTabs.setViewPager(mViewPager);
        return contentView;
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
        mTabs.setTextColor(Color.parseColor("#eeeeee"));
    }

    public boolean onBackPressed() {
        if (mViewPager.getCurrentItem() == 0 && gourmetFragment.holdBackEvent()) {
            gourmetFragment.closeFloatMenu();
            return true;
        }
        return false;
    }


    public class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.banquet);
            } else if (position == 0) {
                return getString(R.string.gourmet);
            }
            return "!";
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (gourmetFragment == null)
                    gourmetFragment = new GourmetDisplayFragment(GourmetDisplayFragment.MAIN_PAGE, mContext);
                return gourmetFragment;
            } else {
                if (banquetFragment == null)
                    banquetFragment = new BanquetDisplayFragment();
                return banquetFragment;
            }
        }
    }


}
