package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;

/**
 * Created by xiaqibo on 2015/6/16.
 */
@SuppressLint("ValidFragment")
public class MyOrderFragment extends Fragment {

    private ViewPager mViewPager = null;
    private PagerSlidingTab mTabs = null;
    private OrderListFragment mComingOrderFragment;
    private OrderListFragment mGoneOrderFragment;
    private OrderListFragment mWholeOrderFragment;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private MainPagerAdapter mPagerAdapter = null;
    private Context mContext;

    public MyOrderFragment() {
        super();
    }

    public MyOrderFragment(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification, null);
        mViewPager = (ViewPager) contentView.findViewById(R.id.notification_view_pager);
        if (mPagerAdapter == null) {
//            FragmentManager manager = getActivity().getSupportFragmentManager() ;
            mPagerAdapter = new MainPagerAdapter(getChildFragmentManager());
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


    public class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.coming_soon);
            } else if (position == 1) {
                return getString(R.string.leaving_for_evaluated);
            } else {
                return getString(R.string.whole);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position == 0) {
                if (mComingOrderFragment == null)
                    mComingOrderFragment = new OrderListFragment(position);
                return mComingOrderFragment;
            } else if (position == 1) {
                if (mGoneOrderFragment == null)
                    mGoneOrderFragment = new OrderListFragment(position);
                return mGoneOrderFragment;
            } else if (position == 2) {
                if (mWholeOrderFragment == null)
                    mWholeOrderFragment = new OrderListFragment(position);
                return mWholeOrderFragment;
            } else {
//                if(mWholeOrderFragment == null)
//                    mWholeOrderFragment = new OrderListFragment(position ) ;
                return new OrderListFragment(position);
            }
        }
    }

}
