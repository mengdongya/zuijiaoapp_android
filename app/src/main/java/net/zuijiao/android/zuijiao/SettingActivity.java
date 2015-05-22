package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.model.common.Configuration;
import com.zuijiao.android.zuijiao.model.common.ConfigurationType;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.ThirdPartySDKManager;

/**
 * Created by xiaqibo on 2015/5/4.
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {
    @ViewInject(R.id.setting_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.setting_sb_follow_me)
    private SwitchButton mSb1;
    @ViewInject(R.id.setting_sb_favor_me)
    private SwitchButton mSb2;
    @ViewInject(R.id.setting_sb_comment_me)
    private SwitchButton mSb3;
    @ViewInject(R.id.setting_account_layout)
    private LinearLayout mAccountLayout = null;
    @ViewInject(R.id.setting_logout_btn)
    private Button mLogoutBtn = null;
    @ViewInject(R.id.setting_current_account)
    private TextView mCurrentAccount;
    private Configuration mConfiguration = null;
    private LambdaExpression successCallback = null;
    private String mEmail = null;
    private CompoundButton.OnCheckedChangeListener mSbListener = null;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmail = mPreferMng.getStoredBindEmail();
        mCurrentAccount.setText(mEmail.equals("") ? getString(R.string.un_setting) : mEmail);

        Router.getCommonModule().currentConfiguration(configuration -> registerSwitchButton(configuration), null);
        registerSwitchButton(mConfiguration);
        mLogoutBtn.setOnClickListener((View view) -> {
            logout();
        });
        mAccountLayout.setOnClickListener((View view) -> {
            bindEmail();
        });
        mSb1.setOnCheckedChangeListener(new SBCheckedChangeListener());
        mSb2.setOnCheckedChangeListener(new SBCheckedChangeListener());
        mSb3.setOnCheckedChangeListener(new SBCheckedChangeListener());
    }

    private void bindEmail() {
        if (mEmail.equals("")) {
            Intent intent = new Intent();
            intent.setClass(mContext, BindEmailActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, getString(R.string.email_bound), Toast.LENGTH_SHORT).show();
        }
    }


    private void registerSwitchButton(Configuration config) {
        mConfiguration = config;
        if (mConfiguration == null) {
            mConfiguration = mPreferMng.getConfigs();
        } else {
            mPreferMng.saveConfig(mConfiguration);
        }
        mSb1.setChecked(mConfiguration.isNotifyFollowed());
        mSb2.setChecked(mConfiguration.isNotifyLike());
        mSb3.setChecked(mConfiguration.isNotifyComment());
    }

    private void logout() {
        View logoutView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.logout_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this).setView(logoutView).create();
        logoutView.findViewById(R.id.logout_btn_cancel).setOnClickListener((View v) -> {
            dialog.dismiss();
        });
        logoutView.findViewById(R.id.logout_btn_confirm).setOnClickListener((View v) -> {
            dialog.dismiss();
            createDialog();
            ThirdPartySDKManager.getInstance(mContext).logout(mContext);
            PreferenceManager.getInstance(mContext).clearThirdPartyLoginMsg();
            try {
                FileManager.recommendList.get().clear();
                FileManager.favorGourmets.get().clear();
            } catch (Exception e) {
            }
            Router.getOAuthModule().visitor(() -> {
                backToMain();
            }, errorMessage -> {
                Router.getInstance().setCurrentUser(Optional.empty());
                backToMain();
            });
        });
        dialog.show();
    }

    private void backToMain() {
        finallizeDialog();
        setResult(MainActivity.LOGOUT_RESULT);
        finish();
    }

    @Override
    protected void findViews() {

    }

    private class SBCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private boolean bFromUser = true;

        public void setFromUser(boolean fromUser) {
            this.bFromUser = fromUser;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!bFromUser) {
                bFromUser = true;
            } else {
                ((SwitchButton) buttonView).setEnabled(false);
                boolean switchTo = false;
                ConfigurationType type = ConfigurationType.Follow;
                switch (buttonView.getId()) {
                    case R.id.setting_sb_comment_me:
                        type = ConfigurationType.Comment;
                        switchTo = !mConfiguration.isNotifyComment();
                        successCallback = () -> {
                            mConfiguration.setNotifyComment(!mConfiguration.isNotifyComment());
                        };
                        break;
                    case R.id.setting_sb_favor_me:
                        type = ConfigurationType.Like;
                        switchTo = !mConfiguration.isNotifyLike();
                        successCallback = () -> {
                            mConfiguration.setNotifyLike(!mConfiguration.isNotifyLike());
                        };
                        break;
                    case R.id.setting_sb_follow_me:
                        type = ConfigurationType.Follow;
                        switchTo = !mConfiguration.isNotifyFollowed();
                        successCallback = () -> {
                            mConfiguration.setNotifyFollowed(!mConfiguration.isNotifyFollowed());
                        };
                        break;
                }
                Router.getCommonModule().updateConfiguration(type, switchTo, () -> {
                    buttonView.setEnabled(true);
                    successCallback.action();
                    mPreferMng.saveConfig(mConfiguration);
                }, errorMsg -> {
                    new Handler().postDelayed(() -> {
                        buttonView.setEnabled(true);
                        this.bFromUser = false;
                        ((SwitchButton) buttonView).toggle();
                        Toast.makeText(mContext, getString(R.string.notify_net3), Toast.LENGTH_SHORT).show();
                    }, 500);
                });
            }
        }
    }
}
