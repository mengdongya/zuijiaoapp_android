package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Window;
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
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.SellerStatus;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.message.NewsList;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.utils.AdapterViewHeightCalculator;
import com.zuijiao.utils.StrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

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
    private BanquetDisplayFragment mMainFragment = null;
    private MyOrderFragment mMyOrderFragment = null;
    private MyBanquentActiveFragment mMyBanquentActiveFragment;
    private HostBanquentFragment mHostBanquentFragment = null;
    private Fragment mCurrentFragment = null;
    private FragmentManager mFragmentMng = null;
    private FragmentTransaction mFragmentTransaction = null;
    private String[] titles = null;
    public static int unReadNewsCount = 0;
    private static final int BACK_STACK_DURATION = 2000;
    public static final int COMMENT_SUCCESS = 10010;
    public static final int COMMENT_REQUEST = 10011;
    public static final int ORDER_REQUEST = 20010;
    public static final int ORDER_CANCEL = 20011;
    public static final int ORDER_PAY_SUCCESS = 20012;
    private SellerStatus mSellerStatus;

    private enum TabTag {
        publicBanquet, myOrder, sellerBanquet, sellerOrder, sellerApplication
    }

    private OnItemClickListener mTabsListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (view == null) {
                mFragmentTransaction = mFragmentMng.beginTransaction();
                mFragmentTransaction.hide(mCurrentFragment).show(mFragmentList.get(0)).commit();
                mCurrentFragment = mFragmentList.get(0);
            } else {
                TabTag tag = (TabTag) view.getTag();
                switch (tag) {
                    case publicBanquet:

                    case myOrder:

                    case sellerBanquet:
                    case sellerOrder:
                        mFragmentTransaction = mFragmentMng.beginTransaction();
                        mFragmentTransaction.hide(mCurrentFragment).show(mFragmentList.get(position)).commit();
                        mCurrentFragment = mFragmentList.get(position);
                        break ;
                    case sellerApplication:

                    default:
                        onItemClick(parent, null, position, id);
                }
            }

