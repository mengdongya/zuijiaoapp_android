package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.view.MyViewPager;

import java.util.ArrayList;

@ContentView(R.layout.activity_big_image)
public class BigImageActivity extends BaseActivity {
	@ViewInject(R.id.big_image_container)
	private MyViewPager mViewPager = null;

	private ArrayList<Fragment> contentFragment = null;

	private int testPicIds[] = { R.drawable.detail_show_1,
			R.drawable.detail_show_2, R.drawable.detail_show_3, };
	private int nPicNum = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nPicNum = getIntent().getIntExtra("picnum", 0);
	}

	@Override
	protected void findViews() {

	}

	@Override
	protected void registeViews() {
		contentFragment = new ArrayList<Fragment>();
		for (int i = 0; i < nPicNum; i++) {
			contentFragment.add(new PictureFragment(testPicIds[i]));
		}
		ViewPagerAdapter adapter = new ViewPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(0);
	}

	private class ViewPagerAdapter extends FragmentStatePagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return contentFragment.get(position);
		}

		@Override
		public int getCount() {
			return 3;
		}

	}

}
