package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zuijiao.utils.PageTransformerUtil;
import com.zuijiao.view.PagerSlidingTab;

import java.util.ArrayList;

/**
 * display kinds status of my banquet order list ,
 * included in main activity
 * and called from side bar of main activity ;
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

    private static int needRefreshIndex = -1;

//    public MyOrderFragment() {
//        super();
//    }

    public static  MyOrderFragment getInstance(){
        MyOrderFragment fragment = new MyOrderFragment();
        Bundle bundle = new Bundle();
//        bundle.putInt("position" ,position );
        fragment.setArguments(bundle);
        return fragment;
    }

//    public MyOrderFragment(Context context) {
//        super();
//        this.mContext = context;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification, null);
        mViewPager = (ViewPager) contentView.findViewById(R.id.notification_view_pager);
        if (mPagerAdapter == null) {
//            FragmentManager manager = getActivity().getSupportFragmentManager() ;
            mPagerAdapter = new MainPagerAdapter(getChildFragmentManager());
        }
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {

            @Override
            public void transformPage(View page, float position) {
                PageTransformerUtil.zoomOutPageTransformer(page, position);
            }
        });
        mTabs = (PagerSlidingTab) contentView.findViewById(R.id.notification_tabs);
        initTabsValue();
        mTabs.setViewPager(mViewPager);
        mTabs.setOnPageChangeListener(onPageChangeListener);
        return contentView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (needRefreshIndex == position) {
                ((OrderListFragment) mPagerAdapter.getItem(needRefreshIndex)).onRefresh();
                needRefreshIndex = -1;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

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
        mTabs.setTextColor(getResources().getColor(R.color.unselected_tab_text_color));
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
                    mComingOrderFragment =  OrderListFragment.newInstance(position);
                return mComingOrderFragment;
            } else if (position == 1) {
                if (mGoneOrderFragment == null)
                    mGoneOrderFragment =   OrderListFragment.newInstance(position);
                return mGoneOrderFragment;
            } else if (position == 2) {
                if (mWholeOrderFragment == null)
                    mWholeOrderFragment =   OrderListFragment.newInstance(position);
                return mWholeOrderFragment;
            } else {
//                if(mWholeOrderFragment == null)
//                    mWholeOrderFragment = new OrderListFragment(position ) ;
                return   OrderListFragment.newInstance(position);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.COMMENT_SUCCESS) {
//            mGoneOrderFragment.onRefresh();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mWholeOrderFragment.onRefresh();
//                }
//            }, 200);
            ((OrderListFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).onRefresh();
            needRefreshIndex = 3 - mViewPager.getCurrentItem();
        }
    }
}
