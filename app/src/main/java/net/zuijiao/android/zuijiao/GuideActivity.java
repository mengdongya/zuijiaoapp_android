package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_guide)
public class GuideActivity extends BaseActivity implements OnClickListener {
    @ViewInject(R.id.guide_viewpager)
    private ViewPager mPager;
    @ViewInject(R.id.guide_dots)
    private LinearLayout mDotsLayout;
    @ViewInject(R.id.guide_btn)
    private Button mBtn;

    private List<View> viewList;
    private int[] indexIcon = {R.drawable.welcome1,
            R.drawable.wizard_index1, R.drawable.wizard_index2,
            R.drawable.wizard_index3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    private void initPager() {
        viewList = new ArrayList<View>();
        int[] images = new int[]{R.drawable.welcome1, R.drawable.welcome2,
                R.drawable.welcome3};
        String[] textHead = getResources().getStringArray(R.array.welcome_text1) ;
        String[] textNotes = getResources().getStringArray(R.array.welcome_text2) ;
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
        mDotsLayout.getChildAt(0).setBackgroundResource(R.drawable.wizard_index_selected);
        mDotsLayout.getChildAt(1).setBackgroundResource(R.drawable.wizard_index_unselected);
        mDotsLayout.getChildAt(2).setBackgroundResource(R.drawable.wizard_index_unselected);
    }

    private View initDot() {

        ImageView dot = (ImageView) LayoutInflater.from(
                getApplicationContext()).inflate(R.layout.layout_dot, null);
        return dot;
    }

    private View initView(int res, String head, String note) {
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
        initPager();
        mPager.setAdapter(new ViewPagerAdapter(viewList));
        mPager.setOnPageChangeListener(mPageListener);

        mBtn.setOnClickListener(this);
    }

    private OnPageChangeListener mPageListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                if (i == arg0) {
                    mDotsLayout.getChildAt(i).setBackgroundResource(
                            R.drawable.wizard_index_selected);
                } else {
                    mDotsLayout.getChildAt(i).setBackgroundResource(
                            R.drawable.wizard_index_unselected);
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
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guide_btn:
                openHome();
                mPreferenceInfo.setAppFirstLaunch(false);
                mPreferMng.saveFirstLaunch();
                break;
            default:
        }
    }

}
