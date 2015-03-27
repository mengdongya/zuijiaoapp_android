package net.zuijiao.android.zuijiao;

import java.util.ArrayList;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.WindowManager;

@ContentView(R.layout.activity_wizard)
public class WizardActivity extends FragmentActivity {
	private WelcomeFragment mFragment1;
	private WelcomeFragment mFragment2;
	private WelcomeFragment mFragment3;
	private ArrayList<Fragment> mFragmentList;
	@ViewInject(R.id.wizard_container)
	private ViewPager mViewPager = null;
	private FragmentManager mFragmentMng = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		com.lidroid.xutils.ViewUtils.inject(this);
		registeViews();
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	private void registeViews() {
		mFragmentList = new ArrayList<Fragment>();
		mFragment1 = new WelcomeFragment(1);
		mFragmentList.add(mFragment1);
		mFragment2 = new WelcomeFragment(2);
		mFragmentList.add(mFragment2);
		mFragment3 = new WelcomeFragment(3);
		mFragmentList.add(mFragment3);
		mFragmentMng = getSupportFragmentManager();
		mViewPager.setAdapter(mPagerAdapter);
	}

	private FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(
			mFragmentMng) {
		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return mFragmentList.get(arg0);
		}

		public Object instantiateItem(android.view.ViewGroup container,
				int position) {
			Fragment fragment = mFragmentList.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = mFragmentMng.beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				mFragmentMng.executePendingTransactions();
			}


			return fragment.getView();
		};

	};

}
