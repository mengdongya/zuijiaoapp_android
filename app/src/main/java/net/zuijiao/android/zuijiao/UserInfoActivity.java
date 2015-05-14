package net.zuijiao.android.zuijiao;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.TasteTag;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.SimpleImage;

import java.util.ArrayList;
import java.util.Date;

@ContentView(R.layout.activity_user_info)
public final class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final int EDIT_USER_INFO_REQ = 5001;
    @ViewInject(R.id.user_info_list)
    private ListView mInfoList;
    @ViewInject(R.id.user_info_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.tv_user_name_detail)
    private TextView mUserName = null;
    @ViewInject(R.id.user_info_user_head)
    private ImageView mUserHead = null;
    @ViewInject(R.id.user_info_recommend)
    private Button mBtnRecommend;
    @ViewInject(R.id.user_info_wish)
    private Button mBtnFavor;
    @ViewInject(R.id.user_info_follow)
    private Button mBtnFollow;
    @ViewInject(R.id.user_info_fans)
    private Button mBtnFans;

    private TinyUser mTinyUser = null;
    private User mFullUser = null;
//    private User mUser = null ;
//    private ParcelableUser mParcelableUser = null ;

    //    private boolean bSelf = false;
//    private AdapterView.OnItemClickListener mUserInfoItemListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getApplicationContext(), "parent" + position, Toast.LENGTH_SHORT).show();
//        }
//    };
    private BaseAdapter mFlavorAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            try {
                return mFullUser.getProfile().getTasteTags().get().size();
            } catch (Exception e) {
                return 0;
            }
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
            View contentView = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.user_info_favor_item, null);
            ImageView image = (ImageView) contentView.findViewById(R.id.user_info_favor_item_image);
            TextView text = (TextView) contentView.findViewById(R.id.user_info_favor_item_text);
            text.setText(mFullUser.getProfile().getTasteTags().get().get(position));
            for (TasteTag tasteTag : Cache.INSTANCE.tasteTags) {
                if (mFullUser.getProfile().getTasteTags().get().get(position).equals(tasteTag.getName())) {
                    Picasso.with(mContext).load(tasteTag.getImageURL()).placeholder(R.drawable.default_user_head).into(image);
                    break;
                }
            }
            return contentView;
        }
    };
    private AdapterView.OnItemClickListener mFlavorItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
        }
    };
    private BaseAdapter mInfoAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.user_info_item, null);
            TextView title = (TextView) contentView.findViewById(R.id.user_info_item_title);
            TextView content = (TextView) contentView.findViewById(R.id.user_info_item_content);
            if (position == 0) {
                title.setText(getString(R.string.base_info));
                String basicInfo;
                if (mFullUser != null && mFullUser.getProfile() != null) {
                    String age = "";
                    try {
//                        int now = new Date().getYear() ;
//                        int birth =  mFullUser.getProfile().getBirthday().get().getYear() ;
                        age = String.format(getString(R.string.year_old), new Date().getYear() + 1900 - mFullUser.getProfile().getBirthday().get().getYear());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
//                    if (mFullUser.getProfile().getBirthday().isPresent()) {
//                        age = String.format(getString(R.string.year_old), new Date().getYear() - mFullUser.getProfile().getBirthday().get().getYear());
//                    }
                    String address = DBOpenHelper.getmInstance(mContext).getLocationByIds(mFullUser.getProfile().getProvinceId(), mFullUser.getProfile().getCityId());
                    String gender = mFullUser.getProfile().getGender();
                    if (gender.equals("keepSecret")) {
                        gender = getString(R.string.gender_keep_secret);
                    } else if (gender.equals("female")) {
                        gender = getString(R.string.gender_female);
                    } else if (gender.equals("male")) {
                        gender = getString(R.string.gender_male);
                    }
                    basicInfo = gender + "      " + age + "     " + address;
                } else {
                    basicInfo = getString(R.string.no_base_info);
                }
                content.setText(basicInfo);
            } else if (position == 1) {
                title.setText(getString(R.string.personal_introduction));
                String intro;
                if (mFullUser != null && mFullUser.getProfile() != null && mFullUser.getProfile().getSelfIntroduction().isPresent()) {
                    intro = mFullUser.getProfile().getSelfIntroduction().get();
                } else {
                    intro = getString(R.string.no_experience);
                }
                content.setText(intro);
            } else if (position == 2) {
                title.setText(getString(R.string.flavor_hobby));
                if (mFullUser != null
                        && mFullUser.getProfile() != null
                        && mFullUser.getProfile().getTasteTags().isPresent()
                        && mFullUser.getProfile().getTasteTags().get().size() != 0) {
                    GridView gdView = (GridView) contentView.findViewById(R.id.gv_user_info_favor);
                    gdView.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                    gdView.setOnItemClickListener(mFlavorItemListener);
                    gdView.setFocusable(false);
                    gdView.setFocusableInTouchMode(false);
                    gdView.setAdapter(mFlavorAdapter);
                    setListViewHeightBasedOnChildren(gdView);
                } else {
                    content.setText(getString(R.string.no_hobby));
                }
            }
            return contentView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    };
    private MenuItem mMenuBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_USER_INFO_REQ && resultCode == RESULT_OK) {
            mFullUser = mFileMng.getFullUser();
            registerViewByFullUser();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_self_info:
                Intent intent = new Intent();
                intent.setClass(mContext, EditUserInfoActivity.class);
                startActivityForResult(intent, EDIT_USER_INFO_REQ);
                break;
            case R.id.menu_follow_someone:
                if (mFullUser != null && mFullUser.getFriendShip() != null && mFullUser.getFriendShip().isFollowing()) {
                    Router.getSocialModule().unFollow(mTinyUser.getIdentifier(), () -> {
                        Toast.makeText(mContext, getString(R.string.un_follow_success), Toast.LENGTH_SHORT).show();
                        mMenuBtn.setTitle(getString(R.string.follow));
                    }, errorMsg -> {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                    });
                } else if (mFullUser != null && mFullUser.getFriendShip() != null && !mFullUser.getFriendShip().isFollowing()) {
                    Router.getSocialModule().follow(mTinyUser.getIdentifier(), () -> {
                        Toast.makeText(mContext, getString(R.string.follow_success), Toast.LENGTH_SHORT).show();
                        mMenuBtn.setTitle(getString(R.string.un_follow));
                    }, errorMsg -> {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Router.getSocialModule().follow(mTinyUser.getIdentifier(), () -> {
                        Toast.makeText(mContext, getString(R.string.follow_success), Toast.LENGTH_SHORT).show();
                        mMenuBtn.setTitle(getString(R.string.un_follow));
                    }, errorMsg -> {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTinyUser.getIdentifier().equals(mPreferMng.getStoredUserId()) || mTinyUser.getIdentifier() == -1) {
            getMenuInflater().inflate(R.menu.user_info_self, menu);
        } else {
            getMenuInflater().inflate(R.menu.user_info, menu);
            mMenuBtn = menu.findItem(R.id.menu_follow_someone);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void registerViewByFullUser() {
        if (mFullUser.getIdentifier().equals(mPreferMng.getStoredUserId())) {
            mFileMng.setFullUser(mFullUser);
        }
        Picasso.with(mContext).load(mFullUser.getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(mUserHead);
        mUserName.setText(mFullUser.getNickname().get());
        int follower = 0, following = 0, recommendation = 0, favor = 0;
        if (mFullUser.getFriendShip() != null) {
            follower = mFullUser.getFriendShip().getFollowerCount() == null ? 0 : mFullUser.getFriendShip().getFollowerCount();
            following = mFullUser.getFriendShip().getFollowingCount() == null ? 0 : mFullUser.getFriendShip().getFollowingCount();
            if (!mTinyUser.getIdentifier().equals(mPreferMng.getStoredUserId()))
                mMenuBtn.setTitle(mFullUser.getFriendShip().isFollowing() ? getString(R.string.un_follow) : getString(R.string.follow));
        }
        if (mFullUser.getFood() != null) {
            recommendation = mFullUser.getFood().getRecommendationCount() == null ? 0 : mFullUser.getFood().getRecommendationCount();
            favor = mFullUser.getFood().getCollectionCount() == null ? 0 : mFullUser.getFood().getCollectionCount();
        }
        mBtnFans.setText(String.format(getString(R.string.fans_count), follower));
        mBtnFavor.setText(String.format(getString(R.string.favorite_count), favor));
        mBtnFollow.setText(String.format(getString(R.string.follow_count), following));
        mBtnRecommend.setText(String.format(getString(R.string.recommend_count), recommendation));
        mInfoAdapter.notifyDataSetChanged();
    }
    private void getUserInfo() {
        OneParameterExpression<User> successExpression = fullUser -> {
            mFullUser = fullUser;
            registerViewByFullUser();
        };
        OneParameterExpression<String> failExpression = errorMsg -> {
            Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
        };
        if ((mTinyUser.getIdentifier().equals(mPreferMng.getStoredUserId()) || mTinyUser.getIdentifier() == -1)) {
            Router.getAccountModule().fetchMyInfo(successExpression, failExpression);
        } else {
            Router.getAccountModule().fetchUserInfoById(mTinyUser.getIdentifier(), successExpression, failExpression);
        }
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mTendIntent != null) {
            mTinyUser = (TinyUser) mTendIntent.getSerializableExtra("tiny_user");
            if (mTinyUser == null) {
                finish();
            }
            int id = mTinyUser.getIdentifier();
            if (id == -1) {
                tryLoginFirst(() -> {
                    getUserInfo();
                }, errorMsg -> {
                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                });
            } else {
                getUserInfo();
            }
        }
        if (mTinyUser.getAvatarURL().isPresent())
            Picasso.with(mContext).load(mTinyUser.getAvatarURL().get()).placeholder(R.drawable.default_user_head).into(mUserHead);
        mUserName.setText(mTinyUser.getNickName());
        mBtnFans.setText(String.format(getString(R.string.fans_count), 0));
        mBtnFavor.setText(String.format(getString(R.string.favorite_count), 0));
        mBtnFollow.setText(String.format(getString(R.string.follow_count), 0));
        mBtnRecommend.setText(String.format(getString(R.string.recommend_count), 0));
        mBtnRecommend.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnFans.setOnClickListener(this);
        mBtnFavor.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        mInfoList.setAdapter(mInfoAdapter);
        mInfoList.setItemsCanFocus(true);
//        mInfoList.setOnItemClickListener(mUserInfoItemListener);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.user_info_recommend:
                intent.setClass(UserInfoActivity.this, RecommendAndFavorActivity.class);
                intent.putExtra("content_type", MainFragment.RECOMMEND_PAGE);
                intent.putExtra("tiny_user", mTinyUser);
                break;
            case R.id.user_info_fans:
                intent.setClass(UserInfoActivity.this, FriendActivity.class);
                intent.putExtra("friend_index", 1);
                break;
            case R.id.user_info_follow:
                intent.setClass(UserInfoActivity.this, FriendActivity.class);
                intent.putExtra("friend_index", 0);
                break;
            case R.id.user_info_wish:
                intent.setClass(UserInfoActivity.this, RecommendAndFavorActivity.class);
                intent.putExtra("content_type", MainFragment.FAVOR_PAGE);
                intent.putExtra("tiny_user", mTinyUser);
                break;
            case R.id.user_info_user_head:
                intent.setClass(UserInfoActivity.this, BigImageActivity.class);
                ArrayList<SimpleImage> avatarUrls = new ArrayList<>();
                String avatarUrl = null;
                if (mFullUser != null) {
                    avatarUrl = mFullUser.getAvatarURL().get();
                } else {
                    avatarUrl = mTinyUser.getAvatarURL().get();
                }
                avatarUrls.add(new SimpleImage("", "", avatarUrl));
                intent.putParcelableArrayListExtra("edit_images", avatarUrls);
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
                + (gdView.getVerticalSpacing() * (listAdapter.getCount()));

    }
}
