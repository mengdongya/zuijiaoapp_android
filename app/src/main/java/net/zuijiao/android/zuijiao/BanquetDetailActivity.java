package net.zuijiao.android.zuijiao;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentCapacity;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Review;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.utils.AdapterViewHeightCalculator;
import com.zuijiao.view.BanquetDetailScrollView;
import com.zuijiao.view.ReviewRatingBar;
import com.zuijiao.view.RoundImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/6/10.
 * display the detail information of the banquet ;
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
    @ViewInject(R.id.banquet_detail_request_text)
    private TextView mInstructRequirement;
    @ViewInject(R.id.banquet_detail_instruct_status)
    private TextView mInstructStatus;
    @ViewInject(R.id.banquet_detail_ordered_person)
    private GridView mOrderedPersonShow;
    @ViewInject(R.id.banquet_detail_review_container)
    private LinearLayout mReviewContainer;
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
    @ViewInject(R.id.banquet_detail_menu_container)
    private View mMenuContainer;
    @ViewInject(R.id.banquet_detail_char_container)
    private View mCharactContainer;
    @ViewInject(R.id.banquet_detail_finished)
    private TextView mFinishText;
    @ViewInject(R.id.banquet_detail_bottom_can_order)
    private View mBottomOrderView;
    @ViewInject(R.id.banquet_comment_rtatingbar)
    private ReviewRatingBar mCommentRatingbar;
    private Reviews mReviews;
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
            createDialog();
            Router.getAccountModule().banquetUserInfo(mBanquent.getAttendees().get(position).getIdentifier(), new OneParameterExpression<Attendee>() {
                @Override
                public void action(Attendee attendee) {
                    finalizeDialog();
                    Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("attendee_info", attendee);
                    mContext.startActivity(intent);
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    finalizeDialog();
                }
            });
        }
    };
    /**
     * display ordered user's avatar
     */
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
            if (user.getAvatarURLSmall().isPresent() && !user.getAvatarURLSmall().get().equals("http://pic.zuijiao.net")) {
                String url = user.getAvatarURLSmall().get();
                Picasso.with(mContext)
                        .load(user.getAvatarURLSmall().get())
                        .placeholder(R.drawable.default_user_head)
                        .fit()
                        .centerCrop()
                        .into(userHead);
            }
