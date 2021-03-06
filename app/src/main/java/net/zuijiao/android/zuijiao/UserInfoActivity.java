package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.TasteTag;
import com.zuijiao.android.zuijiao.model.user.FriendShip;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.SimpleImage;
import com.zuijiao.utils.AdapterViewHeightCalculator;

import java.util.ArrayList;
import java.util.Date;

/**
 * display user information ,called from main-activity side bar
 * or by some other user avatar like in gourmet list , gourmet detail and so on;
 */
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
    // tiny user , get from precious activity
    private TinyUser mTinyUser = null;
    //user info ,including all information of one user ,get from net work request
    private User mFullUser = null;

    /**
     * user taste adapter
     */
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
                    Picasso.with(mContext).load(tasteTag.getImageURL()).placeholder(R.drawable.default_user_head).fit().centerCrop().into(image);
                    break;
                }
            }
            return contentView;
        }
    };

    /**
     * user info adapter , including three part .
     */
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
                        int now = new Date().getYear();
                        int birth = mFullUser.getProfile().getBirthday().get().getYear();
                        age = String.format(getString(R.string.year_old), new Date().getYear() - mFullUser.getProfile().getBirthday().get().getYear());
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
                    gdView.setFocusable(false);
                    gdView.setFocusableInTouchMode(false);
                    gdView.setAdapter(mFlavorAdapter);
                    AdapterViewHeightCalculator.setGridViewHeightBasedOnChildren(gdView);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_USER_INFO_REQ && resultCode == RESULT_OK) {
            mFullUser = mFileMng.getFullUser();
            registerViewByFullUser();
            Intent intent = new Intent(MessageDef.ACTION_GET_THIRD_PARTY_USER);
            sendBroadcast(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * register the menu item listener
     * if the user is self , click to edit user info ,
     * if not ,click to follow or un-follow this gay
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_self_info:
                if (mFullUser == null) {
                    getUserInfo(new LambdaExpression() {
                        @Override
                        public void action() {
                            Intent intent = new Intent();
                            intent.setClass(mContext, EditUserInfoActivity.class);
                            startActivityForResult(intent, EDIT_USER_INFO_REQ);
                        }
                    });
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, EditUserInfoActivity.class);
                    startActivityForResult(intent, EDIT_USER_INFO_REQ);
                }
                break;
            case R.id.menu_follow_someone:
                if (mFullUser != null && mFullUser.getFriendShip() != null && mFullUser.getFriendShip().isFollowing()) {
                    Router.getSocialModule().unFollow(mTinyUser.getIdentifier(), new LambdaExpression() {
                        @Override
                        public void action() {
                            mFullUser.getFriendShip().setIsFollowing(false);
                            Toast.makeText(mContext, getString(R.string.un_follow_success), Toast.LENGTH_SHORT).show();
                            mMenuBtn.setTitle(getString(R.string.follow));
                        }
                    }, new OneParameterExpression<String>() {
                        @Override
                        public void action(String s) {
                            Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (mFullUser != null && mFullUser.getFriendShip() != null && !mFullUser.getFriendShip().isFollowing()) {
                    Router.getSocialModule().follow(mTinyUser.getIdentifier(), new LambdaExpression() {
                        @Override
                        public void action() {
                            mFullUser.getFriendShip().setIsFollowing(true);
                            Toast.makeText(mContext, getString(R.string.follow_success), Toast.LENGTH_SHORT).show();
                            mMenuBtn.setTitle(getString(R.string.un_follow));
                        }
                    }, new OneParameterExpression<String>() {
                        @Override
                        public void action(String s) {
                            Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Router.getSocialModule().follow(mTinyUser.getIdentifier(), new LambdaExpression() {
                        @Override
                        public void action() {
                            if (mFullUser != null && mFullUser.getFriendShip() == null) {
                                mFullUser.setFriendShip(new FriendShip());
                            }
                            mFullUser.getFriendShip().setIsFollowing(true);
                            mFullUser.getFriendShip().setIsFollowing(true);
                            Toast.makeText(mContext, getString(R.string.follow_success), Toast.LENGTH_SHORT).show();
                            mMenuBtn.setTitle(getString(R.string.un_follow));
                        }
                    }, new OneParameterExpression<String>() {
                        @Override
                        public void action(String s) {
                            Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                        }
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
        if (mFullUser.getAvatarURLSmall().isPresent())
            Picasso.with(mContext).load(mFullUser.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).fit().centerCrop().into(mUserHead);
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

    /**
     * get full-user information from net work ,
     *
     * @param expression
     */
    private void getUserInfo(final LambdaExpression expression) {
        OneParameterExpression<User> successExpression = new OneParameterExpression<User>() {
            @Override
            public void action(User fullUser) {
                mFullUser = fullUser;
                registerViewByFullUser();
                if (expression != null)
                    expression.action();
            }
        };
        OneParameterExpression<String> failExpression = new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
            }
        };
        if ((mTinyUser.getIdentifier().equals(mPreferMng.getStoredUserId()) || mTinyUser.getIdentifier() == -1)) {
            if (Router.getInstance().getCurrentUser().isPresent()) {
                Router.getAccountModule().fetchMyInfo(successExpression, failExpression);
            } else {
                tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        getUserInfo(null);
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
                tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        getUserInfo(null);
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                    }
                });
            } else getUserInfo(null);
        }
        if (mTinyUser.getAvatarURLSmall().isPresent())
            Picasso.with(mContext).load(mTinyUser.getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).fit().centerCrop().into(mUserHead);
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
    }

    /**
     * register views listener ;
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.user_info_recommend:
                intent.setClass(UserInfoActivity.this, RecommendAndFavorActivity.class);
                intent.putExtra("content_type", GourmetDisplayFragment.RECOMMEND_PAGE);
                intent.putExtra("tiny_user", mTinyUser);
                break;
            case R.id.user_info_fans:
                intent.setClass(UserInfoActivity.this, FriendActivity.class);
                intent.putExtra("friend_index", 1);
                if (mFullUser != null && mFullUser.getFriendShip() != null) {
                    intent.putExtra("follow_count", mFullUser.getFriendShip().getFollowingCount());
                    intent.putExtra("fans_count", mFullUser.getFriendShip().getFollowerCount());
                }
                intent.putExtra("tiny_user", mTinyUser);
                break;
            case R.id.user_info_follow:
                intent.setClass(UserInfoActivity.this, FriendActivity.class);
                intent.putExtra("friend_index", 0);
                if (mFullUser != null && mFullUser.getFriendShip() != null) {
                    intent.putExtra("follow_count", mFullUser.getFriendShip().getFollowingCount());
                    intent.putExtra("fans_count", mFullUser.getFriendShip().getFollowerCount());
                }
                intent.putExtra("tiny_user", mTinyUser);
                break;
            case R.id.user_info_wish:
                intent.setClass(UserInfoActivity.this, RecommendAndFavorActivity.class);
                intent.putExtra("content_type", GourmetDisplayFragment.FAVOR_PAGE);
                intent.putExtra("tiny_user", mTinyUser);
                break;
            case R.id.user_info_user_head:
                intent.setClass(UserInfoActivity.this, BigImageActivity.class);
                ArrayList<SimpleImage> avatarUrls = new ArrayList<>();
                String avatarUrl = null;
                try {
                    if (mFullUser != null) {
                        avatarUrl = mFullUser.getAvatarURL().get();
                    } else {
                        avatarUrl = mTinyUser.getAvatarUrl().get();
                    }
                } catch (Exception e) {
                    System.out.print("no avatar url");
                }
                if (avatarUrl == null || avatarUrl.equals("")) {
                    Toast.makeText(mContext, getString(R.string.no_avatar), Toast.LENGTH_SHORT).show();
                    return;
                }
                avatarUrls.add(new SimpleImage("", "", avatarUrl));
                intent.putParcelableArrayListExtra("edit_images", avatarUrls);
                break;
        }
        startActivity(intent);
    }

}
