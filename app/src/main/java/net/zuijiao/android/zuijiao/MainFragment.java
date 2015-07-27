package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
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

import com.zuijiao.utils.PageTransformerUtil;
import com.zuijiao.view.PagerSlidingTab;

/**
 * display the common gourmet and banquet list ,has two sub-fragment ;
 */
@SuppressLint("ValidFragment")
public class MainFragment extends Fragment {
    private ViewPager mViewPager = null;
    private PagerSlidingTab mTabs = null;
    private MainPagerAdapter mPagerAdapter = null;
    private GourmetDisplayFragment gourmetFragment;
    private BanquetDisplayFragment banquetFragment;
    private Context mContext;




    public static MainFragment getInstance(){
        MainFragment fragment = new MainFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("position" ,position );
//        fragment.setArguments(bundle);
        return fragment;
    }
    /**
     * init two sub-fragment on create ;
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification, null);
        mContext = getActivity() ;
        mViewPager = (ViewPager) contentView.findViewById(R.id.notification_view_pager);
        if (mPagerAdapter == null) {
            FragmentManager manager = getChildFragmentManager();
            mPagerAdapter = new MainPagerAdapter(manager);
        }
        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
//
//            @Override
//            public void transformPage(View page, float position) {
//                PageTransformerUtil.zoomOutPageTransformer(page, position);
//            }
//        });
        mTabs = (PagerSlidingTab) contentView.findViewById(R.id.notification_tabs);
        initTabsValue();
        mTabs.setViewPager(mViewPager);
        return contentView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

    /**
     * judge if the float button of gourmet fragment is opened ,
     * if yes ,close up and expend back event
     *
     * @return
     */
    public boolean onBackPressed() {
        if (mViewPager.getCurrentItem() == 1 && gourmetFragment.holdBackEvent()) {
            gourmetFragment.closeFloatMenu();
            return true;
        }
        return false;
    }

    /**
     * view pager adapter
     */
    public class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.gourmet);
            } else if (position == 0) {
                return getString(R.string.banquet);
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
                if (banquetFragment == null)
                    banquetFragment =  BanquetDisplayFragment.newInstance();
                return banquetFragment;

            } else {
                if (gourmetFragment == null)
                    gourmetFragment =  GourmetDisplayFragment.newInstance(GourmetDisplayFragment.MAIN_PAGE);
                return gourmetFragment;
            }
        }
    }


    public void refreshBanquetList() {
        try {
            banquetFragment.onRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
