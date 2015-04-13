package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Picasso;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.entity.AuthorInfo;

import java.util.ArrayList;

@ContentView(R.layout.activity_main)
public final class MainActivity extends BaseActivity {

    // main activity layout widget ,including slide menu
    @ViewInject(R.id.drawer)
    private DrawerLayout mDrawerLayout;
    // DrawerListener
    private ActionBarDrawerToggle mDrawerToggle;
    // login button and user head in drawer view
//    @ViewInject(R.id.vs_user_info)
//    private ViewSwitcher mViewSwitcher = null;
    @ViewInject(R.id.btn_login)
    private Button mBtnLogin = null;
    // setting list in drawer view
    @ViewInject(R.id.lv_drawe_items)
    private ListView mSettingList = null;
    @ViewInject(R.id.tv_user_name)
    private TextView mThirdPartyUserName = null;
    @ViewInject(R.id.iv_user_head)
    private ImageView mThirdPartyUserHead = null;
    // content view in main
    // @ViewInject(R.id.content_items)
    // private ListView mContentList = null;
    // float add button
    // @ViewInject(R.id.iv_add_one)
    // private ImageButton mIbAdd = null;
    // instead of actionbar
    @ViewInject(R.id.main_toolbar)
    private Toolbar mToolBar = null;
    @ViewInject(R.id.container_user_info)
    private LinearLayout mUserInfo = null;
    @ViewInject(R.id.lv_drawe_items2)
    private ListView mSettingList2 = null;
    //    private String[] settingStr;
    private ArrayList<Fragment> mFragmentList = null;
    private MainFragment mMainFragment = null;
    private MainFragment mFavorFragment = null;
    private MessageFragment mMsgFragment = null;
    private FragmentManager mFragmentMng = null;
    private FragmentTransaction mFragmentTransaction = null;
    private String[] titles = null;
    private OnItemClickListener mSetting1Listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            mFragmentTransaction = mFragmentMng.beginTransaction();
            mFragmentTransaction.replace(R.id.main_content_container,
                    mFragmentList.get(position));
            mFragmentTransaction.commit();
            mToolBar.setTitle(titles[position]);
            if (position != 0) {
                mLocationView.setVisibility(View.INVISIBLE);
            } else {
                mLocationView.setVisibility(View.VISIBLE);
            }
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    };
    private View mLocationView = null;
    private BadgeView mBadgeView = null;
    private ProgressDialog mDialog = null;
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
    private OnItemClickListener mSetting2Listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position == 0) {
                Intent feedBackIntent = new Intent(MainActivity.this,
                        CommonWebViewActivity.class);
                feedBackIntent.putExtra("title",
                        getResources().getString(R.string.feed_back));
                String token = Router.INSTANCE.getAccessToken().get();
                String url = String.format(getString(R.string.feed_back_url), "", token);
                feedBackIntent.putExtra("content_url", getString(R.string.feed_back_url));
                startActivity(feedBackIntent);
            } else if (position == 1) {
                Intent feedBackIntent = new Intent(MainActivity.this,
                        ConcerningActivity.class);
                startActivity(feedBackIntent);
            } else if (position == 2) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.logout_confirm_title)
                        .setItems(
                                new String[]{getResources().getString(
                                        R.string.logout)},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mDialog = ProgressDialog.show(MainActivity.this, null, getResources().getString(R.string.on_loading));
                                        ThirdPartySDKManager.getInstance(getApplicationContext()).logout(getApplicationContext());
                                        PreferenceManager.getInstance(getApplicationContext()).clearThirdPartyLoginMsg();
                                        Router.getOAuthModule().visitor(() -> {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    getResources().getString(
                                                            R.string.logout_msg),
                                                    Toast.LENGTH_LONG).show();
                                            mBtnLogin.setVisibility(View.VISIBLE);
                                            mThirdPartyUserName.setVisibility(View.GONE);
                                            mThirdPartyUserHead.setVisibility(View.GONE);
//                                            mViewSwitcher.showPrevious();
                                            mSettingArray = getResources()
                                                    .getStringArray(
                                                            R.array.settings2);
                                            mSettingList2.setAdapter(mSettingAdapter2);
                                            ;
//                                            mSettingList2
//                                                    .setAdapter(new ArrayAdapter<String>(
//                                                            MainActivity.this,
//                                                            android.R.layout.simple_list_item_1,
//                                                            settingStr));
                                            if (mDialog != null) {
                                                mDialog.dismiss();
                                                mDialog = null;
                                            }
                                        }, errorMessage -> {
                                            if (mDialog != null) {
                                                mDialog.dismiss();
                                                mDialog = null;
                                            }
                                        });

                                    }
                                }).create().show();
            }
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    };
    private OnClickListener mUserInfoDetail = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, UserInfoActivity.class);
