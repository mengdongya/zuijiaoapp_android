package net.zuijiao.android.zuijiao;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.view.BanquetDetailScrollView;

import java.util.ArrayList;

/**
 * Created by xiaqibo on 2015/6/10.
 */
@ContentView(R.layout.activity_banquet_detail)
public class BanquetDetailActivity extends BaseActivity implements BanquetDetailScrollView.ScrollStateChangeListener, View.OnClickListener {
    @ViewInject(R.id.banquet_detail_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.banquet_detail_bottom)
    private View mBottomView = null;
    @ViewInject(R.id.banquet_detail_division)
    private View mBottomDivisionView;
    @ViewInject(R.id.banquet_detail_scroll_view)
    private BanquetDetailScrollView mScrollView = null;
    @ViewInject(R.id.banquet_detail_images)
    private ViewPager mImagePages = null;
    @ViewInject(R.id.banquet_detail_images_index)
    private TextView mImagesIndex = null;
    @ViewInject(R.id.banquet_detail_banquet_title)
    private TextView mBanquetTitle;
    @ViewInject(R.id.banquet_detail_banquet_description)
    private TextView mBanquetDescription;
    @ViewInject(R.id.banquet_detail_host_head)
    private ImageView mHostHead;
    @ViewInject(R.id.banquet_detail_host_name)
    private TextView mHostName;
    @ViewInject(R.id.banquet_detail_star_container)
    private LinearLayout mStarContainer;
    @ViewInject(R.id.banquet_detail_host_score)
    private TextView mHostScore;
    @ViewInject(R.id.banquet_detail_about_host)
    private Button mAboutHostBtn = null;
    @ViewInject(R.id.banquet_detail_menu_title)
    private TextView mMenuTitle;
    @ViewInject(R.id.banquet_detail_menu_content)
    private TextView mMenuContent;
    @ViewInject(R.id.banquet_detail_chara_title)
    private TextView mCharaTitle;
    @ViewInject(R.id.banquet_detail_chara_content)
    private TextView mCharaContent;
    @ViewInject(R.id.banquet_detail_instruction_title)
    private TextView mInstructTitle;
    @ViewInject(R.id.banquet_detail_location_text)
    private TextView mInstructPosition;
    @ViewInject(R.id.banquet_detail_instruction_content_date)
    private TextView mInstructDate;
    @ViewInject(R.id.banquet_detail_instruction_content_price)
    private RelativeLayout mInstructPrice;
    @ViewInject(R.id.banquet_detail_instruction_content_requirement)
    private TextView mInstructRequirement;
    @ViewInject(R.id.banquet_detail_instruct_status)
    private TextView mInstructStatus;
    @ViewInject(R.id.banquet_detail_ordered_person)
    private GridView mOrderedPersonShow;
    @ViewInject(R.id.banquet_detail_review_title)
    private TextView mReviewTitle;
    @ViewInject(R.id.banquet_detail_lastest_comment)
    private View mLastComment;
    @ViewInject(R.id.banquet_detail_comment_btn)
    private Button mAllCommentBtn;
    @ViewInject(R.id.banquet_detail_bottom_price)
    private TextView mBottomPrice;
    @ViewInject(R.id.banquet_detail_bottom_date)
    private TextView mBottomDate;
    @ViewInject(R.id.banquet_detail_bottom_order)
    private Button mOrderBtn;

