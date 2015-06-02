package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterOAuth;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.AuthorInfo;

public abstract class BaseActivity extends ActionBarActivity {
    public static final int LOGIN_REQ = 10001;
    protected PreferenceManager mPreferMng = null;
    protected PreferenceInfo mPreferenceInfo = null;
    protected FileManager mFileMng = null;
    protected DBOpenHelper dbMng = null;
    protected Intent mTendIntent = null;
    protected ProgressDialog mDialog = null;
    protected Context mContext = null;
    protected ThirdPartySDKManager authMng = null;
    private LambdaExpression mLoginCallBack;
    private AlertDialog mNotifyLoginDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.lidroid.xutils.ViewUtils.inject(this);
        mContext = getApplicationContext();
        mPreferMng = PreferenceManager.getInstance(mContext);
        mFileMng = FileManager.getInstance(mContext);
        dbMng = DBOpenHelper.getmInstance(mContext);
        authMng = ThirdPartySDKManager.getInstance(this);
        if (savedInstanceState == null) {
            mTendIntent = getIntent();
        }
        registerViews();
        PushAgent.getInstance(mContext).onAppStart();
        ActivityTask.getInstance().addActivity(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTask.getInstance().removeActivity(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    protected void createDialog(String message) {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = ProgressDialog.show(this, "", message);
    }

    protected void createDialog() {
//        if (mDialog != null && mDialog.isShowing()) return ;
//        mDialog = ProgressDialog.show(this, "", getString(R.string.on_loading));
    }

    protected void finalizeDialog() {
        if (mDialog == null) return;
        mDialog.dismiss();
        mDialog = null;
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
                startActivityForResult(intent, LOGIN_REQ);
                mNotifyLoginDialog.dismiss();
                finalizeDialog();
            });
            mNotifyLoginDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOGIN_REQ == requestCode && mLoginCallBack != null)
            mLoginCallBack.action();
    }

    protected void findViews() {

    }

    protected abstract void registerViews();
}
