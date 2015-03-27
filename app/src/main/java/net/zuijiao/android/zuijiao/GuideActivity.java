package net.zuijiao.android.zuijiao;

import java.util.ArrayList;
import java.util.List;




import com.zuijiao.controller.PreferenceManager;

import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuideActivity extends BaseActivity {

	private ViewPager mPager;
	private LinearLayout mDotsLayout;
	private Button mBtn;

	private List<View> viewList;
	private int[] indexIcon = { R.drawable.wizard_index0,
			R.drawable.wizard_index1, R.drawable.wizard_index2,
			R.drawable.wizard_index3 };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		mPager = (ViewPager) findViewById(R.id.guide_viewpager);
		mDotsLayout = (LinearLayout) findViewById(R.id.guide_dots);
		mBtn = (Button) findViewById(R.id.guide_btn);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		initPager();
		mPager.setAdapter(new ViewPagerAdapter(viewList));
		mPager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(GuideActivity.this, "!!!!!!!!", 1000).show();
			}
		});
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
					if (i == arg0) {
						mDotsLayout.getChildAt(i).setBackgroundResource(
								indexIcon[i + 1]);
						;
					} else {
						mDotsLayout.getChildAt(i).setBackgroundResource(
								indexIcon[0]);
						;
					}
				}
				if (arg0 == mDotsLayout.getChildCount() - 1) {
					mBtn.setVisibility(View.VISIBLE);
				} else {
					mBtn.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openHome();
				mPreferenceInfo.setAppFirstLaunch(false);
				mPreferMng.saveFirstLaunch() ;
			}
		});
	}

	private void initPager() {
		viewList = new ArrayList<View>();
		int[] images = new int[] { R.drawable.welcome1, R.drawable.welcome2,
				R.drawable.welcome3 };
		int[] textHead = new int[] { R.string.wizard_text1_head,
				R.string.wizard_text2_head, R.string.wizard_text3_head };
		int[] textNotes = new int[] { R.string.wizard_text1_note,
				R.string.wizard_text2_note, R.string.wizard_text3_note };
		for (int i = 0; i < images.length; i++) {
			viewList.add(initView(images[i], textHead[i], textNotes[i]));
		}
		initDots(images.length);
	}

	private void initDots(int count) {
		for (int j = 0; j < count; j++) {
			mDotsLayout.addView(initDot());
		}
		mDotsLayout.getChildAt(0).setSelected(true);
		mDotsLayout.getChildAt(0).setBackgroundResource(indexIcon[1]);
		;
		mDotsLayout.getChildAt(1).setBackgroundResource(indexIcon[0]);
		;
		mDotsLayout.getChildAt(2).setBackgroundResource(indexIcon[0]);
		;
	}

	private View initDot() {
		ImageButton dot = (ImageButton) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.layout_dot, null);
		return dot;
	}

	private View initView(int res, int head, int note) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.item_guide, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iguide_img);
		imageView.setImageResource(res);
		TextView headText = (TextView) view.findViewById(R.id.wizard_text1);
		headText.setText(head);
		TextView noteText = (TextView) view.findViewById(R.id.wizard_text2);
		noteText.setText(note);
		return view;
	}

	private void openHome() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}

	class ViewPagerAdapter extends PagerAdapter {

		private List<View> data;

		public ViewPagerAdapter(List<View> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(data.get(position));
			return data.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(data.get(position));
		}

	}

	@Override
	protected void findViews() {
		
	}

	@Override
	protected void registeViews() {
		
	}

}