//            userName.setText(user.getNickName());
            userName.setVisibility(View.GONE);
            return contentView;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void registerViews() {
        Log.i("outofmemory ", "oncreate");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(((ActivityTask) getApplication()).getDefaultDisplayImageOptions()).memoryCacheExtraOptions(50, 50)
                .threadPoolSize(1).build();
        ImageLoader.getInstance().init(config);
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
        Router.getBanquentModule().commentsofBanquent(mBanquent.getMaster().getIdentifier(), null, 1, new OneParameterExpression<Reviews>() {
            @Override
            public void action(Reviews reviews) {
                mReviews = reviews;
                registerCommentView();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.get_history_list_failed), Toast.LENGTH_SHORT).show();
            }
        });
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBottomView.getLayoutParams();
        mBottomPrice.measure(0 , 0 );
        int priceHeight = mBottomPrice.getMeasuredHeight();
        mBottomDate.measure(0 , 0);
        int dateHeight = mBottomDate.getMeasuredHeight() ;
        int margin = (int) (3* getResources().getDimension(R.dimen.end_z));
        params.height = priceHeight + dateHeight + margin ;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
        layoutParams.bottomMargin = params.height ;
    }

    private void registerCommentView() {
        List<Review> reviewList = mReviews.getReviewList();
        if (reviewList != null && reviewList.size() != 0) {
            mReviewContainer.setVisibility(View.VISIBLE);
            mStarContainer.setVisibility(View.VISIBLE);
            if (mReviews.getTotalCount() > 1) {
                mAllCommentBtn.setVisibility(View.VISIBLE);
                mAllCommentBtn.setText(String.format(getString(R.string.all_comments), mReviews.getTotalCount()));
            } else {
                mAllCommentBtn.setVisibility(View.GONE);
            }
            Review review = reviewList.get(0);
//            mCommentRatingbar.setRating(4.5f);
            mCommentRatingbar.setRating(mBanquent.getMaster().getScore());
            mHostScore.setText("(" + mReviews.getTotalCount() + ")");
            ImageView head = (RoundImageView) mLastComment.findViewById(R.id.banquet_comment_item_head);
            ImageLoader.getInstance().displayImage(review.getReviewer().getAvatarURLSmall().get(), head);
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog();
                    Router.getAccountModule().banquetUserInfo(review.getReviewer().getIdentifier(), new OneParameterExpression<Attendee>() {
                        @Override
                        public void action(Attendee attendee) {
                            finalizeDialog();
                            Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("attendee_info", attendee);
                            mContext.startActivity(intent);
                        }
                    }, new OneParameterExpression<String>() {
                        @Override
                        public void action(String s) {
                            finalizeDialog();
                        }
                    });
                }
            });
            ((TextView) mLastComment.findViewById(R.id.banquet_comment_item_user_name)).setText(review.getReviewer().getNickName());
            ((TextView) mLastComment.findViewById(R.id.banquet_comment_item_issue)).setText(review.getEvent().getTitle() + " · " + formatDate(review.getEvent().getTime()));
            ((ReviewRatingBar) mLastComment.findViewById(R.id.banquet_comment_item_stars)).setRating(review.getScore());
            ((TextView) mLastComment.findViewById(R.id.banquet_comment_item_comment)).setText(review.getContent());
        } else {
            mReviewContainer.setVisibility(View.GONE);
            mStarContainer.setVisibility(View.GONE);
        }
    }

    private void initViewsByBanquet() {
        mBanquetTitle.setText(mBanquent.getTitle());
        mBanquetDescription.setText(mBanquent.getDesc());
        if (mBanquent.getMaster().getAvatarURLSmall().isPresent())
            Picasso.with(mContext)
                    .load(mBanquent.getMaster().getAvatarURLSmall().get())
                    .placeholder(R.drawable.default_user_head)
                    .fit()
                    .centerCrop()
                    .into(mHostHead);
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
        } else {
            String date = formatDate(mBanquent.getTime());
            String endDate = formatDate(mBanquent.getEndTime());
            if (mBanquent.getTime().getDay() == mBanquent.getEndTime().getDay()) {
                date = date + " ~ " + endDate.substring(endDate.length() - 8, endDate.length());
            } else {
                date = date + " ~ " + endDate;
            }
            mInstructDate.setText(date);
        }

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
        AdapterViewHeightCalculator.setGridViewHeightBasedOnChildren(mOrderedPersonShow);
        switch (BanquentStatus.fromString(mBanquent.getStatus())) {
            case Selling:

                break;
            case SoldOut:
                mOrderBtn.setText(getString(R.string.banquet_status_sold_out));
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                mOrderBtn.setEnabled(false);
                break;
            case OverTime:
                mOrderBtn.setText(getString(R.string.banquet_status_over_time));
                mOrderBtn.setEnabled(false);
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                break;
            case End:
                mOrderBtn.setText(getString(R.string.banquet_status_end));
                mOrderBtn.setEnabled(false);
                mOrderBtn.setTextColor(getResources().getColor(R.color.tv_light_gray));
                break;
        }
        mBottomPrice.setText(String.valueOf(mBanquent.getPrice()));
        mBottomDate.setText(formatDate(mBanquent.getTime()));
    }


    /**
     * init the banquet display images ,
     * which shown on top of the activity in a viewpager;
     *
     * @return image-view list
     */
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
                    .fit()
                    .centerCrop()
                    .into(image);
            mImageList.add(image);
        }
        return mImageList;
    }

    /**
     * listen to the scroll of the scroller view ;
     *
     * @param gapScrollY
     */
    @Override
    public void onScroll(int gapScrollY) {
        Log.i("gapScrollY", "gapScrollY == " + gapScrollY);
        if (gapScrollY > 0) {
            showToolbar();
        } else {
            hideToolbar();
        }
    }

    /**
     * form a list to a string ,split each item by \n
     *
     * @return formatted string
     */
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

    private void hideToolbar() {
        if (mToolbar.getVisibility() == View.GONE)
            return;
        hideToolbarAnim.setDuration(500);
        mToolbar.startAnimation(hideToolbarAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void showToolbar() {
        if (mToolbar.getVisibility() == View.VISIBLE)
            return;
        showToolbarAnim.setDuration(500);
        mToolbar.startAnimation(showToolbarAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.banquet_detail_comment_btn:
                intent.setClass(mContext, BanquetCommentActivity.class);
                intent.putExtra("host_id", mBanquent.getMaster().getIdentifier());
                intent.putExtra("totalCount", mReviews.getTotalCount());
                startActivity(intent);
                break;
            case R.id.banquet_detail_host_head:
            case R.id.banquet_detail_about_host:

                createDialog();
                Router.getAccountModule().banquetUserInfo(mBanquent.getMaster().getUserId(), new OneParameterExpression<Attendee>() {
                    @Override
                    public void action(Attendee attendee) {
                        finalizeDialog();
                        Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("attendee_info", attendee);
//                            intent.putExtra("b_host", true);
//                            intent.putExtra("attendee_id", banquent.getMaster().getUserId());
                        mContext.startActivity(intent);
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        finalizeDialog();
                    }
                });
//                intent.putExtra("b_host", true);
//                intent.putExtra("attendee_id", mBanquent.getMaster().getUserId());
//                intent.setClass(mContext, HostAndGuestActivity.class);
//                startActivity(intent);
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


    /**
     * before go to the order activity ,
     * fetch user info and check if telephone num is filled in ,
     * and used as default order connect way ;
     */
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
                intent.setClass(mContext, BanquetOrderCreateActivity.class);
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


    /**
     * show the date in the specified format
     *
     * @param date
     * @return formatted string
     */
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