    private ImageViewPagerAdapter mViewPagerAdapter = null;
    private TranslateAnimation hideToolbarAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
    private TranslateAnimation showToolbarAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    private TranslateAnimation hideBottomViewAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
    private TranslateAnimation showBottomViewAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mScrollView.setScrollStateListener(this);
        mViewPagerAdapter = new ImageViewPagerAdapter(initImages());
        mImagePages.setAdapter(mViewPagerAdapter);
        mImagePages.setOnPageChangeListener(mPageListener);
        mAllCommentBtn.setOnClickListener(this);
        mAboutHostBtn.setOnClickListener(this);
        mOrderBtn.setOnClickListener(this);
        mHostHead.setOnClickListener(this);
        mOrderedPersonShow.setAdapter(mGridAdapter);
        setListViewHeightBasedOnChildren(mOrderedPersonShow);
    }

    private ArrayList<ImageView> initImages() {
        ArrayList<ImageView> mImageList = new ArrayList<>();
        final ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("http://d.hiphotos.baidu.com/image/pic/item/b64543a98226cffc2d1e40b8ba014a90f603ea80.jpg");
        imageUrls.add("http://t10.baidu.com/it/u=4286244909,263883255&fm=32&s=5A35894461A38EA4FAE4159603008089&w=533&h=800&img.JPEG");
        imageUrls.add("http://t12.baidu.com/it/u=3975863465,10899649&fm=32&s=5160317707314388464C68EB0300E032&w=533&h=800&img.JPEG");
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                if (gourmet.getImageURLs().isEmpty()) {
//                    return;
//                }
                    Intent intent = new Intent(BanquetDetailActivity.this, BigImageActivity.class);
                    int currentImageIndex = mImagePages.getCurrentItem();
                    intent.putExtra("current_image_index", currentImageIndex);
                    intent.putStringArrayListExtra("cloud_images", imageUrls);
                    startActivity(intent);
                }
            });
            Picasso.with(getApplicationContext())
                    .load(imageUrls.get(i))
                    .placeholder(R.drawable.empty_view_greeting)
                    .error(R.drawable.empty_view_greeting)
                    .into(image);
            mImageList.add(image);
        }
        return mImageList;
    }

    @Override
    public void onScroll(int gapScrollY) {
        Log.i("gapScrollY", "gapScrollY == " + gapScrollY);
        if (gapScrollY > 0) {
            showToolbarAndBottomView();
        } else {
            hideToolbarAndBottomView();
        }
    }

    private void hideToolbarAndBottomView() {
        if (mToolbar.getVisibility() == View.GONE
                && mBottomView.getVisibility() == View.GONE)
            return;
        mBottomDivisionView.setVisibility(View.GONE);
        hideToolbarAnim.setDuration(500);
        mToolbar.startAnimation(hideToolbarAnim);
        hideBottomViewAnim.setDuration(500);
        mBottomView.startAnimation(hideBottomViewAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.GONE);
                mBottomView.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void showToolbarAndBottomView() {
        if (mToolbar.getVisibility() == View.VISIBLE
                && mBottomView.getVisibility() == View.VISIBLE)
            return;
        mBottomDivisionView.setVisibility(View.VISIBLE);
        showBottomViewAnim.setDuration(500);
        showToolbarAnim.setDuration(500);
        mToolbar.startAnimation(showToolbarAnim);
        mBottomView.startAnimation(showBottomViewAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.VISIBLE);
                mBottomView.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            mImagesIndex.setText((arg0 + 1) + "/" + mViewPagerAdapter.getCount());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private BaseAdapter mGridAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 26;
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
            return LayoutInflater.from(mContext).inflate(R.layout.user_info_favor_item, null);
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.banquet_detail_comment_btn:
                intent.setClass(mContext, BanquetCommentActivity.class);
                break;
            case R.id.banquet_detail_host_head:
            case R.id.banquet_detail_about_host:
                intent.setClass(mContext, HostAndGuestActivity.class);
                break;
            case R.id.banquet_detail_bottom_order:
                intent.setClass(mContext, BanquetOrderActivity.class);
                break;
        }
        startActivity(intent);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setListViewHeightBasedOnChildren(GridView gdView) {
        ListAdapter listAdapter = gdView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i += 5) {
            View listItem = listAdapter.getView(i, null, gdView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gdView.getLayoutParams();
        params.height = totalHeight
                + (gdView.getVerticalSpacing() * (listAdapter.getCount() / 5));

    }
}
