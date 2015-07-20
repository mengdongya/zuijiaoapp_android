package net.zuijiao.android.zuijiao;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Review;
import com.zuijiao.android.zuijiao.model.Banquent.Seller;
import com.zuijiao.android.zuijiao.model.common.Language;
import com.zuijiao.android.zuijiao.model.user.Profile;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.view.ReviewRatingBar;
import com.zuijiao.view.RoundImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/6/15.
 */
@ContentView(R.layout.activity_host_guest)
public class HostAndGuestActivity extends BaseActivity {
    @ViewInject(R.id.host_guest_tool_bar)
    private Toolbar mToolbar;

    @ViewInject(R.id.host_guest_host_image_container)
    private RelativeLayout mHostImageContainer;
    @ViewInject(R.id.host_guest_host_head)
    private ImageView mHostHead;
    @ViewInject(R.id.host_guest_host_images)
    private ViewPager mHostImages;
    @ViewInject(R.id.host_guest_images_index)
    private TextView mImageIndex;
    @ViewInject(R.id.host_guest_guest_head)
    private ImageView mGuestHead;
    @ViewInject(R.id.host_guest_attendee_name)
    private TextView mAttendeeName;
    @ViewInject(R.id.host_guest_attendee_info)
    private TextView mAttendeeInfo;
    @ViewInject(R.id.host_guest_personal_introduction_group)
    private View mIntroductionContainer;
    @ViewInject(R.id.host_guest_introduction_title)
    private TextView mAttendeeIntroductionTitle;
    @ViewInject(R.id.host_guest_introduction)
    private TextView mAttendeeIntroduction;
    @ViewInject(R.id.attendee_detail_cooking_item)
    private View mAttendeeCooking;
    @ViewInject(R.id.attendee_detail_skilled_item)
    private View mAttendeeSkilled;
    @ViewInject(R.id.attendee_detail_place_item)
    private View mAttendeePlace;
    @ViewInject(R.id.attendee_detail_address_item)
    private View mAttendeeAddress;
    @ViewInject(R.id.attendee_detail_career_item)
    private View mAttendeeCareer;
    @ViewInject(R.id.attendee_detail_language_item)
    private View mAttendeeLanguage;
    @ViewInject(R.id.attendee_detail_education_item)
    private View mAttendeeEducation;
    @ViewInject(R.id.attendee_detail_hobby_item)
    private View mAttendeeHobby;
    @ViewInject(R.id.host_history_title)
    private TextView mHostHold;
    @ViewInject(R.id.host_guest_history_title)
    private TextView mHostAttendee;
    @ViewInject(R.id.host_guest_review_container)
    private LinearLayout mReviewContainer;
    @ViewInject(R.id.host_guest_hold_btn)
    private Button mHoldAllBanquet;
    @ViewInject(R.id.host_guest_attendee_btn)
    private Button mAttendeeBanquet;
    @ViewInject(R.id.host_guest_comment_btn)
    private Button mAllComment;
//    @ViewInject(R.id.host_guest_history_list)
//    private ListView mHistoryList;

    @ViewInject(R.id.host_detail_group)
    private LinearLayout mhostMsg;
    @ViewInject(R.id.ll_host_comment_stars)
    private LinearLayout mCommentStars;
    @ViewInject(R.id.host_guest_lastest_comment)
    private RelativeLayout mLastestComment;
    @ViewInject(R.id.host_guest_review_title)
    private TextView mReviewTitle;
    @ViewInject(R.id.host_guest_empty_review_iv)
    private ImageView mEmptyIv;
    @ViewInject(R.id.host_comment_count)
    private TextView mCommentCount;
    @ViewInject(R.id.host_comment_rtatingbar)
    private ReviewRatingBar mCommentRatingbar;
    //        @ViewInject(R.id.host_comment_list)
////    private LinearLayout mHostCommentList;
    @ViewInject(R.id.ll_host_hold_banquet)
    private LinearLayout mHoldBanquet;
    //    @ViewInject(R.id.ll_host_attendee_banquet)
//    private LinearLayout llAttendeeBanquet;
    @ViewInject(R.id.host_guest_history_item_image_hold)
    private ImageView banquetImageHoldNull;
    @ViewInject(R.id.host_guest_history_item_image_attendee)
    private ImageView banquetImageAttendeeNull;

