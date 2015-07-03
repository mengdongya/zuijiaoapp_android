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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentCapacity;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentStatus;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.view.BanquetDetailScrollView;

import java.util.ArrayList;
import java.util.Date;

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
    @ViewInject(R.id.banquet_detail_date_text)
    private TextView mInstructDate;
    @ViewInject(R.id.banquet_detail_price_text)
    private TextView mInstructPrice;
    //    @ViewInject(R.id.banquet_detail_instruction_content_price)
//    private RelativeLayout mInstructPrice;
    @ViewInject(R.id.banquet_detail_request_text)
    private TextView mInstructRequirement;
    @ViewInject(R.id.banquet_detail_instruct_status)
    private TextView mInstructStatus;
    @ViewInject(R.id.banquet_detail_ordered_person)
    private GridView mOrderedPersonShow;
    @ViewInject(R.id.banquet_detail_review_title)
    private TextView mReviewTitle;
    //    @ViewInject(R.id.banquet_detail_lastest_comment)
//    private View mLastComment;
    @ViewInject(R.id.banquet_detail_comment_btn)
    private Button mAllCommentBtn;
    @ViewInject(R.id.banquet_detail_bottom_price)
    private TextView mBottomPrice;
    @ViewInject(R.id.banquet_detail_bottom_date)
    private TextView mBottomDate;
    @ViewInject(R.id.banquet_detail_bottom_order)
    private Button mOrderBtn;
    @ViewInject(R.id.banquet_detail_menu_container)
    private View mMenuContainer;
    @ViewInject(R.id.banquet_detail_char_container)
    private View mCharactContainer;
    @ViewInject(R.id.banquet_detail_finished)
    private TextView mFinishText;
    @ViewInject(R.id.banquet_detail_bottom_can_order)
    private View mBottomOrderView;
    private Banquent mBanquent;
    private String[] weekDays;

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
    private AdapterView.OnItemClickListener mGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mContext, HostAndGuestActivity.class);
            intent.putExtra("attendee_id", mBanquent.getAttendees().get(position).getIdentifier());
            intent.putExtra("b_host", false);
            startActivity(intent);
        }
    };
    private BaseAdapter mGridAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (mBanquent.getAttendees() != null)
                return mBanquent.getAttendees().size();
            return 0;
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
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.user_info_favor_item, null);
            TinyUser user = mBanquent.getAttendees().get(position);
            ImageView userHead = (ImageView) contentView.findViewById(R.id.user_info_favor_item_image);
            TextView userName = (TextView) contentView.findViewById(R.id.user_info_favor_item_text);
            Picasso.with(mContext).load(user.getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(userHead);
//            userName.setText(user.getNickName());
            userName.setVisibility(View.GONE);
            return contentView;
        }
    };

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mTendIntent != null) {
            mBanquent = (Banquent) mTendIntent.getSerializableExtra("banquet");
        }
        if (mBanquent == null) {
            finish();
            return;
        }
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        mScrollView.setScrollStateListener(this);
        mViewPagerAdapter = new ImageViewPagerAdapter(initImages());
        mImagePages.setAdapter(mViewPagerAdapter);
        mImagePages.setOnPageChangeListener(mPageListener);
        mImagesIndex.setText(1 + "/" + mViewPagerAdapter.getCount());
        initViewsByBanquet();
        mAllCommentBtn.setOnClickListener(this);
        mAboutHostBtn.setOnClickListener(this);
        mOrderBtn.setOnClickListener(this);
        mHostHead.setOnClickListener(this);
    }

    private void initViewsByBanquet() {
        mBanquetTitle.setText(mBanquent.getTitle());
        mBanquetDescription.setText(mBanquent.getDesc());
        if (mBanquent.getMaster().getAvatarURL().isPresent())
            Picasso.with(mContext).load(mBanquent.getMaster().getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(mHostHead);
        mHostName.setText(mBanquent.getMaster().getNickName());
        String menu = formatMenuContent();
        if (menu.equals("")) {
            mMenuContainer.setVisibility(View.GONE);
        } else
            mMenuContent.setText(menu);
        String characteristic = mBanquent.getCharacteristic();
        if (characteristic == null || characteristic.equals("")) {
            mCharactContainer.setVisibility(View.GONE);
        } else
            mCharaContent.setText(characteristic);
        String address = mBanquent.getAddress();
        if (address == null || address.equals("")) {
            findViewById(R.id.banquet_detail_instruction_content_position).setVisibility(View.GONE);
        } else
            mInstructPosition.setText(address);
        if (mBanquent.getTime() == null) {
            findViewById(R.id.banquet_detail_instruction_content_date).setVisibility(View.GONE);
        } else
            mInstructDate.setText(formatDate(mBanquent.getTime()));
        if (mBanquent.getPrice() == null) {
            findViewById(R.id.banquet_detail_instruction_content_price).setVisibility(View.GONE);
        } else
            mInstructPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
        String requirement = mBanquent.getRequirement();
        if (requirement == null || requirement.equals("")) {
            findViewById(R.id.banquet_detail_instruction_content_requirement).setVisibility(View.GONE);
        }
        mInstructRequirement.setText(mBanquent.getRequirement());
        BanquentCapacity banquentCapacity = mBanquent.getBanquentCapacity();
        if (banquentCapacity.getMax() == banquentCapacity.getMin()) {
            mInstructStatus.setText(String.format(getString(R.string.banquent_capacity_simple),
                    banquentCapacity.getMin(),
                    banquentCapacity.getCount()));
        } else {
            mInstructStatus.setText(String.format(getString(R.string.banquent_capacity_muilt),
                    banquentCapacity.getMin(),
                    banquentCapacity.getMax(),
                    banquentCapacity.getCount()));
        }
        mOrderedPersonShow.setAdapter(mGridAdapter);
        mOrderedPersonShow.setOnItemClickListener(mGridListener);
        setListViewHeightBasedOnChildren(mOrderedPersonShow);
        switch (BanquentStatus.fromString(mBanquent.getStatus())) {
            case Selling:

                break;
            case SoldOut:
//                mBottomOrderView.setVisibility(View.GONE);
//                mFinishText.setVisibility(View.VISIBLE);
//                mFinishText.setText(getString(R.string.banquet_status_sold_out));
                mOrderBtn.setText(getString(R.string.banquet_status_sold_out));
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                mOrderBtn.setEnabled(false);
                break;
            case OverTime:
//                mBottomOrderView.setVisibility(View.GONE);
//                mFinishText.setVisibility(View.VISIBLE);
//                mFinishText.setText(getString(R.string.banquet_status_over_time));
                mOrderBtn.setText(getString(R.string.banquet_status_over_time));
                mOrderBtn.setEnabled(false);
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                break;
            case End:
//                mBottomOrderView.setVisibility(View.GONE);
//                mFinishText.setVisibility(View.VISIBLE);
//                mFinishText.setText(getString(R.string.banquet_status_end));
                mOrderBtn.setText(getString(R.string.banquet_status_end));
                mOrderBtn.setEnabled(false);
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                break;
        }
        mBottomPrice.setText(String.valueOf(mBanquent.getPrice()));
//                mBottomPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
        mBottomDate.setText(formatDate(mBanquent.getTime()));
    }

    private ArrayList<ImageView> initImages() {
        ArrayList<ImageView> mImageList = new ArrayList<>();
        if (mBanquent.getImageUrls() == null)
            return null;
        for (String imageUrl : mBanquent.getImageUrls()) {
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
                    intent.putStringArrayListExtra("cloud_images", mBanquent.getImageUrls());
                    startActivity(intent);
                }
            });
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
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

    private String formatMenuContent() {
        if (mBanquent.getMenu() == null || mBanquent.getMenu().size() == 0) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder();
        for (String menuItem : mBanquent.getMenu()) {
            strBuilder.append(menuItem);
            if (mBanquent.getMenu().indexOf(menuItem) != mBanquent.getMenu().size() - 1)
                strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

    private void hideToolbarAndBottomView() {
        if (mToolbar.getVisibility() == View.GONE)
            return;
//        mBottomDivisionView.setVisibility(View.GONE);
        hideToolbarAnim.setDuration(500);
        mToolbar.startAnimation(hideToolbarAnim);
//        hideBottomViewAnim.setDuration(500);
//        mBottomView.startAnimation(hideBottomViewAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.GONE);
//                mBottomView.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void showToolbarAndBottomView() {
        if (mToolbar.getVisibility() == View.VISIBLE)
            return;
//        mBottomDivisionView.setVisibility(View.VISIBLE);
//        showBottomViewAnim.setDuration(500);
        showToolbarAnim.setDuration(500);
        mToolbar.startAnimation(showToolbarAnim);
//        mBottomView.startAnimation(showBottomViewAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.VISIBLE);
//                mBottomView.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.banquet_detail_comment_btn:
                intent.setClass(mContext, BanquetCommentActivity.class);
                startActivity(intent);
                break;
            case R.id.banquet_detail_host_head:
            case R.id.banquet_detail_about_host:
                intent.putExtra("b_host", true);
                intent.putExtra("attendee_id", mBanquent.getMaster().getIdentifier());
                intent.setClass(mContext, HostAndGuestActivity.class);
                startActivity(intent);
                break;
            case R.id.banquet_detail_bottom_order:
                if (!Router.getInstance().getCurrentUser().isPresent()) {
                    tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            if (Router.getInstance().getCurrentUser().isPresent()) {
                                goToOrder();
                            } else {
                                notifyLogin(null);
                            }
                        }
                    }, new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    goToOrder();
                }
                break;
        }
    }

    private void goToOrder() {
        Intent intent = new Intent();
        createDialog();
        Router.getAccountModule().fetchMyInfo(new OneParameterExpression<User>() {
            @Override
            public void action(User user) {
                String phoneNum = "";
                try {
                    phoneNum = user.getContactInfo().get().getPhoneNumber();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                intent.setClass(mContext, BanquetOrderActivity.class);
                intent.putExtra("banquet", mBanquent);
                if (phoneNum != null && !phoneNum.equals(""))
                    intent.putExtra("contact_phone_num", phoneNum);
                startActivity(intent);
                finalizeDialog();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                finalizeDialog();
            }
        });
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

    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(mContext.getString(R.string.month_day), date.getMonth() + 1, date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(String.format(mContext.getString(R.string.banquet_format_time), date.getHours(), date.getMinutes()));
        strBuilder.append(" ");
        return strBuilder.toString();
    }


}
