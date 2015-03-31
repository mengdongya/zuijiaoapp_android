package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
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

import java.util.ArrayList;
import java.util.List;

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
    @ViewInject(R.id.gv_favor_people)
    private GridView mGdView = null;
    @ViewInject(R.id.food_detail_layout_top)
    private View mLayoutTop = null;
    @ViewInject(R.id.food_detail_layout)
    private View mLayoutTopFloat = null;
    private ImageView mFavorBtn1 = null;
    private ImageView mFavorBtn2 = null;

    @ViewInject(R.id.et_comment)
    private EditText mEtComment = null;
    private boolean openEdit = false;
    private int toolbarHeight = 0;

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
        toolbarHeight = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mImageContainer.measure(width, viewPagerHeight);
        viewPagerHeight = mImageContainer.getMeasuredHeight();
        mToolbar.measure(width, toolbarHeight);
        toolbarHeight = mToolbar.getMeasuredHeight();
        mCommentList.setAdapter(mCommentAdapter);
        String[] test_label = getResources().getStringArray(R.array.test_label);
        for (int i = 0; i < test_label.length; i++) {
            TextView textview = new TextView(this);
            textview.setBackgroundResource(R.drawable.bg_label);
            textview.setTextSize(16);
            textview.setTextColor(getResources().getColor(R.color.main_label));
            textview.setTextSize(18);
            textview.setText(test_label[i]);
            mLabelContainer.addView(textview);
        }
        setListViewHeightBasedOnChildren(mCommentList);
        mScrollView.setOnScrollListener(this);
        mScrollView.setTopY(toolbarHeight);
        mScrollView.setBottomY(viewPagerHeight);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (openEdit) {
                            mScrollView.moveToTop();
                            openEdit = false;
                        }
                        onScroll(mScrollView.getScrollY());
                        onTopChange(mScrollView.getTop());
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
        mFavorBtn1 = (ImageView) mLayoutTop.findViewById(R.id.favor_btn);
        mFavorBtn2 = (ImageView) mLayoutTopFloat.findViewById(R.id.favor_btn);
        mFavorBtn1.setOnClickListener(favorListener);
        mFavorBtn2.setOnClickListener(favorListener);
        mEtComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openEdit = true;
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                int bottomY = mImageContainer.getHeight();
                mScrollView.setBottomY(bottomY);
            }
        }, 200);
        mGdView.setAdapter(mGdAdapter);
        mGdView.setOnItemClickListener(mGvListener);
        mCommentList.setOnItemClickListener(mCommentListListener);
    }
    private AdapterView.OnItemClickListener mCommentListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mEtComment.setFocusable(true);
            mEtComment.setFocusableInTouchMode(true);
            mEtComment.requestFocus();
            InputMethodManager inputManager =
                    (InputMethodManager)mEtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mEtComment, 0);
            openEdit = true ;
        }
    } ;
    private BaseAdapter mCommentAdapter = new BaseAdapter() {

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
    };
    private AdapterView.OnItemClickListener mGvListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), FavorPersonListActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mFloatView.getTop());
        mTopFloatView.layout(0, mBuyLayout2ParentTop, mTopFloatView.getWidth(),
                mBuyLayout2ParentTop + mTopFloatView.getHeight());
        Log.i("scrollerY", "scrollY == " + scrollY);
    }

    private OnClickListener favorListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (false) {
                View contentView = mInflater.inflate(R.layout.alert_login_dialog, null);
                TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
                final AlertDialog dialog = new AlertDialog.Builder(FoodDetailActivity.this).setView(contentView).create();
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
                dialog.show();
            } else {
                View view = mInflater.inflate(R.layout.favor_feedback, null);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getHeight();
        for (int i = 0; i <= listAdapter.getCount(); i++) {
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
            mToolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        } else {
            mToolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_gradient));
        }
    }


    private BaseAdapter mGdAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == mGdView.getNumColumns() - 1) {
                View contentView = mInflater.inflate(R.layout.food_detail_favor_item2, null);
                TextView tv = (TextView) contentView.findViewById(R.id.favor_people_count);
                tv.setText("6");
                return contentView;
            } else {
                return mInflater.inflate(R.layout.food_detail_favor_item, null);
            }
        }
    };
}
