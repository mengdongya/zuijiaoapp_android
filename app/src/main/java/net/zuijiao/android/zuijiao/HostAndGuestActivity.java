package net.zuijiao.android.zuijiao;


import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import com.zuijiao.android.zuijiao.model.common.Language;
import com.zuijiao.android.zuijiao.model.user.Profile;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;

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
    @ViewInject(R.id.host_guest_history_list)
    private ListView mHistoryList;
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
    @ViewInject(R.id.attendee_detail_career_item)
    private View mAttendeeCareer;
    @ViewInject(R.id.attendee_detail_language_item)
    private View mAttendeeLanguage;
    @ViewInject(R.id.attendee_detail_education_item)
    private View mAttendeeEducation;
    @ViewInject(R.id.attendee_detail_hobby_item)
    private View mAttendeeHobby;
    @ViewInject(R.id.host_guest_history_title)
    private TextView mHistoryTitle;
    private ArrayList<ImageView> mImageList = new ArrayList<>();
    private ImageViewPagerAdapter mViewPagerAdapter;
    private List<Banquent> banquentList;
    private int mAttendeeId = -1;
    private boolean bHost = false;
    private Attendee mAttendee;
    private String[] weekDays;

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
        ((TextView) mAttendeeCareer.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.industry));
        ((TextView) mAttendeeLanguage.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.my_language));
        ((TextView) mAttendeeEducation.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.education));
        ((TextView) mAttendeeHobby.findViewById(R.id.attendee_detail_info_item_title)).setText(getString(R.string.interest_hobby));
        mHostImages.setOnPageChangeListener(mPageListener);
        mHistoryList.setOnItemClickListener(mItemListener);
        networkStep();
    }

    private void networkStep() {
        createDialog();
        if (bHost) {
            mAttendeeIntroductionTitle.setText(getString(R.string.host_introduction));
            mHistoryTitle.setText(getString(R.string.hosted_banquet));
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
            Router.getBanquentModule().themesOfMaster(mAttendeeId, null, 500, new OneParameterExpression<Banquents>() {

                @Override
                public void action(Banquents banquents) {
                    banquentList = banquents.getBanquentList();
                    mHistoryList.setAdapter(mHistoryAdapter);
                    setListViewHeightBasedOnChildren(mHistoryList);
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
        } else {
            mHistoryTitle.setText(getString(R.string.attended_banquet));
            mAttendeeIntroductionTitle.setText(getString(R.string.personal_introduction));
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
            Router.getBanquentModule().themesOfParticipator(mAttendeeId, null, 500, new OneParameterExpression<Banquents>() {

                @Override
                public void action(Banquents banquents) {
                    banquentList = banquents.getBanquentList();
                    mHistoryList.setAdapter(mHistoryAdapter);
                    setListViewHeightBasedOnChildren(mHistoryList);
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
        }
    }

    private void registerViewsByAttendee() {
        final ArrayList<String> imageUrls = mAttendee.getImageUrls();
        mToolbar.setTitle(mAttendee.getNickname());
        if (imageUrls != null && imageUrls.size() != 0) {
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
            if (mAttendee.getAvatarURL().isPresent()) {
                Picasso.with(mContext).load(mAttendee.getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(mHostHead);
                mHostHead.setOnClickListener(mHeadListener);
            }
            mGuestHead.setVisibility(View.GONE);
        } else {
            mHostImageContainer.setVisibility(View.GONE);
            mGuestHead.setVisibility(View.VISIBLE);
            if (mAttendee.getAvatarURL().isPresent()) {
                Picasso.with(mContext).load(mAttendee.getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(mGuestHead);
                mGuestHead.setOnClickListener(mHeadListener);
            }
        }
        mAttendeeName.setText(mAttendee.getNickname());
        String profile = buildAttendeeProfile();
        mAttendeeInfo.setText(profile);
        if (mAttendee.getProfile().getSelfIntroduction().isPresent()
                && !mAttendee.getProfile().getSelfIntroduction().get().equals(""))
            mAttendeeIntroduction.setText(mAttendee.getProfile().getSelfIntroduction().get());
        else
            mIntroductionContainer.setVisibility(View.GONE);
        if (mAttendee.getProfile().getCareer().isPresent() && !mAttendee.getProfile().getCareer().get().equals("")) {
            ((TextView) mAttendeeCareer.findViewById(R.id.attendee_detail_info_item_content)).setText(mAttendee.getProfile().getCareer().get());
        } else {
            mAttendeeCareer.setVisibility(View.GONE);
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
        } else {
            mAttendeeLanguage.setVisibility(View.GONE);
        }
        if (mAttendee.getProfile().getEducationBackground().isPresent() && !mAttendee.getProfile().getEducationBackground().get().equals("")) {
            ((TextView) mAttendeeEducation.findViewById(R.id.attendee_detail_info_item_content)).setText(mAttendee.getProfile().getEducationBackground().get());
        } else {
            mAttendeeEducation.setVisibility(View.GONE);
        }
        if (mAttendee.getProfile().getHobby().isPresent() && !mAttendee.getProfile().getHobby().get().equals("")) {
            ((TextView) mAttendeeHobby.findViewById(R.id.attendee_detail_info_item_content)).setText(mAttendee.getProfile().getHobby().get());
        } else {
            mAttendeeHobby.setVisibility(View.GONE);
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
        strBuilder.append("\n");
        if (bHost) {
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));
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


    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mContext, BanquetDetailActivity.class);
            intent.putExtra("banquet", banquentList.get(position));
            startActivity(intent);
        }
    };
    private View.OnClickListener mHeadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAttendee != null && mAttendee.getAvatarURL().isPresent()) {
                Intent intent = new Intent(mContext, BigImageActivity.class);
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(mAttendee.getAvatarURL().get());
                intent.putStringArrayListExtra("cloud_images", arrayList);
                startActivity(intent);
            }
        }
    };
    private BaseAdapter mHistoryAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (banquentList == null) {
                return 0;
            }
            return banquentList.size();
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
            Banquent banquet = banquentList.get(position);
            Picasso.with(mContext).load(banquet.getSurfaceImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
            holder.title.setText(banquet.getTitle());
            holder.date.setText(formatDate(banquet.getTime()));
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

        ViewHolder(View convertView) {
            com.lidroid.xutils.ViewUtils.inject(this, convertView);
        }
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