    @ViewInject(R.id.hold_banquet_item)
    private View holdBanquet;
    @ViewInject(R.id.attendee_banquet_item)
    private View attendeeBanquet;
//    @ViewInject(R.id.banquet_item_text)
//    private View holdAttendeeBanquet;

    private ArrayList<ImageView> mImageList = new ArrayList<>();
    private ImageViewPagerAdapter mViewPagerAdapter;
    private int mAttendeeId = -1;
//    private boolean bHost = false;
    private Attendee mAttendee;
    private String[] weekDays;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(((ActivityTask) getApplication()).getDefaultDisplayImageOptions()).memoryCacheExtraOptions(50, 50)
                .threadPoolSize(1).build();
        ImageLoader.getInstance().init(config);

        weekDays = mContext.getResources().getStringArray(R.array.week_days);
//        if (mTendIntent != null) {
////            bHost = mTendIntent.getBooleanExtra("b_host", false);
//            mAttendeeId = mTendIntent.getIntExtra("attendee_id", -1);
//        }
//        if (mAttendeeId == -1) {
//            finish();
//            return;
//        }
        ((TextView) mAttendeeCooking.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.cooking));
        ((TextView) mAttendeeSkilled.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.skilled));
        ((TextView) mAttendeePlace.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.place));
        ((TextView) mAttendeeAddress.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.address));
        ((TextView) mAttendeeCareer.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.industry));
        ((TextView) mAttendeeLanguage.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.my_language));
        ((TextView) mAttendeeEducation.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.education));
        ((TextView) mAttendeeHobby.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.interest_hobby));
        mHostImages.setOnPageChangeListener(mPageListener);
        mHoldAllBanquet.setOnClickListener(mHeadListener);
        mAttendeeBanquet.setOnClickListener(mHeadListener);
        mAllComment.setOnClickListener(mHeadListener);
        holdBanquet.setOnClickListener(mHeadListener);
        attendeeBanquet.setOnClickListener(mHeadListener);
        mCommentRatingbar.setStepSize(0.5f);
        mAttendee = (Attendee) mTendIntent.getSerializableExtra("attendee_info");
        if(mAttendee == null){
            networkStep();
        }else{
            //register common user info
            registerViewsByAttendee();
            //register last attend banquet begin
            Banquent lastAttendBanquet = mAttendee.getLastAttendEvent();

            if (lastAttendBanquet == null) {
                attendeeBanquet.setVisibility(View.GONE);
                banquetImageAttendeeNull.setVisibility(View.VISIBLE);
                mAttendeeBanquet.setVisibility(View.GONE);
            } else {
                attendeeBanquet.setVisibility(View.VISIBLE);
                banquetImageAttendeeNull.setVisibility(View.GONE);
                mAttendeeBanquet.setVisibility(View.VISIBLE);
                mHostAttendee.setText(String.format(getString(R.string.attended_banquet), mAttendee.getAttendCount()));
                Picasso.with(mContext).load(lastAttendBanquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into((ImageView) attendeeBanquet.findViewById(R.id.banquet_history_item_image1));
                setBanquetStatus(lastAttendBanquet,attendeeBanquet);
                ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_title1)).setText(lastAttendBanquet.getTitle());
                ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_date1)).setText(formatDate(lastAttendBanquet.getTime()));
                ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_price1)).setText(String.format(getString(R.string.price_per_one), lastAttendBanquet.getPrice()));
                ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_situation1)).setText(String.format(getString(R.string.total_attendee), lastAttendBanquet.getAttendees().size()));
            }
            //register last attend banquet end
            //register seller info begin ;
            Seller sellerInfo = mAttendee.getSellerInfo();
            if (sellerInfo == null) {
                getSupportActionBar().setTitle(getString(R.string.guest));
                mCommentStars.setVisibility(View.GONE);
                mReviewContainer.setVisibility(View.GONE);
                mhostMsg.setVisibility(View.GONE);
                mHoldAllBanquet.setVisibility(View.GONE);
                mHoldBanquet.setVisibility(View.GONE);
                mEmptyIv.setVisibility(View.GONE);
                mAllComment.setVisibility(View.GONE);
                mGuestHead.setVisibility(View.VISIBLE);
                if (mAttendee.getAvatarURLSmall().isPresent()) {
                    Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                    mGuestHead.setOnClickListener(mHeadListener);
                }
            } else {
                String culinary = sellerInfo.getCulinary();
                if (culinary != null && !culinary.equals("")) {
                    ((TextView)mAttendeeSkilled.findViewById(R.id.attendee_detail_info_item_content)).setText(culinary);
                }
                String cookSkill = sellerInfo.getSkill();
                if (cookSkill != null && !cookSkill.equals("")) {
                    ((TextView)  mAttendeeCooking.findViewById(R.id.attendee_detail_info_item_content)).setText(cookSkill);
                }
                Seller.SellerPlace sellerPlace = sellerInfo.getPlace();
                if (sellerPlace != null) {
                    String placeType = sellerInfo.getPlace().getPlaceType();
                    if (placeType != null && !placeType.equals("")) {
                        ((TextView) mAttendeePlace.findViewById(R.id.attendee_detail_info_item_content)).setText(placeType);
                    }
                    String address = sellerPlace.getAddress();
                    if (address != null && !address.equals("")) {
                        ((TextView) mAttendeeAddress.findViewById(R.id.attendee_detail_info_item_content)).setText(address);
                    }
                    final ArrayList<String> imageUrls = sellerInfo.getPlace().getPlaceImages();
                    if (imageUrls != null && imageUrls.size() != 0) {
                        mHostImageContainer.setVisibility(View.VISIBLE);
                        for (int i = 0; i < imageUrls.size(); i++) {
                            ImageView image = new ImageView(mContext);
                            image.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                                    ActionBar.LayoutParams.MATCH_PARENT));
                            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(HostAndGuestActivity.this, BigImageActivity.class);
                                    int currentImageIndex = mHostImages.getCurrentItem();
                                    intent.putExtra("current_image_index", currentImageIndex);
                                    intent.putStringArrayListExtra("cloud_images", imageUrls);
                                    startActivity(intent);
                                }
                            });
                            Picasso.with(mContext)
                                    .load(imageUrls.get(i))
                                    .placeholder(R.drawable.empty_view_greeting)
                                    .error(R.drawable.empty_view_greeting)
                                    .into(image);
                            mImageList.add(image);
                        }
                        mViewPagerAdapter = new ImageViewPagerAdapter(mImageList);
                        mImageIndex.setText(1 + "/" + mImageList.size());
                        mHostImages.setAdapter(mViewPagerAdapter);
                        if (mAttendee.getAvatarURLSmall().isPresent()) {
                            Log.i("outofmemory", mAttendee.getAvatarURLSmall().get());
                            Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mHostHead);
                            mHostHead.setOnClickListener(mHeadListener);
                        }
                    } else {
                        mGuestHead.setVisibility(View.VISIBLE);
                        if (mAttendee.getAvatarURLSmall().isPresent()) {
                            Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                            mGuestHead.setOnClickListener(mHeadListener);
                        }
                    }
                } else {
                    mGuestHead.setVisibility(View.VISIBLE);
                    if (mAttendee.getAvatarURLSmall().isPresent()) {
                        Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                        mGuestHead.setOnClickListener(mHeadListener);
                    }
                }
                // register last hold banquet begin
                Banquent lastHoldBanquet = sellerInfo.getLastHoldEvent();
                if (lastHoldBanquet != null) {
                    mHoldAllBanquet.setVisibility(View.VISIBLE);
                    holdBanquet.setVisibility(View.VISIBLE);
                    banquetImageHoldNull.setVisibility(View.GONE);

                    mHostHold.setText(String.format(getString(R.string.hosted_banquet), sellerInfo.getEventCount(), sellerInfo.getSoldCount()));
                    Picasso.with(mContext).load(lastHoldBanquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into((ImageView) holdBanquet.findViewById(R.id.banquet_history_item_image1));
                    setBanquetStatus(lastHoldBanquet,holdBanquet);
                    ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_title1)).setText(lastHoldBanquet.getTitle());
                    ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_date1)).setText(formatDate(lastHoldBanquet.getTime()));
                    ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_price1)).setText(String.format(getString(R.string.price_per_one), lastHoldBanquet.getPrice()));
                    ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_situation1)).setText(String.format(getString(R.string.total_attendee), lastHoldBanquet.getAttendees().size()));
                } else {
                    mHoldAllBanquet.setVisibility(View.GONE);
                    holdBanquet.setVisibility(View.GONE);
                    banquetImageHoldNull.setVisibility(View.VISIBLE);
                }
                //register last hold banquet end
                //register last comment  ;
                Review lastReview = sellerInfo.getLastReview();
                registerCommentView(lastReview);
            }
            //register seller info end ;
        }

    }

    private void networkStep() {
        createDialog();
        mAttendeeIntroductionTitle.setText(getString(R.string.host_introduction));
        Router.getAccountModule().banquetUserInfo(mAttendeeId, new OneParameterExpression<Attendee>() {
            @Override
            public void action(Attendee attendee) {
                finalizeDialog();
                mAttendee = attendee;
                //register common user info
                registerViewsByAttendee();
                //register last attend banquet begin
                Banquent lastAttendBanquet = attendee.getLastAttendEvent();

                if (lastAttendBanquet == null) {
                    attendeeBanquet.setVisibility(View.GONE);
                    banquetImageAttendeeNull.setVisibility(View.VISIBLE);
                    mAttendeeBanquet.setVisibility(View.GONE);
                } else {
                    attendeeBanquet.setVisibility(View.VISIBLE);
                    banquetImageAttendeeNull.setVisibility(View.GONE);
                    mAttendeeBanquet.setVisibility(View.VISIBLE);
                    mHostAttendee.setText(String.format(getString(R.string.attended_banquet), mAttendee.getAttendCount()));
                    Picasso.with(mContext).load(lastAttendBanquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into((ImageView) attendeeBanquet.findViewById(R.id.banquet_history_item_image1));
                    setBanquetStatus(lastAttendBanquet,attendeeBanquet);
                    ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_title1)).setText(lastAttendBanquet.getTitle());
                    ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_date1)).setText(formatDate(lastAttendBanquet.getTime()));
                    ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_price1)).setText(String.format(getString(R.string.price_per_one), lastAttendBanquet.getPrice()));
                    ((TextView) attendeeBanquet.findViewById(R.id.banquet_history_item_situation1)).setText(String.format(getString(R.string.total_attendee), lastAttendBanquet.getAttendees().size()));
                }
                //register last attend banquet end
                //register seller info begin ;
                Seller sellerInfo = attendee.getSellerInfo();
                if (sellerInfo == null) {
                    getSupportActionBar().setTitle(getString(R.string.guest));
                    mCommentStars.setVisibility(View.GONE);
                    mReviewContainer.setVisibility(View.GONE);
                    mhostMsg.setVisibility(View.GONE);
                    mHoldAllBanquet.setVisibility(View.GONE);
                    mHoldBanquet.setVisibility(View.GONE);
                    mEmptyIv.setVisibility(View.GONE);
                    mAllComment.setVisibility(View.GONE);
                    mGuestHead.setVisibility(View.VISIBLE);
                    if (mAttendee.getAvatarURLSmall().isPresent()) {
                        Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                        mGuestHead.setOnClickListener(mHeadListener);
                    }
                } else {
                    String culinary = sellerInfo.getCulinary();
                    if (culinary != null && !culinary.equals("")) {
                        ((TextView)mAttendeeSkilled.findViewById(R.id.attendee_detail_info_item_content)).setText(culinary);
                    }
                    String cookSkill = sellerInfo.getSkill();
                    if (cookSkill != null && !cookSkill.equals("")) {
                        ((TextView)  mAttendeeCooking.findViewById(R.id.attendee_detail_info_item_content)).setText(cookSkill);
                    }
                    Seller.SellerPlace sellerPlace = sellerInfo.getPlace();
                    if (sellerPlace != null) {
                        String placeType = sellerInfo.getPlace().getPlaceType();
                        if (placeType != null && !placeType.equals("")) {
                            ((TextView) mAttendeePlace.findViewById(R.id.attendee_detail_info_item_content)).setText(placeType);
                        }
                        String address = sellerPlace.getAddress();
                        if (address != null && !address.equals("")) {
                            ((TextView) mAttendeeAddress.findViewById(R.id.attendee_detail_info_item_content)).setText(address);
                        }
                        final ArrayList<String> imageUrls = sellerInfo.getPlace().getPlaceImages();
                        if (imageUrls != null && imageUrls.size() != 0) {
                            mHostImageContainer.setVisibility(View.VISIBLE);
                            for (int i = 0; i < imageUrls.size(); i++) {
                                ImageView image = new ImageView(mContext);
                                image.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                                        ActionBar.LayoutParams.MATCH_PARENT));
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(HostAndGuestActivity.this, BigImageActivity.class);
                                        int currentImageIndex = mHostImages.getCurrentItem();
                                        intent.putExtra("current_image_index", currentImageIndex);
                                        intent.putStringArrayListExtra("cloud_images", imageUrls);
                                        startActivity(intent);
                                    }
                                });
                                Picasso.with(mContext)
                                        .load(imageUrls.get(i))
                                        .placeholder(R.drawable.empty_view_greeting)
                                        .error(R.drawable.empty_view_greeting)
                                        .into(image);
                                mImageList.add(image);
                            }
                            mViewPagerAdapter = new ImageViewPagerAdapter(mImageList);
                            mImageIndex.setText(1 + "/" + mImageList.size());
                            mHostImages.setAdapter(mViewPagerAdapter);
                            if (mAttendee.getAvatarURLSmall().isPresent()) {
                                Log.i("outofmemory", mAttendee.getAvatarURLSmall().get());
                                Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mHostHead);
                                mHostHead.setOnClickListener(mHeadListener);
                            }
                        } else {
                            mGuestHead.setVisibility(View.VISIBLE);
                            if (mAttendee.getAvatarURLSmall().isPresent()) {
                                Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                                mGuestHead.setOnClickListener(mHeadListener);
                            }
                        }
                    } else {
                        mGuestHead.setVisibility(View.VISIBLE);
                        if (mAttendee.getAvatarURLSmall().isPresent()) {
                            Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                            mGuestHead.setOnClickListener(mHeadListener);
                        }
                    }
                    // register last hold banquet begin
                    Banquent lastHoldBanquet = sellerInfo.getLastHoldEvent();
                    if (lastHoldBanquet != null) {
                        mHoldAllBanquet.setVisibility(View.VISIBLE);
                        holdBanquet.setVisibility(View.VISIBLE);
                        banquetImageHoldNull.setVisibility(View.GONE);

                        mHostHold.setText(String.format(getString(R.string.hosted_banquet), sellerInfo.getEventCount(), sellerInfo.getSoldCount()));
                        Picasso.with(mContext).load(lastHoldBanquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into((ImageView) holdBanquet.findViewById(R.id.banquet_history_item_image1));
                        setBanquetStatus(lastHoldBanquet,holdBanquet);
                        ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_title1)).setText(lastHoldBanquet.getTitle());
                        ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_date1)).setText(formatDate(lastHoldBanquet.getTime()));
                        ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_price1)).setText(String.format(getString(R.string.price_per_one), lastHoldBanquet.getPrice()));
                        ((TextView) holdBanquet.findViewById(R.id.banquet_history_item_situation1)).setText(String.format(getString(R.string.total_attendee), lastHoldBanquet.getAttendees().size()));
                    } else {
                        mHoldAllBanquet.setVisibility(View.GONE);
                        holdBanquet.setVisibility(View.GONE);
                        banquetImageHoldNull.setVisibility(View.VISIBLE);
                    }
                    //register last hold banquet end
                    //register last comment  ;
                    Review lastReview = sellerInfo.getLastReview();
                    registerCommentView(lastReview);
                }
                //register seller info end ;
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                finalizeDialog();
                Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setBanquetStatus(Banquent banquet,View v){
        TextView finish = (TextView)v.findViewById(R.id.banquet_history_item_status_finished1);
        switch (BanquentStatus.fromString(banquet.getStatus())) {
            case Selling:
                finish.setVisibility(View.GONE);
                break;
            case SoldOut:
                finish.setText(R.string.banquet_status_sold_out);
                finish.setVisibility(View.VISIBLE);
                break;
            case OverTime:
                finish.setText(R.string.banquet_status_over_time);
                finish.setVisibility(View.VISIBLE);
                break;
            case End:
                finish.setText(R.string.banquet_status_end);
                finish.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void registerCommentView(Review review) {
        if (review != null) {
            mEmptyIv.setVisibility(View.GONE);
            mLastestComment.setVisibility(View.VISIBLE);
            mCommentStars.setVisibility(View.VISIBLE);

            if (mAttendee.getSellerInfo().getCommentCount() > 1) {
                mAllComment.setVisibility(View.VISIBLE);
            } else {
                mAllComment.setVisibility(View.GONE);
            }
            mReviewTitle.setText(String.format(getString(R.string.receive_comments), mAttendee.getSellerInfo().getCommentCount()));
//            Review review = reviewList.get(0);
            mCommentCount.setText("(" + mAttendee.getSellerInfo().getCommentCount() + ")");
            mCommentRatingbar.setRating(mAttendee.getSellerInfo().getScore());
            ImageView head = (RoundImageView) mLastestComment.findViewById(R.id.banquet_comment_item_head);
            ImageLoader.getInstance().displayImage(review.getReviewer().getAvatarURLSmall().get(), head);
            head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, HostAndGuestActivity.class);
//                    intent.putExtra("attendee_id", review.getReviewer().getIdentifier());
//                    intent.putExtra("b_host", false);
//                    startActivity(intent);
                    createDialog();
                    Router.getAccountModule().banquetUserInfo(review.getReviewer().getIdentifier(), new OneParameterExpression<Attendee>() {
                        @Override
                        public void action(Attendee attendee) {
                            finalizeDialog();
                            Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("attendee_info" , attendee) ;
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
                }
            });
            ((TextView) mLastestComment.findViewById(R.id.banquet_comment_item_user_name)).setText(review.getReviewer().getNickName());
            ((TextView) mLastestComment.findViewById(R.id.banquet_comment_item_issue)).setText(review.getEvent().getTitle() + " · " + formatDate(review.getEvent().getTime()));
            ((ReviewRatingBar) mLastestComment.findViewById(R.id.banquet_comment_item_stars)).setRating(review.getScore());
            ((TextView) mLastestComment.findViewById(R.id.banquet_comment_item_comment)).setText(review.getContent());
        } else {
            mEmptyIv.setVisibility(View.VISIBLE);
            mLastestComment.setVisibility(View.GONE);
            mCommentStars.setVisibility(View.GONE);
            mReviewTitle.setText(String.format(getString(R.string.receive_comments), 0));
            mAllComment.setVisibility(View.GONE);
        }
    }

    private void registerViewsByAttendee() {
        mAttendeeName.setText(mAttendee.getNickname());
        String profile = buildAttendeeProfile();
        mAttendeeInfo.setText(profile);
        if (mAttendee.getProfile().getSelfIntroduction().isPresent()
                && !mAttendee.getProfile().getSelfIntroduction().get().equals(""))
            mAttendeeIntroduction.setText(mAttendee.getProfile().getSelfIntroduction().get());

        if (mAttendee.getProfile().getCareer().isPresent() && !mAttendee.getProfile().getCareer().get().equals("")) {
            ((TextView) mAttendeeCareer.findViewById(R.id.attendee_detail_info_item_content)).setText(mAttendee.getProfile().getCareer().get());
        }
        if (mAttendee.getProfile().getLanguages().isPresent() && mAttendee.getProfile().getLanguages().get().size() != 0) {
            String languages = "";
            List<Language> cacheLanguages = Cache.INSTANCE.languages;
            for (String languageCode : mAttendee.getProfile().getLanguages().get()) {
                for (Language language : cacheLanguages) {
                    if (languageCode.equals(language.getCode())) {
                        languages += language.getName();
                        languages += " ";
                        break;
                    }
                }
            }
            ((TextView) mAttendeeLanguage.findViewById(R.id.attendee_detail_info_item_content)).setText(languages);
        }
        if (mAttendee.getProfile().getEducationBackground().isPresent() && !mAttendee.getProfile().getEducationBackground().get().equals("")) {
            ((TextView) mAttendeeEducation.findViewById(R.id.attendee_detail_info_item_content)).setText(mAttendee.getProfile().getEducationBackground().get());
        }
        if (mAttendee.getProfile().getHobby().isPresent() && !mAttendee.getProfile().getHobby().get().equals("")) {
            ((TextView) mAttendeeHobby.findViewById(R.id.attendee_detail_info_item_content)).setText(mAttendee.getProfile().getHobby().get());
        }
    }

    private String buildAttendeeProfile() {
        StringBuilder strBuilder = new StringBuilder();
        Profile profile = mAttendee.getProfile();
        if (profile == null)
            return null;
        String location = dbMng.getLocationByIds(profile.getProvinceId(), profile.getCityId());
        strBuilder.append(location);
        strBuilder.append(getString(R.string.center_dot));
        String gender = profile.getGender();
        if (gender.equals("male"))
            gender = getString(R.string.male);
        else if (gender.equals("female"))
            gender = getString(R.string.female);
        else
            gender = getString(R.string.gender_keep_secret);
        strBuilder.append(gender);
//        if (bHost) {
//            strBuilder.append("\n");
//            String qualification = mAttendee.getSellerInfo().getCulinary();
//            if (qualification != null) {
//                strBuilder.append(qualification + " ");
//            }
//            String cookingSkill = mAttendee.getSellerInfo().getSkill();
//            if (cookingSkill != null) {
//                strBuilder.append(cookingSkill);
//            }
//        }
        return strBuilder.toString();
    }


    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            mImageIndex.setText((arg0 + 1) + "/" + mViewPagerAdapter.getCount());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    private View.OnClickListener mHeadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.host_guest_host_head:
                case R.id.host_guest_guest_head:
                    if (mAttendee != null && mAttendee.getAvatarURLSmall().isPresent()) {
                        intent = new Intent(mContext, BigImageActivity.class);
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(mAttendee.getAvatarURL().get());
                        intent.putStringArrayListExtra("cloud_images", arrayList);
                        startActivity(intent);
                    }
                    break;
                case R.id.attendee_banquet_item:
                    if (mAttendee.getLastAttendEvent() == null) {
                    } else {
                        intent = new Intent(mContext, BanquetDetailActivity.class);
                        intent.putExtra("banquet", mAttendee.getLastAttendEvent());
                        startActivity(intent);
                    }
                    break;
                case R.id.hold_banquet_item:
                    if (mAttendee.getSellerInfo().getLastHoldEvent() == null) {
                    } else {
                        intent = new Intent(mContext, BanquetDetailActivity.class);
                        intent.putExtra("banquet", mAttendee.getSellerInfo().getLastHoldEvent());
                        startActivity(intent);
                    }
                    break;
                case R.id.host_guest_comment_btn:
                    intent = new Intent();
                    intent.setClass(mContext, BanquetCommentActivity.class);
                    intent.putExtra("host_id", mAttendee.getSellerInfo().getIdentifier());
                    intent.putExtra("totalCount", mAttendee.getSellerInfo().getCommentCount());
                    startActivity(intent);
                    break;
                case R.id.host_guest_attendee_btn:
                    intent = new Intent();
                    intent.setClass(mContext, BanquetListActivity.class);
                    intent.putExtra("b_hold", false);
                    intent.putExtra("attendee_id", mAttendee.getIdentifier());
                    startActivity(intent);
                    break;
                case R.id.host_guest_hold_btn:
                    intent = new Intent();
                    intent.setClass(mContext, BanquetListActivity.class);
                    intent.putExtra("b_hold", true);
                    intent.putExtra("attendee_id", mAttendee.getSellerInfo().getIdentifier());
                    startActivity(intent);
                    break;
            }
        }
    };
