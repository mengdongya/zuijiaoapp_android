package net.zuijiao.android.zuijiao;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.view.MyScrollView;
import com.zuijiao.view.MyScrollView.OnScrollListener;
import com.zuijiao.view.WordWrapView;

@SuppressLint("ShowToast")
@ContentView(R.layout.activity_food_detail)
public class FoodDetailActivity extends BaseActivity implements
        OnScrollListener {
    @ViewInject(R.id.food_detail_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.food_detail_container)
    private MyScrollView mScrollView = null;
    @ViewInject(R.id.food_detail_layout)
    private LinearLayout mFloatView;
    @ViewInject(R.id.food_detail_layout_top)
    private LinearLayout mTopFloatView;
    @ViewInject(R.id.lv_food_detail_comment)
    private ListView mCommentList = null;
    private LayoutInflater mInflater = null;
    @ViewInject(R.id.food_detail_parent)
    private View rootView = null;
    @ViewInject(R.id.view_wordwrap)
    private WordWrapView mLabelContainer = null;
    @ViewInject(R.id.food_detail_images)
    private ViewPager mImagePager = null;
    @ViewInject(R.id.image_container)
    private FrameLayout mImageContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registeViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInflater = LayoutInflater.from(this);
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int viewPagerHeight = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.EXACTLY);
        int toolbarHeight = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mImageContainer.measure(width, viewPagerHeight);
        viewPagerHeight = mImageContainer.getMeasuredHeight();
        mToolbar.measure(width, toolbarHeight);
        toolbarHeight = mToolbar.getMeasuredHeight();
        mCommentList.setAdapter(new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mInflater.inflate(R.layout.comment_item, null);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
        String[] test_label = getResources().getStringArray(R.array.test_label) ;
        for (int i = 0; i < test_label.length; i++) {
            TextView textview = new TextView(this);
            textview.setBackgroundResource(R.drawable.bg_label);
            textview.setTextSize(16);
            textview.setTextColor(getResources().getColor(R.color.main_label));
            textview.setTextSize(18);
            textview.setText(test_label[i]);
            mLabelContainer .addView(textview);
        }
        setListViewHeightBasedOnChildren(mCommentList);
        mScrollView.setOnScrollListener(this);
        mScrollView.setTopY(toolbarHeight);
        mScrollView.setBottomY(viewPagerHeight);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        onScroll(mScrollView.getScrollY());
                    }
                });
        ArrayList<View> data = new ArrayList<View>();
        for (int i = 0; i < 3; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(R.drawable.empty_view_greeting);
            image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            image.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FoodDetailActivity.this, BigImageActivity.class);
                    startActivity(intent);
                }
            });
            data.add(image);
        }
        mImagePager.setAdapter(new ViewPagerAdapter(data));
        mImageContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(FoodDetailActivity.this, "!!!", Toast.LENGTH_SHORT).show();
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                int bottomY = mImageContainer.getHeight();
                mScrollView.setBottomY(bottomY);
            }
        }, 200);
    }

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mFloatView.getTop());
        mTopFloatView.layout(0, mBuyLayout2ParentTop, mTopFloatView.getWidth(),
                mBuyLayout2ParentTop + mTopFloatView.getHeight());
        Log.i("scrollerY", "scrollY == " + scrollY);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getHeight();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));

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
    public void onTopChange(int top) {
        if (top <= mToolbar.getHeight() + 20) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.bg_food_detai_top));
        } else {
            mToolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_gradient));
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item) ;
    }
}