//            //startActivity(intent);
            intent.setClass(MainActivity.this, ImageChooseActivity.class);
            startActivity(intent);
        }
    };
    private OnClickListener loginBtnListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // mViewSwitcher.showNext();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    msg += "Click edit";
                    break;
            }

            if (!msg.equals("")) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT)
                        .show();
            }
            return true;
        }
    };
    private ArrayList<String> mLabelData = new ArrayList<String>();
    private BaseAdapter mSettingListAdapter = new BaseAdapter() {
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
            if (position == 0) {
                image.setImageResource(R.drawable.setting_home);
                textView.setText(R.string.main_page);
                textMsg.setVisibility(View.GONE);
            } else if (position == 1) {
                image.setImageResource(R.drawable.setting_favor);
                textView.setText(R.string.favor_page);
                textMsg.setVisibility(View.GONE);
            } else if (position == 2) {
                image.setImageResource(R.drawable.setting_msg);
                textView.setText(R.string.msg_page);
                textMsg.setVisibility(View.GONE);
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
            // TODO Auto-generated method stub
            return 3;
        }
    };
    private String[] mSettingArray = null;
    private BaseAdapter mSettingAdapter2 = new BaseAdapter() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void registeViews() {
        setSupportActionBar(mToolBar);
        mToolBar.setOnMenuItemClickListener(onMenuItemClick);
        mLocationView = LayoutInflater.from(this).inflate(
                R.layout.location_layout, null);
        mLocationView.setOnClickListener(mLocationListener);
        mToolBar.addView(mLocationView);
        Optional<TinyUser> user = Router.INSTANCE.getCurrentUser();
        if (user.isPresent()) {
            mBtnLogin.setVisibility(View.GONE);
            mThirdPartyUserName.setVisibility(View.VISIBLE);
            mThirdPartyUserHead.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setText(user.get().getNickName());
            try {

                Picasso.with(getApplicationContext())
                        .load(user.get().getAvatarURL().get())
                        .placeholder(R.drawable.default_user_head)
                        .into(mThirdPartyUserHead);
            } catch (Exception e) {
                mThirdPartyUserHead.setImageResource(R.drawable.default_user_head);
            }
            mSettingArray = getResources()
                    .getStringArray(
                            R.array.settings1);
            mSettingList2.setAdapter(mSettingAdapter2);
            ;
        } else {
            mSettingArray = getResources().getStringArray(R.array.settings2);
            mSettingList2.setAdapter(mSettingAdapter2);
            ;
            mBtnLogin.setVisibility(View.VISIBLE);
            mThirdPartyUserName.setVisibility(View.GONE);
            mThirdPartyUserHead.setVisibility(View.GONE);
        }
        mSettingList2.setOnItemClickListener(mSetting2Listener);
        titles = getResources().getStringArray(R.array.fragment_title);
        mToolBar.setTitle(titles[0]);
        mBadgeView = initBadgeView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mBtnLogin.setOnClickListener(loginBtnListener);
        mSettingList.setAdapter(mSettingListAdapter);
        mSettingList.setOnItemClickListener(mSetting1Listener);
        mUserInfo.setOnClickListener(mUserInfoDetail);
        mFragmentList = new ArrayList<Fragment>();
        mMainFragment = new MainFragment(MainFragment.MAIN_PAGE, MainActivity.this);
        mFragmentList.add(mMainFragment);
        mFavorFragment = new MainFragment(MainFragment.FAVOR_PAGE, MainActivity.this);
        mFragmentList.add(mFavorFragment);
        mMsgFragment = new MessageFragment(MainActivity.this);
        mFragmentList.add(mMsgFragment);
        mFragmentMng = getSupportFragmentManager();
        mFragmentTransaction = mFragmentMng.beginTransaction();
        mFragmentTransaction.add(R.id.main_content_container,
                mFragmentList.get(0));
        mFragmentTransaction.commit();
        mToolBar.setTitle(titles[0]);
        checkVersion();
    }

    private BadgeView initBadgeView() {
        ViewGroup badgeParent = (ViewGroup) findViewById(R.id.badge_parent);
        final BadgeView badgeView = new BadgeView(getApplicationContext(),
                badgeParent);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
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
        badgeView.setBadgeMargin(width * 2 / 5, height / 3);
        badgeView.setFocusable(false);
        return badgeView;
    }

    private void showBadgeView() {
        if (mBadgeView == null) {
            mBadgeView = initBadgeView();
        }
        if (mBadgeView != null) {
            mBadgeView.show();
        }
    }

    private void removeBadgeView() {
        if (mBadgeView != null) {
            mBadgeView.hide();
        }
    }

    ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTask.getInstance().exit();
    }

    @Override
    protected void onUserInfoGot(boolean bSucces) {
        super.onUserInfoGot(bSucces);
        if (bSucces) {
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
            mSettingList2.setAdapter(mSettingAdapter2);
//            mSettingList2
//                    .setAdapter(new ArrayAdapter<String>(
//                            MainActivity.this,
//                            android.R.layout.simple_list_item_1,
//                            settingStr));
        }
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
                // case 0: // has update
                // case 1: // has no update
                // case 2: // none wifi
                // case 3: // time out
            }
        });

        UmengUpdateAgent.update(this);
    }

    private void showUpdateDialog(final String downloadUrl, final String message) {
        AlertDialog.Builder updateAlertDialog = new AlertDialog.Builder(this);
        updateAlertDialog.setIcon(R.drawable.icon);
        updateAlertDialog.setTitle(R.string.app_name);
        updateAlertDialog.setMessage(getString(R.string.update_hint, message));
        updateAlertDialog.setNegativeButton(R.string.update_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(downloadUrl)));
                        } catch (Exception ex) {

                        }
                    }
                }).setPositiveButton(R.string.dialog_no, null);
        if (!isFinishing())
            updateAlertDialog.show();
    }
}
