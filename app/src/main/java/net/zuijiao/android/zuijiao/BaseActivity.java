package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.io.File;

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
    protected String updateApkName = "zuijiao-update.apk";
    protected long mUpdateDownloadId = Integer.MIN_VALUE;

    /**
     * listen to the downloading of update package
     */

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mUpdateDownloadId == downId) {
                    String apDir = getExternalFilesDir("").getAbsolutePath();
                    Intent installIntent = new Intent();
                    installIntent.setAction(Intent.ACTION_VIEW);
                    File file = new File(apDir, updateApkName);
                    installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    startActivity(installIntent);
                }
            }
        }
    };
    AlertDialog updateAlertDialog = null;
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
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mReceiver, filter);
        PushAgent.getInstance(mContext).onAppStart();
        ActivityTask.getInstance().addActivity(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        if(BuildConfig.OpenUmengAgent){
//            Log.i("openUmengAgent" ,BuildConfig.OpenUmengAgent + "") ;
//        }
//
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
//        if(BuildConfig.OpenUmengAgent)
//
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        ActivityTask.getInstance().removeActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home )
        {
            if(mDialog == null || !mDialog.isShowing()){
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void createDialog(String message) {
        if(this == null || this.isFinishing())
            return ;
        try{
            if (mDialog != null)
                mDialog.dismiss();
            mDialog = ProgressDialog.show(this, "", message);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void createDialog() {
        if(this == null || this.isFinishing())
            return ;
        try{
            if (mDialog != null && mDialog.isShowing()) return;
            mDialog = ProgressDialog.show(this, "", getString(R.string.on_loading));
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void finalizeDialog() {
        if (mDialog == null) return;
        try {
            mDialog.dismiss();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        mDialog = null;
    }


    /**
     * save auth information in the shared-preference ,called when login finished
     *
     * @param auth AuthorInfo
     */
    private void saveAuthInfo(AuthorInfo auth) {
        if (!Router.getInstance().getCurrentUser().isPresent()) {
            return;
        }
        TinyUser user = Router.getInstance().getCurrentUser().get();
        AuthorInfo authFromUser = new AuthorInfo();
        authFromUser.setHeadPath(user.getAvatarURLSmall().orElse("placeholder"));
        authFromUser.setUserName(user.getNickName());
        authFromUser.setUserId(user.getIdentifier());
        authFromUser.setPassword(auth.getPassword());
        authFromUser.setUid(auth.getUid());
        authFromUser.setToken(auth.getToken());
        authFromUser.setEmail(auth.getEmail());
        authFromUser.setPlatform(auth.getPlatform());
        PreferenceManager.getInstance(mContext).saveThirdPartyLoginMsg(authFromUser);
    }


    /**
     * do authentication before network operation ,
     * at which case when logged in last time app
     * launched but cannot authentic this time
     * (due to network condition or something else),
     * use the auth information stored in shared-preference
     *
     * @param successCallback LambdaExpression
     * @param failureCallback OneParameterExpression
     */

    protected void tryLoginFirst(LambdaExpression successCallback, OneParameterExpression<Integer> failureCallback) {
        AuthorInfo auth = PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg();
        if (ThirdPartySDKManager.getInstance(mContext).isThirdParty(auth.getPlatform())) {
            //auth by third party account
            Router.getOAuthModule().login(auth.getUid(), auth.getPlatform(),
                    Optional.<String>empty(),
                    Optional.of(auth.getToken()),
                    new LambdaExpression() {
                        @Override
                        public void action() {
                            saveAuthInfo(auth);
                            if (successCallback != null)
                                successCallback.action();
                        }
                    },
                    new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer errorMessage) {
                            if (failureCallback != null)
                                failureCallback.action(errorMessage);
                        }
                    });
        } else if ((auth.getEmail() != null) && (!auth.getEmail().equals(""))) {
            //2@2.2
            //c81e728d9d4c2f636f067f89cc14862c
            //auth by zuijiao account
            RouterOAuth.INSTANCE.loginEmailRoutine(auth.getEmail(),
                    auth.getPassword(),
                    Optional.empty(),
                    Optional.empty(),
                    new LambdaExpression() {
                        @Override
                        public void action() {
                            saveAuthInfo(auth);
                            if (successCallback != null)
                                successCallback.action();
                        }
                    },
                    new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer errorMessage) {
                            if (failureCallback != null)
                                failureCallback.action(errorMessage);
                        }
                    }

            );

        } else {
            //visitor mode
            Router.getOAuthModule().visitor(new LambdaExpression() {
                                                @Override
                                                public void action() {
                                                    PreferenceManager.getInstance(mContext).clearThirdPartyLoginMsg();
                                                    //saveAuthInfo(new AuthorInfo()) ;
                                                    if (successCallback != null)
                                                        successCallback.action();
                                                }
                                            },
                    new OneParameterExpression<Integer>() {
                        @Override
                        public void action(Integer errorMessage) {
                            if (failureCallback != null)
                                failureCallback.action(errorMessage);
                        }
                    });
        }
        //download cache
        Cache.INSTANCE.setup();
    }


    /**
     * create dialog and notify visitor to login
     *
     * @param loginCallBack LambdaExpression @Nullable
     */
    protected void notifyLogin(LambdaExpression loginCallBack) {
        if(isFinishing())
            return ;
        if (mNotifyLoginDialog == null || !mNotifyLoginDialog.isShowing()) {
            this.mLoginCallBack = loginCallBack;
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.alert_login_dialog, null);
            TextView tv = (TextView) contentView.findViewById(R.id.fire_login);
            mNotifyLoginDialog = new AlertDialog.Builder(this).setView(contentView).create();
            Window window = mNotifyLoginDialog.getWindow() ;
            window.setWindowAnimations(R.style.dialogWindowAnim);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQ);
                    mNotifyLoginDialog.dismiss();
                    finalizeDialog();
                }
            });
            mNotifyLoginDialog.show();
        }
    }


    /**
     * show update dialog
     *
     * @param downloadUrl apk url
     * @param message     dialog message ,including update information
     */
    protected void showUpdateDialog(final String downloadUrl, final String message) {
        if(this == null || this.isFinishing())
            return ;
        if (updateAlertDialog != null && updateAlertDialog.isShowing()) {
            return;
        }
        View logoutView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.logout_dialog, null);
        AlertDialog updateAlertDialog = new AlertDialog.Builder(this).setView(logoutView).create();
        Window window = updateAlertDialog.getWindow() ;
        window.setWindowAnimations(R.style.dialogWindowAnim);
        ((TextView) logoutView.findViewById(R.id.logout_title)).setText(getString(R.string.new_version));
        ((TextView) logoutView.findViewById(R.id.logout_content)).setText(message);
        Button confirmBtn = (Button) logoutView.findViewById(R.id.logout_btn_confirm);
        Button cancelBtn = (Button) logoutView.findViewById(R.id.logout_btn_cancel);
        confirmBtn.setText(getString(R.string.update));
        cancelBtn.setText(getString(R.string.later));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlertDialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlertDialog.dismiss();
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(downloadUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalFilesDir(mContext, null, updateApkName);
                mUpdateDownloadId = downloadManager.enqueue(request);
            }
        });
        if (!isFinishing())
            updateAlertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOGIN_REQ == requestCode && mLoginCallBack != null)
            mLoginCallBack.action();
    }

    /**
     * replaced by xutils
     */
    @Deprecated
    protected void findViews() {

    }

    /**
     * register views
     */
    protected abstract void registerViews();
}