//            if (position == mFragmentList.size()) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, ApplyForHostStep1Activity.class);
//                startActivity(intent);
//            } else {
//                mFragmentTransaction = mFragmentMng.beginTransaction();
//                if (!mFragmentList.get(position).isAdded()) {
//                    mFragmentTransaction.hide(mCurrentFragment).add(R.id.main_content_container, mFragmentList.get(position)).commit();
//                } else {
//                    mFragmentTransaction.hide(mCurrentFragment).show(mFragmentList.get(position)).commit();
//                }
//                mCurrentFragment = mFragmentList.get(position);
//                mToolBar.setTitle(titles[position]);
//                if (position != 0) {
//                    mLocationView.setVisibility(View.INVISIBLE);
//                }
//            }
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    };
    private int[] mTabImages = {R.drawable.setting_home,
            R.drawable.setting_orders,
            R.drawable.setting_banquent_active,
            R.drawable.order_list_place_holder,
            R.drawable.apply_host};
    private int[] mTabTitles = {R.string.main_page, R.string.my_order, R.string.my_banquent_active, R.string.my_banquent_order, R.string.apply_to_be_host};

    private View mLocationView = null;
    private BadgeView mBadgeView = null;
    private OnClickListener mLocationListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.location_choose_layout, null);
            AlertDialog dialog = new AlertDialog.Builder(
                    MainActivity.this).setView(view).create();
            Window window = dialog.getWindow();
            window.setWindowAnimations(R.style.dialogWindowAnim);
            dialog.show();
        }
    };
    private OnClickListener mUserInfoDetail = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, EditUserInfoActivity.class);
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
            TextView sellerStatusText = (TextView) contentView
                    .findViewById(R.id.drawer_setting_item_text2);
            switch (position) {
                case 0:
                    textView.setText(R.string.main_page);
                    image.setImageResource(R.drawable.setting_home);
                    sellerStatusText.setVisibility(View.GONE);
                    contentView.setTag(TabTag.publicBanquet);
                    break;
                case 1:
                    textView.setText(R.string.my_order);
                    image.setImageResource(R.drawable.setting_orders);
                    sellerStatusText.setVisibility(View.GONE);
                    contentView.setTag(TabTag.myOrder);
                    break;
                default:
                    if (mSellerStatus == null) {
                        textView.setText(R.string.apply_host);
                        image.setImageResource(R.drawable.setting_banquent_active);
                        sellerStatusText.setVisibility(View.GONE);
                    } else if (mSellerStatus.getApplyStatus() != SellerStatus.ApplyStatus.passed
                            || mSellerStatus.getBankStatus() != SellerStatus.BankStatus.finished) {
                        int statusResStr = -1;
                        switch (mSellerStatus.getApplyStatus()) {
                            case editing:
                                break;
                            case waiting:
                            case reviewing:
                                statusResStr = R.string.seller_status_reviewing;
                                break;
                            case fail:
                                statusResStr = R.string.seller_status_failed;
                                break;
                            case passed:
                                statusResStr = R.string.seller_status_success;

                        }
                        textView.setText(R.string.apply_host);
                        image.setImageResource(R.drawable.setting_banquent_active);
                        if (statusResStr != -1) {
                            sellerStatusText.setText(statusResStr);
                        } else
                            sellerStatusText.setVisibility(View.GONE);
                        contentView.setTag(TabTag.sellerApplication);
                    } else {
                        if (position == 3) {
                            textView.setText(R.string.my_banquent_active);
                            image.setImageResource(R.drawable.setting_banquent_active);
                            sellerStatusText.setVisibility(View.GONE);
                            contentView.setTag(TabTag.sellerBanquet);
                        } else if (position == 4) {
                            textView.setText(R.string.my_banquent_order);
                            image.setImageResource(R.drawable.order_list_place_holder);
                            sellerStatusText.setVisibility(View.GONE);
                            contentView.setTag(TabTag.sellerOrder);
                        }
                    }
            }
            return contentView;
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
            if (mSellerStatus != null
                    && mSellerStatus.getApplyStatus() == SellerStatus.ApplyStatus.passed
                    && mSellerStatus.getBankStatus() == SellerStatus.BankStatus.finished) {
                return 4;
            } else {
                return 3;
            }
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
//                onRecommendationChanged();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void registerViews() {
        setSupportActionBar(mToolBar);
        mLocationView = LayoutInflater.from(this).inflate(
                R.layout.location_layout, null);
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
                        .fit()
                        .centerCrop()
                        .into(mThirdPartyUserHead);
            } catch (Exception e) {
                e.printStackTrace();
                mThirdPartyUserHead.setImageResource(R.drawable.default_user_head);
            }
            mSettingArray = getResources()
                    .getStringArray(
                            R.array.settings1);
            mSettingList.setAdapter(mSettingAdapter);
            AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mSettingList);
        } else {
            mSettingArray = getResources().getStringArray(R.array.settings2);
            mSettingList.setAdapter(mSettingAdapter);
            AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mSettingList);
            mBtnLogin.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setVisibility(View.GONE);
            mThirdPartyUserHead.setVisibility(View.GONE);
        }
        mSettingList.setOnItemClickListener(mSettingListener);
        titles = getResources().getStringArray(R.array.fragment_title);
        mBadgeView = initBadgeView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mBtnLogin.setOnClickListener(loginBtnListener);

        mThirdPartyUserHead.setOnClickListener(mUserInfoDetail);
        mFragmentList = new ArrayList<Fragment>();
        mMainFragment = BanquetDisplayFragment.newInstance();
        mFragmentList.add(mMainFragment);
        mMyOrderFragment = MyOrderFragment.getInstance();
        mFragmentList.add(mMyOrderFragment);

//        mMyBanquentActiveFragment = MyBanquentActiveFragment.newInstance();
//        mFragmentList.add(mMyBanquentActiveFragment);
//
//        mHostBanquentFragment = HostBanquentFragment.getInstance();
//        mFragmentList.add(mHostBanquentFragment);
        mMainTabsTitle.setAdapter(mTabTitleAdapter);
        AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mMainTabsTitle);
        mMainTabsTitle.setOnItemClickListener(mTabsListener);
        mFragmentMng = getSupportFragmentManager();
        mFragmentTransaction = mFragmentMng.beginTransaction();
        mFragmentTransaction.add(R.id.main_content_container,
                mFragmentList.get(0));
        mCurrentFragment = mFragmentList.get(0);
        mFragmentTransaction.commit();
        checkVersion();
        checkSellerStatus();
