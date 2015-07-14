package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Picasso;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.message.NewsList;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.utils.StrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_main)
public final class MainActivity extends BaseActivity {
    private static final int SETTING_REQ = 6001;
    public static final int LOGOUT_RESULT = 60001;
    // main activity layout widget ,including slide menu
    @ViewInject(R.id.drawer)
    private DrawerLayout mDrawerLayout;

    // DrawerListener
    private ActionBarDrawerToggle mDrawerToggle;
    // login button and user head in drawer view
    @ViewInject(R.id.vs_user_info)
    private RelativeLayout mUserInfo = null;
    @ViewInject(R.id.btn_login)
    private Button mBtnLogin = null;
    // setting list in drawer view
    @ViewInject(R.id.lv_drawe_items)
    private ListView mMainTabsTitle = null;
    @ViewInject(R.id.tv_user_name)
    private TextView mThirdPartyUserName = null;
    @ViewInject(R.id.iv_user_head)
    private ImageView mThirdPartyUserHead = null;
    @ViewInject(R.id.main_toolbar)
    private Toolbar mToolBar = null;
    @ViewInject(R.id.container_user_info)
    private LinearLayout mUserInfoArea = null;
    @ViewInject(R.id.lv_drawe_items2)
    private ListView mSettingList = null;
    private ArrayList<Fragment> mFragmentList = null;
    //    private GourmetDisplayFragment mMainFragment = null;
    private MainFragment mMainFragment = null;
    private GourmetDisplayFragment mRecommendFragment = null;
    private GourmetDisplayFragment mFavorFragment = null;
    private MyOrderFragment mMyOrderFragment = null;
    private Fragment mCurrentFragment = null;
    private FragmentManager mFragmentMng = null;
    private FragmentTransaction mFragmentTransaction = null;
    private String[] titles = null;
    public static int unReadNewsCount = 0;
    private static final int BACK_STACK_DURATION = 2000;
    public final static int COMMENT_SUCCESS = 10002;
    public final static int COMMENT_REQUEST = 10001;


    private OnItemClickListener mTabsListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            mFragmentTransaction = mFragmentMng.beginTransaction();
            if (!mFragmentList.get(position).isAdded()) {
                mFragmentTransaction.hide(mCurrentFragment).add(R.id.main_content_container, mFragmentList.get(position)).commit();
            } else {
//                if(mFragmentList.indexOf(mCurrentFragment) == 0){
//                    mFragmentTransaction.hide(mCurrentFragment).addToBackStack("main_fragment").show(mFragmentList.get(position)).commit();
//                }else{
                mFragmentTransaction.hide(mCurrentFragment).show(mFragmentList.get(position)).commit();
//                }
            }
            mCurrentFragment = mFragmentList.get(position);
            mToolBar.setTitle(titles[position]);
            if (position == 3) {
//                removeBadgeView();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mToolBar.setElevation(0);
                }
//                if (!mToolBar.getMenu().hasVisibleItems())
//                    mToolBar.getMenu().add(0, R.id.action_already_read, 1, getString(R.string.already_read))
//                            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
//                            .setOnMenuItemClickListener((MenuItem item) -> {
//                                Router.getMessageModule().markAsRead(() -> {
//                                    unReadNewsCount = 0 ;
//                                    mTabTitleAdapter.notifyDataSetChanged();
//                                }, () -> {
//
//                                });
//                                Router.getMessageModule().markAsRead(News.NotificationType.Notice, () -> {
//                                    if (mMyOrderFragment != null) {
//                                        mMyOrderFragment.notificationRead();
//                                    }
//                                }, () -> {
//                                });
//                                Router.getMessageModule().markAsRead(News.NotificationType.Comment, () -> {
//                                    if (mMyOrderFragment != null) {
//                                        mMyOrderFragment.messageRead();
//                                    }
//                                }, () -> {
//                                });
//                                return false;
//                            });
            } else {
//                mToolBar.getMenu().removeItem(R.id.action_already_read);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mToolBar.setElevation(30);
                }
            }
            if (position != 0) {
                mLocationView.setVisibility(View.INVISIBLE);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mToolBar.setElevation(0);
                }
                mLocationView.setVisibility(View.VISIBLE);
            }
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    };
    private int[] mTabImages = {R.drawable.setting_home, R.drawable.setting_recommend, R.drawable.setting_favor, R.drawable.setting_orders};
    private int[] mTabTitles = {R.string.main_page, R.string.recommend_page, R.string.favor_page, R.string.my_order};
    private View mLocationView = null;
    private BadgeView mBadgeView = null;
    private OnClickListener mLocationListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.location_choose_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setView(view).create().show();
        }
    };
    private OnClickListener mUserInfoDetail = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, UserInfoActivity.class);
            Optional<TinyUser> user = Router.getInstance().getCurrentUser();
            if (!user.isPresent()) {
                user = Optional.of(new TinyUser());
                AuthorInfo authInfo = PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg();
                user.get().setNickName(authInfo.getUserName());
                user.get().setAvatarURL(authInfo.getHeadPath());
                user.get().setIdentifier(authInfo.getUserId());
            }
            intent.putExtra("tiny_user", user.get());
            startActivity(intent);
        }
    };
    private OnClickListener loginBtnListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // mViewSwitcher.showNext();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            intent.putExtra("b_self", true);
            startActivity(intent);
        }
    };
    private NewsList mNewsList = null;
    private ArrayList<String> mLabelData = new ArrayList<String>();
    private BaseAdapter mTabTitleAdapter = new BaseAdapter() {
        private ArrayList<String> data = new ArrayList<String>();

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.drawer_setting_item, null);
            ImageView image = (ImageView) contentView
                    .findViewById(R.id.drawer_setting_item_image);
            TextView textView = (TextView) contentView
                    .findViewById(R.id.drawer_setting_item_text);
            TextView textMsg = (TextView) contentView
                    .findViewById(R.id.drawer_setting_item_text2);
            image.setImageResource(mTabImages[position]);
            textView.setText(getString(mTabTitles[position]));
            textMsg.setVisibility(View.GONE);