//    private BaseAdapter mHistoryAdapter = new BaseAdapter() {
//        @Override
//        public int getCount() {
//            if (banquentAttendeeList == null) {
//                return 0;
//            }
//            return banquentAttendeeList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_history_item, null);
//                holder = new ViewHolder(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            Banquent banquet = banquentAttendeeList.get(position);
//            Picasso.with(mContext).load(banquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
//            holder.title.setText(banquet.getTitle());
//            holder.date.setText(formatDate(banquet.getTime()));
//            holder.price.setText(String.format(getString(R.string.price_per_one), banquet.getPrice()));
//            holder.situation.setText(String.format(getString(R.string.total_attendee), banquet.getAttendees().size()));
//            return convertView;
//
//        }
//    };
//    private BaseAdapter mHistoryAdapter2 = new BaseAdapter() {
//        @Override
//        public int getCount() {
//            if (banquentHoldList == null) {
//                return 0;
//            }
//            return banquentHoldList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_history_item, null);
//                holder = new ViewHolder(convertView);
//                convertView.setTag(holder);
//            } else
//                holder = (ViewHolder) convertView.getTag();
//
//
//            Banquent banquet = banquentHoldList.get(position);
//            Picasso.with(mContext).load(banquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
//            holder.title.setText(banquet.getTitle());
//            holder.date.setText(formatDate(banquet.getTime()));
//            holder.price.setText(String.format(getString(R.string.price_per_one), banquet.getPrice()));
//            holder.situation.setText(String.format(getString(R.string.total_attendee), banquet.getAttendees().size()));
//            return convertView;
//
//        }
//    };

    class ViewHolder {
        @ViewInject(R.id.banquet_history_item_image)
        ImageView image;
        @ViewInject(R.id.banquet_history_item_title)
        TextView title;
        @ViewInject(R.id.banquet_history_item_date)
        TextView date;
        @ViewInject(R.id.banquet_history_item_situation)
        TextView situation;
        @ViewInject(R.id.banquet_history_item_price)
        TextView price;
        @ViewInject(R.id.banquet_history_item_status_finished)
        TextView finish;

        ViewHolder(View convertView) {
            com.lidroid.xutils.ViewUtils.inject(this, convertView);
        }
    }

//    protected void createDialog() {
//        if (mDialog != null && mDialog.isShowing()) return;
//        mDialog = ProgressDialog.show(this, "", getString(R.string.on_loading));
//    }

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