//        if (mTendIntent != null)
//            startNewActivity(mTendIntent);

    }

    private void checkSellerStatus() {
        Router.getAccountModule().sellerStatus(new OneParameterExpression<SellerStatus>() {
            @Override
            public void action(SellerStatus sellerStatus) {
                mSellerStatus  =SellerStatus.INSTANCE_SELLER ;
                Router.getInstance().setSellerStatus(Optional.of(sellerStatus));
//                mSellerStatus = sellerStatus;
                mTabTitleAdapter.notifyDataSetChanged();
                AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mMainTabsTitle);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Log.e("sellerstatus", s);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startNewActivity(intent);
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        //drop gourmet begin
//        if (mFragmentList.indexOf(mCurrentFragment) == 0 && mMainFragment.onBackPressed()) {
//            return;
//        }
        //drop gourmet end
        if (mFragmentList.indexOf(mCurrentFragment) != 0) {
            mTabsListener.onItemClick(mMainTabsTitle, null, 0, 0);
//            mFragmentMng.beginTransaction().hide(mCurrentFragment).show(mFragmentList.get(0)).commit();
//            mCurrentFragment =mFragmentList.get(0) ;
            return;
        }
        long currentBackPressedTime = new Date().getTime();
        if (currentBackPressedTime - lastBackPressedTime > BACK_STACK_DURATION) {
            Toast.makeText(mContext, getString(R.string.exit_confirm), Toast.LENGTH_SHORT).show();
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
            }
        }, null);
    }

    private void onOrderCreated() {
        //drop gourmet
        mMainFragment.onRefresh();
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
                    .fit()
                    .centerCrop()
                    .into(mThirdPartyUserHead);
        mBtnLogin.setVisibility(View.GONE);
        mThirdPartyUserHead.setVisibility(View.VISIBLE);
        mThirdPartyUserName.setVisibility(View.VISIBLE);
        mSettingArray = getResources()
                .getStringArray(
                        R.array.settings1);
        mSettingList.setAdapter(mSettingAdapter);
        AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mSettingList);
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
        if (requestCode == COMMENT_REQUEST || requestCode == ORDER_REQUEST) {
            mMyOrderFragment.onActivityResult(requestCode, resultCode, data);
            mHostBanquentFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == SETTING_REQ && resultCode == LOGOUT_RESULT) {
            //drop gourmet begin
//            mFavorFragment.clearPersonalData();
//            mRecommendFragment.clearPersonalData();
////            mMyOrderFragment.clearMessage();
//            mFavorFragment.mDisplayUser = null;
//            mRecommendFragment.mDisplayUser = null;
            //drop gourmet end
            Toast.makeText(mContext, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
            mBtnLogin.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setVisibility(View.GONE);
            mThirdPartyUserHead.setVisibility(View.GONE);
            mSettingArray = getResources().getStringArray(R.array.settings2);
            mSettingList.setAdapter(mSettingAdapter);
            AdapterViewHeightCalculator.setListViewHeightBasedOnChildren(mSettingList);
        }
    }

    //drop gourmet begin
//    protected void onRecommendationChanged() {
//
//        try {
//            if (mRecommendFragment.isAdded())
//                mRecommendFragment.firstInit();
//        } catch (Exception r) {
//            r.printStackTrace();
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }
    //drop gourmet end
    public void click(View v) {
        File file = new File(getCacheDir().getPath() + File.separator + "head.jpg");
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "h.jpg");
        try {

            file2.createNewFile();
        } catch (Exception r) {

        }
        boolean b = file.renameTo(file2);
    }

    @Override
    protected void fetchContent() {
        if (mFragmentList.indexOf(mCurrentFragment) == 0) {
            mMainFragment.onRefresh();
        }
    }
}