//            if (position == 3 && unReadNewsCount != 0) {
//                textMsg.setText(unReadNewsCount + "");
//                textMsg.setVisibility(View.VISIBLE);
//            }
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
            return 4;
        }
    };
    private String[] mSettingArray = null;
    private BaseAdapter mSettingAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mSettingArray.length;
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
            TextView tv = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.drawer_setting2_item, null);
            tv.setText(mSettingArray[position]);
            return tv;
        }
    };
    private OnItemClickListener mSettingListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position == 0) {
                FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
                agent.startFeedbackActivity();
//                Intent feedBackIntent = new Intent(MainActivity.this,
//                        CommonWebViewActivity.class);
//                feedBackIntent.putExtra("title",
//                        getResources().getString(R.string.feed_back));
//                String token = Router.getInstance().getAccessToken().get();
//                String url = String.format(getString(R.string.feed_back_url), "", token);
//                feedBackIntent.putExtra("content_url", getString(R.string.feed_back_url));
//                startActivity(feedBackIntent);
            } else if (position == 1) {
                Intent feedBackIntent = new Intent(MainActivity.this,
                        ConcerningActivity.class);
                startActivity(feedBackIntent);
            } else if (position == 2) {
                Intent settingIntent = new Intent(mContext, SettingActivity.class);
                startActivityForResult(settingIntent, SETTING_REQ);
            }
        }
    };

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MessageDef.ACTION_GET_THIRD_PARTY_USER)) {
                onUserInfoGot();
            } else if (intent.getAction().equals(MessageDef.ACTION_REFRESH_RECOMMENDATION)) {
                onRecommendationChanged();
            } else if (intent.getAction().equals(MessageDef.ACTION_PUSH_RECEIVED)) {
                onPushReceived();
            } else if (intent.getAction().equals(MessageDef.ACTION_ORDER_CREATED)) {
                onOrderCreated();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
        filter.addAction(MessageDef.ACTION_REFRESH_RECOMMENDATION);
        filter.addAction(MessageDef.ACTION_PUSH_RECEIVED);
        filter.addAction(MessageDef.ACTION_ORDER_CREATED);
        registerReceiver(mReceiver, filter);
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
    }

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        getString(R.string.guest);
        return true;
    }

    @Override
    protected void registerViews() {
        String device_token = UmengRegistrar.getRegistrationId(mContext);
        Log.i("deviceToken", device_token);
        setSupportActionBar(mToolBar);
//        mToolBar.setOnMenuItemClickListener(onMenuItemClick);
        mLocationView = LayoutInflater.from(this).inflate(
                R.layout.location_layout, null);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Light.ttf");
        ((TextView) mLocationView.findViewById(R.id.toolbar_location_text)).setTypeface(tf);
        mLocationView.setOnClickListener(mLocationListener);
        mToolBar.addView(mLocationView);
        // Optional<TinyUser> user = Router.getInstance().getCurrentUser();
        // System.err.println("user" + user);
        AuthorInfo auth = PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg();
        if (auth.getUserName() != null && !auth.getUserName().equals("")) {
            mBtnLogin.setVisibility(View.GONE);
            mThirdPartyUserName.setVisibility(View.VISIBLE);
            mThirdPartyUserHead.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setText(auth.getUserName());
            try {
                Picasso.with(mContext)
                        .load(auth.getHeadPath())
                        .placeholder(R.drawable.default_user_head)
                        .into(mThirdPartyUserHead);
            } catch (Exception e) {
                e.printStackTrace();
                mThirdPartyUserHead.setImageResource(R.drawable.default_user_head);
            }
            mSettingArray = getResources()
                    .getStringArray(
                            R.array.settings1);
            mSettingList.setAdapter(mSettingAdapter);
        } else {
            mSettingArray = getResources().getStringArray(R.array.settings2);
            mSettingList.setAdapter(mSettingAdapter);
            mBtnLogin.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setVisibility(View.GONE);
            mThirdPartyUserHead.setVisibility(View.GONE);
        }
//        mUserInfoArea.setOnClickListener((View v) -> {
//        });
        mSettingList.setOnItemClickListener(mSettingListener);
        titles = getResources().getStringArray(R.array.fragment_title);
        mToolBar.setTitle(titles[0]);
        mBadgeView = initBadgeView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mBtnLogin.setOnClickListener(loginBtnListener);
        mMainTabsTitle.setAdapter(mTabTitleAdapter);
        mMainTabsTitle.setOnItemClickListener(mTabsListener);
        mThirdPartyUserHead.setOnClickListener(mUserInfoDetail);
        mFragmentList = new ArrayList<Fragment>();
        mMainFragment = new MainFragment(mContext);
        mFragmentList.add(mMainFragment);
        mRecommendFragment = new GourmetDisplayFragment(GourmetDisplayFragment.RECOMMEND_PAGE, mContext);
        mFragmentList.add(mRecommendFragment);
        mFavorFragment = new GourmetDisplayFragment(GourmetDisplayFragment.FAVOR_PAGE, mContext);
        mFragmentList.add(mFavorFragment);
        mMyOrderFragment = new MyOrderFragment();
        mFragmentList.add(mMyOrderFragment);
        mFragmentMng = getSupportFragmentManager();
        mFragmentTransaction = mFragmentMng.beginTransaction();
        mFragmentTransaction.add(R.id.main_content_container,
                mFragmentList.get(0));
        mCurrentFragment = mFragmentList.get(0);
        mFragmentTransaction.commit();
        mToolBar.setTitle(titles[0]);
        checkVersion();
        Router.getMessageModule().notifications(newsList -> {
            mNewsList = newsList;
            if (newsList != null && newsList.getNews() != null && newsList.getNews().size() != 0) {
                for (News news : newsList.getNews()) {
                    unReadNewsCount += news.getUnreadCount();
                }
                if (unReadNewsCount > 0) {
                    showBadgeView();
//                    mTabTitleAdapter.notifyDataSetChanged();
                } else {
                    removeBadgeView();
                }
            }
        }, errorMsg -> {
            //do nothing
        });
        startNewActivity(mTendIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startNewActivity(intent);
        super.onNewIntent(intent);
    }

    /**
     * start activity from notification
     *
     * @param intent
     */
    private void startNewActivity(Intent intent) {
        String content_url = intent.getStringExtra("content_url");
        if (content_url != null && !content_url.equals("")) {
            if (StrUtil.isNumer(content_url)) {
                int openinfo = Integer.parseInt(content_url);
                if (openinfo != 0) {
                    createDialog();
                    tryLoginFirst(new LambdaExpression() {
                        @Override
                        public void action() {
                            Router.getBanquentModule().theme(openinfo, new OneParameterExpression<Banquent>() {
                                @Override
                                public void action(Banquent banquent) {
                                    Intent intent = new Intent(mContext, BanquetDetailActivity.class);
                                    intent.putExtra("banquet", banquent);
                                    startActivity(intent);
                                    finalizeDialog();
                                }
                            }, new OneParameterExpression<String>() {
                                @Override
                                public void action(String errorMsg) {
                                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                                    finalizeDialog();
                                }
                            });
                        }
                    }, new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer integer) {
                            Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            finalizeDialog();
                        }
                    });
                }
            } else {
                Intent intent1 = new Intent();
                intent1.putExtra("content_url", content_url);
                intent1.setClass(this, CommonWebViewActivity.class);
                startActivity(intent1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private BadgeView initBadgeView() {
        ViewGroup badgeParent = (ViewGroup) findViewById(R.id.badge_parent);
        final BadgeView badgeView = new BadgeView(getApplicationContext(),
                badgeParent);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeView.setWidth(15);
        badgeView.setHeight(15);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        badgeParent.measure(width, height);
        height = badgeParent.getMeasuredHeight();
        width = badgeParent.getMeasuredWidth();
        badgeView.setBadgeMargin(width / 18, height / 4);
        badgeView.setFocusable(false);
        return badgeView;
    }

    private void showBadgeView() {
        if (mBadgeView == null) {
            mBadgeView = initBadgeView();
        }
        if (!mBadgeView.isShown()) {
            mBadgeView.show();
        }
    }

    private void removeBadgeView() {
        if (mBadgeView != null) {
            mBadgeView.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main) {
            Intent intent = new Intent();
            intent.setClass(mContext, MessageActivity.class);
            startActivity(intent);
            Router.getMessageModule().markAsRead(News.NotificationType.Notice, () -> {
                if (unReadNewsCount > 0) {
                    removeBadgeView();
                }
            }, () -> {
            });
            Router.getMessageModule().markAsRead(News.NotificationType.Comment, () -> {
                if (unReadNewsCount == 0) {
                    removeBadgeView();
                }
            }, () -> {
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private long lastBackPressedTime = 0;

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (mFragmentList.indexOf(mCurrentFragment) == 0 && mMainFragment.onBackPressed()) {
            return;
        }
        if (mFragmentList.indexOf(mCurrentFragment) != 0) {
            mTabsListener.onItemClick(mMainTabsTitle, null, 0, 0);
//            mFragmentMng.beginTransaction().hide(mCurrentFragment).show(mFragmentList.get(0)).commit();
//            mCurrentFragment =mFragmentList.get(0) ;
            return;
        }
        long currentBackPressedTime = new Date().getTime();
        if (currentBackPressedTime - lastBackPressedTime > BACK_STACK_DURATION) {
            Toast.makeText(mContext, getString(R.string.exit_confirm), BACK_STACK_DURATION).show();
            lastBackPressedTime = currentBackPressedTime;
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        ActivityTask.getInstance().exit();
    }

    protected void onPushReceived() {
        showBadgeView();
        Router.getMessageModule().notifications(new OneParameterExpression<NewsList>() {
            @Override
            public void action(NewsList newsList) {
                mNewsList = newsList;
//            mTabTitleAdapter.notifyDataSetChanged();
            }
        }, null);
    }

    private void onOrderCreated() {
        mMainFragment.refreshBanquetList();
    }

    protected void onUserInfoGot() {
        AuthorInfo auth = mPreferMng.getThirdPartyLoginMsg();
        mThirdPartyUserName.setText(auth.getUserName());
        if (auth.getHeadPath() == null || auth.getHeadPath().equals("")) {
            mThirdPartyUserHead.setImageResource(R.drawable.default_user_head);
        } else
            Picasso.with(getApplicationContext())
                    .load(auth.getHeadPath())
                    .placeholder(R.drawable.default_user_head)
                    .into(mThirdPartyUserHead);
        mBtnLogin.setVisibility(View.GONE);
        mThirdPartyUserHead.setVisibility(View.VISIBLE);
        mThirdPartyUserName.setVisibility(View.VISIBLE);
        mSettingArray = getResources()
                .getStringArray(
                        R.array.settings1);
        mSettingList.setAdapter(mSettingAdapter);
    }

    private void checkVersion() {
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                                         UpdateResponse updateInfo) {
                if (updateStatus == 0 && updateInfo != null) {
                    showUpdateDialog(updateInfo.path, updateInfo.updateLog);
                }
            }
        });

        UmengUpdateAgent.update(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQ && resultCode == LOGOUT_RESULT) {
            mFavorFragment.clearPersonalData();
            mRecommendFragment.clearPersonalData();
//            mMyOrderFragment.clearMessage();
            mFavorFragment.mDisplayUser = null;
            mRecommendFragment.mDisplayUser = null;
            Toast.makeText(mContext, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
            mBtnLogin.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setVisibility(View.GONE);
            mThirdPartyUserHead.setVisibility(View.GONE);
            mSettingArray = getResources().getStringArray(R.array.settings2);
            mSettingList.setAdapter(mSettingAdapter);
        }
    }

    protected void onRecommendationChanged() {

        try {
            if (mRecommendFragment.isAdded())
                mRecommendFragment.firstInit();
        } catch (Exception r) {
            r.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void click(View v) {
        File file = new File(getCacheDir().getPath() + File.separator + "head.jpg");
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "h.jpg");
        try {

            file2.createNewFile();
        } catch (Exception r) {

        }
        boolean b = file.renameTo(file2);
    }

}
