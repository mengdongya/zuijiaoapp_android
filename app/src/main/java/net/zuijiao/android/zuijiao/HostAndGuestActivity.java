package net.zuijiao.android.zuijiao;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.adapter.ImageViewPagerAdapter;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.common.Language;
import com.zuijiao.android.zuijiao.model.user.Profile;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.AdapterViewHeightCalculator;

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
    @ViewInject(R.id.banquet_detail_review_container)
    private LinearLayout mReviewContainer;
    @ViewInject(R.id.banquet_detail_hold_btn)
    private Button mHoldAllBanquet;
    @ViewInject(R.id.banquet_detail_attendee_btn)
    private Button mAttendeeBanquet;
    @ViewInject(R.id.banquet_detail_comment_btn)
    private Button mAllComment;
    @ViewInject(R.id.host_guest_history_list)
    private ListView mHistoryList;

    @ViewInject(R.id.host_detail_group)
    private LinearLayout mhostMsg;
    @ViewInject(R.id.ll_host_comment_stars)
    private LinearLayout mCommentStars;
    @ViewInject(R.id.ll_host_hold_banquet)
    private LinearLayout mHoldBanquet;
    @ViewInject(R.id.ll_host_attendee_banquet)
    private LinearLayout llAttendeeBanquet;

    /*@ViewInject(R.id.attendee_detail_info_item_view)
    private View attendee_detail_info_item_view;*/
    @ViewInject(R.id.banquet_history_item_image_hold)
    private ImageView banquetImageHoldNull;
    @ViewInject(R.id.banquet_history_item_image_attendee)
    private ImageView banquetImageAttendeeNull;

    @ViewInject(R.id.hold_banquet_item)
    private View holdBanquet;
    @ViewInject(R.id.attendee_banquet_item)
    private View attendeeBanquet;
    @ViewInject(R.id.banquet_item_text)
    private View holdAttendeeBanquet;

    private ArrayList<ImageView> mImageList = new ArrayList<>();
    private ImageViewPagerAdapter mViewPagerAdapter;
    private ArrayList<Banquent> banquentHoldList;
    private ArrayList<Banquent> banquentAttendeeList;
    private int mAttendeeId = -1;
    private boolean bHost = false;
    private Attendee mAttendee;
    private String[] weekDays;
    private Order mOrder;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        if (mTendIntent != null) {
            bHost = mTendIntent.getBooleanExtra("b_host", false);
            mAttendeeId = mTendIntent.getIntExtra("attendee_id", -1);
        }
        if (mAttendeeId == -1) {
            finish();
            return;
        }

        ((TextView) mAttendeeCooking.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.cooking));
        ((TextView) mAttendeeSkilled.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.skilled)+"  ");
        ((TextView) mAttendeePlace.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.place)+"  ");
        ((TextView) mAttendeeAddress.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.address)+"  ");
        ((TextView) mAttendeeCareer.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.industry));
        ((TextView) mAttendeeLanguage.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.my_language));
        ((TextView) mAttendeeEducation.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.education));
        ((TextView) mAttendeeHobby.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.interest_hobby));

        mHostImages.setOnPageChangeListener(mPageListener);
        mHoldAllBanquet.setOnClickListener(mHeadListener);
        mAttendeeBanquet.setOnClickListener(mHeadListener);
        mAllComment.setOnClickListener(mHeadListener);
        mHoldBanquet.setOnClickListener(mHeadListener);
        llAttendeeBanquet.setOnClickListener(mHeadListener);
        networkStep();
    }

    private void networkStep() {
        createDialog();
        mAttendeeIntroductionTitle.setText(getString(R.string.host_introduction));
        mHostAttendee.setText(getString(R.string.attended_banquet));

        Router.getBanquentModule().themesOfParticipator(mAttendeeId, null, 500, new OneParameterExpression<Banquents>() {
            @Override
            public void action(Banquents banquents) {
                banquentAttendeeList = banquents.getBanquentList();
                mHostAttendee.setText(String.format(getString(R.string.attended_banquet), banquentAttendeeList.size()));
                Banquent banquent = null;
                if (banquentAttendeeList.size() == 0) {
                    attendeeBanquet.setVisibility(View.GONE);
                    banquetImageAttendeeNull.setVisibility(View.VISIBLE);
                    mAttendeeBanquet.setVisibility(View.GONE);
                } else {
                    banquent = banquentAttendeeList.get(banquentAttendeeList.size() - 1);
                    Picasso.with(mContext).load(banquent.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into((ImageView) holdBanquet.findViewById(R.id.banquet_history_item_image));
                    ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_title)).setText(banquent.getTitle());
                    ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_date)).setText(formatDate(banquent.getTime()));
                    ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_price)).setText(String.format(getString(R.string.price_per_one), banquent.getPrice()));
                    ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_situation)).setText(String.format(getString(R.string.total_attendee), banquent.getAttendees().size()));
                }
                finalizeDialog();
            }
        }
                , new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.get_history_list_failed), Toast.LENGTH_SHORT).show();
                finalizeDialog();
            }
        });


        if (bHost) {
            mHostHold.setText(getString(R.string.hosted_banquet));
            getSupportActionBar().setTitle(getString(R.string.host));

            mCommentStars.setVisibility(View.VISIBLE);
            // mHostCommentList.setVisibility(View.VISIBLE);

            Router.getBanquentModule().themesOfMaster(mAttendeeId, null, 500, new OneParameterExpression<Banquents>() {

                @Override
                public void action(Banquents banquents) {
                    banquentHoldList = banquents.getBanquentList();
                    Banquent banquent = null;
                    if (banquentHoldList.size() != 0) {
                        int banquetPeopleCount = 0;
                        for (int i = 0; i < banquentHoldList.size(); i++) {
                            banquetPeopleCount += banquentHoldList.get(i).getAttendees().size();
                        }
                        mHostHold.setText(String.format(getString(R.string.hosted_banquet), banquentHoldList.size(), banquetPeopleCount));
                        banquent = banquentHoldList.get(banquentHoldList.size() - 1);
                        Picasso.with(mContext).load(banquent.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into((ImageView) holdBanquet.findViewById(R.id.banquet_history_item_image));
                        ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_title)).setText(banquent.getTitle());
                        ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_date)).setText(formatDate(banquent.getTime()));
                        ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_price)).setText(String.format(getString(R.string.price_per_one), banquent.getPrice()));
                        ((TextView) holdAttendeeBanquet.findViewById(R.id.banquet_history_item_situation)).setText(String.format(getString(R.string.total_attendee), banquent.getAttendees().size()));
                    } else {
                        mHoldAllBanquet.setVisibility(View.GONE);
//                        holdAttendeeBanquet.setVisibility(View.GONE);
                        holdBanquet.setVisibility(View.GONE);
                        banquetImageHoldNull.setVisibility(View.VISIBLE);
                    }

                    finalizeDialog();
                }
            }
                    , new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext, getString(R.string.get_history_list_failed), Toast.LENGTH_SHORT).show();
                    finalizeDialog();
                }
            });

            Router.getAccountModule().masterInfo(mAttendeeId, new OneParameterExpression<Attendee>() {
                @Override
                public void action(Attendee attendee) {
                    mAttendee = attendee;
                    registerViewsByAttendee();
                    finalizeDialog();
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String errorMsg) {
                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    finalizeDialog();
                }
            });
            mReviewContainer.setVisibility(View.VISIBLE);
        } else {
            getSupportActionBar().setTitle(getString(R.string.guest));
            mCommentStars.setVisibility(View.GONE);
            mReviewContainer.setVisibility(View.GONE);
            mhostMsg.setVisibility(View.GONE);
            mHoldAllBanquet.setVisibility(View.GONE);
            mHoldBanquet.setVisibility(View.GONE);
            mAllComment.setVisibility(View.GONE);
            // mHostCommentList.setVisibility(View.GONE);
            Router.getAccountModule().attendeeInfo(mAttendeeId, new OneParameterExpression<Attendee>() {
                @Override
                public void action(Attendee attendee) {
                    mAttendee = attendee;
                    registerViewsByAttendee();
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
    }

    private void registerViewsByAttendee() {
        final ArrayList<String> imageUrls = mAttendee.getImageUrls();
//        mToolbar.setTitle(mAttendee.getNickname());
        if (imageUrls != null && imageUrls.size() != 0) {
            mHostImageContainer.setVisibility(View.VISIBLE);
            for (int i = 0; i < imageUrls.size(); i++) {
                ImageView image = new ImageView(this);
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
//            mGuestHead.setVisibility(View.GONE);
        } else {
//            mHostImageContainer.setVisibility(View.GONE);
            mGuestHead.setVisibility(View.VISIBLE);
            if (mAttendee.getAvatarURLSmall().isPresent()) {
                Picasso.with(mContext).load(mAttendee.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                mGuestHead.setOnClickListener(mHeadListener);
            }
        }
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
        if (bHost) {
            strBuilder.append("\n");
            String qualification = mAttendee.getQualification();
            if (qualification != null) {
                strBuilder.append(qualification + " ");
            }
            String cookingSkill = mAttendee.getCookingSkill();
            if (cookingSkill != null) {
                strBuilder.append(cookingSkill);
            }
        }
        return strBuilder.toString();
    }


    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            mImageIndex.setText((arg0 + 1) + "/" + mViewPagerAdapter.getCount());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {        }

        @Override
        public void onPageScrollStateChanged(int arg0) {        }
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
                case R.id.ll_host_attendee_banquet:
                    if (banquentAttendeeList.size() == 0){
                    }else {
                        intent = new Intent(mContext, BanquetDetailActivity.class);
                        intent.putExtra("banquet", banquentAttendeeList.get(0));
                        startActivity(intent);
                    }
                    break;
                case R.id.ll_host_hold_banquet:
                    if (banquentHoldList.size() == 0){
                    }else {
                        intent = new Intent(mContext, BanquetDetailActivity.class);
                        intent.putExtra("banquet", banquentHoldList.get(0));
                        startActivity(intent);
                    }
                    break;
                case R.id.banquet_detail_comment_btn:
                    intent = new Intent();
                    intent.setClass(mContext, BanquetCommentActivity.class);
                    startActivity(intent);
                    break;
                case R.id.banquet_detail_attendee_btn:
                    intent = new Intent();
                    intent.setClass(mContext,BanquetListActivity.class);
                    intent.putExtra("b_hold", false);
                    intent.putExtra("attendee_id",mAttendeeId);
                    startActivity(intent);
                    break;
                case R.id.banquet_detail_hold_btn:
                    intent = new Intent();
                    intent.setClass(mContext,BanquetListActivity.class);
                    intent.putExtra("b_hold",true);
                    intent.putExtra("attendee_id",mAttendeeId);
                    startActivity(intent);
                    break;
            }
        }
    };
    private BaseAdapter mHistoryAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (banquentAttendeeList == null) {
                return 0;
            }
            return banquentAttendeeList.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_history_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Banquent banquet = banquentAttendeeList.get(position);
            Picasso.with(mContext).load(banquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
            holder.title.setText(banquet.getTitle());
            holder.date.setText(formatDate(banquet.getTime()));
            holder.price.setText(String.format(getString(R.string.price_per_one),banquet.getPrice()));
            holder.situation.setText(String.format(getString(R.string.total_attendee), banquet.getAttendees().size()));
            return convertView;

        }
    };
    private BaseAdapter mHistoryAdapter2 = new BaseAdapter() {
        @Override
        public int getCount() {
            if (banquentHoldList == null) {
                return 0;
            }
            return banquentHoldList.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_history_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();


            Banquent banquet = banquentHoldList.get(position);
            Picasso.with(mContext).load(banquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
            holder.title.setText(banquet.getTitle());
            holder.date.setText(formatDate(banquet.getTime()));
            holder.price.setText(String.format(getString(R.string.price_per_one), banquet.getPrice()));
            holder.situation.setText(String.format(getString(R.string.total_attendee), banquet.getAttendees().size()));
            return convertView;

        }
    };

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
        ViewHolder(View convertView) {
            com.lidroid.xutils.ViewUtils.inject(this, convertView);
        }
    }

    protected void createDialog() {
        if (mDialog != null && mDialog.isShowing()) return;
        mDialog = ProgressDialog.show(this, "", getString(R.string.on_loading));
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
