package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.AuthorInfo;

public abstract class BaseActivity extends ActionBarActivity {
    public static final int LOGIN_FOR_FETCH_FAVOR = 0;
    public static final int LOGIN_FOR_FETCH_COMMON = 1;
    public static final int LOGIN_FOR_FETCH_MESSAGE = 2;
    protected PreferenceManager mPreferMng = null;
    protected PreferenceInfo mPreferenceInfo = null;
    protected FileManager mFileMng = null;
    protected DBOpenHelper dbMng = null;
    protected Intent mTendIntent = null;
    protected ProgressDialog mDialog = null;
    protected Context mContext = null;
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MessageDef.ACTION_LOGIN_FINISH)) {
                onLoginFinish();
            } else if (intent.getAction().equals(MessageDef.ACTION_GET_THIRD_PARTY_USER)) {
                onUserInfoGot(true);
            } else if (intent.getAction().equals(MessageDef.ACTION_REFRESH_RECOMMENDATION)) {
                onRecommendationChanged();
            }
        }
    };

    protected void onRecommendationChanged() {

    }

    private LambdaExpression mLoginCallBack;
    private AlertDialog mNotifyLoginDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        com.lidroid.xutils.ViewUtils.inject(this);
        mContext = getApplicationContext();
        mPreferMng = PreferenceManager.getInstance(getApplicationContext());
        if (mPreferMng.getPreferInfo() == null) {
            mPreferMng.initPreferenceInfo();
        }
        mPreferenceInfo = mPreferMng.getPreferInfo();
        mFileMng = FileManager.getInstance(getApplicationContext());
        dbMng = DBOpenHelper.getmInstance(getApplicationContext());
        if (savedInstanceState == null) {
            mTendIntent = getIntent();
        }
        registerViews();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageDef.ACTION_LOGIN_FINISH);
        filter.addAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
        filter.addAction(MessageDef.ACTION_REFRESH_RECOMMENDATION);
        registerReceiver(mReceiver, filter);
        ActivityTask.getInstance().addActivity(this);
    }

    public void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    protected void onLoginFinish() {

    }

    protected void onUserInfoGot(boolean bSuccess) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        ActivityTask.getInstance().removeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void createDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        mDialog = ProgressDialog.show(this, "", getString(R.string.on_loading));
    }

    protected void finallizeDialog() {
        if (mDialog == null) {
            return;
        }
        mDialog.dismiss();
        mDialog = null;
    }

    protected void forwardTo(Class activityName) {
        Intent intent = new Intent();
        intent.setClass(mContext, activityName);
        startActivity(intent);
    }

    protected void forwardToWithBundle(Class activityName, Bundle b) {
        Intent intent = new Intent();
        intent.putExtra("", b);
        intent.setClass(getApplicationContext(), activityName);
        startActivity(intent);
    }

    private void saveAuthInfo(AuthorInfo auth) {
        if (!Router.getInstance().getCurrentUser().isPresent()) {
            return;
        }
        TinyUser user = Router.getInstance().getCurrentUser().get();
        AuthorInfo authFromUser = new AuthorInfo();
        authFromUser.setHeadPath(user.getAvatarURL().orElse("placeholder"));
        authFromUser.setUserName(user.getNickName());
        authFromUser.setUserId(user.getIdentifier());
        authFromUser.setPassword(auth.getPassword());
        authFromUser.setUid(auth.getUid());
        authFromUser.setToken(auth.getToken());
        authFromUser.setEmail(auth.getEmail());
        authFromUser.setPlatform(auth.getPlatform());
        PreferenceManager.getInstance(mContext).saveThirdPartyLoginMsg(authFromUser);
    }

    protected void tryLoginFirst(LambdaExpression successCallback, OneParameterExpression<Integer> failureCallback) {
        AuthorInfo auth = PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg();
        if (ThirdPartySDKManager.getInstance(mContext).isThirdParty(auth.getPlatform())) {
            Router.getOAuthModule().login(auth.getUid(), auth.getPlatform(), Optional.<String>empty(), Optional.of(auth.getToken()), () -> {
                        saveAuthInfo(auth);
                        successCallback.action();
                    },
                    errorMessage -> {
                        failureCallback.action(errorMessage);
                    });
        } else if ((auth.getEmail() != null) && (!auth.getEmail().equals(""))) {
            //2@2.2
            //c81e728d9d4c2f636f067f89cc14862c
            RouterOAuth.INSTANCE.loginEmailRoutine(auth.getEmail(),
                    auth.getPassword(),
                    Optional.empty(),
                    Optional.empty(),
                    () -> {
                        saveAuthInfo(auth);
                        successCallback.action();
                    },
                    errorMessage -> {
                        failureCallback.action(errorMessage);
                    }
            );

        } else {
            Router.getOAuthModule().visitor(() -> {
                        PreferenceManager.getInstance(mContext).clearThirdPartyLoginMsg();
                        //saveAuthInfo(new AuthorInfo()) ;
                        successCallback.action();
                    },
                    errorMessage -> {
                        failureCallback.action(errorMessage);
                    });
        }
        Cache.INSTANCE.setup();
    }

    protected void notifyLogin(LambdaExpression loginCallBack) {
        if (mNotifyLoginDialog == null || !mNotifyLoginDialog.isShowing()) {
            this.mLoginCallBack = loginCallBack;
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.alert_login_dialog, null);
            TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
            mNotifyLoginDialog = new AlertDialog.Builder(this).setView(contentView).create();
            tv.setOnClickListener((View v) -> {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_FOR_FETCH_FAVOR);
                mNotifyLoginDialog.dismiss();
                finallizeDialog();
            });
            mNotifyLoginDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_FOR_FETCH_FAVOR:
            case LOGIN_FOR_FETCH_COMMON:
            case LOGIN_FOR_FETCH_MESSAGE:
                mLoginCallBack.action();
                break;
        }
    }

    @Deprecated
    protected abstract void findViews();

    protected abstract void registerViews();
}
