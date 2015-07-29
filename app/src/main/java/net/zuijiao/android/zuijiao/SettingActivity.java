package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.Configuration;
import com.zuijiao.android.zuijiao.model.common.ConfigurationType;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.utils.AlertDialogUtil;

/**
 * setting activity ,called from main-activity side bar
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
        Router.getCommonModule().currentConfiguration(new OneParameterExpression<Configuration>() {
            @Override
            public void action(Configuration configuration) {
                registerSwitchButton(configuration);
            }
        }, null);
        registerSwitchButton(mConfiguration);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        mAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindEmail();
            }
        });
        mSb1.setOnCheckedChangeListener(new SBCheckedChangeListener());
        mSb2.setOnCheckedChangeListener(new SBCheckedChangeListener());
        mSb3.setOnCheckedChangeListener(new SBCheckedChangeListener());
    }

    /**
     * unused
     */
    private void bindEmail() {
        if (mEmail.equals("")) {
            Intent intent = new Intent();
            intent.setClass(mContext, BindEmailActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, getString(R.string.email_bound), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * register switch button by configuration
     *
     * @param config
     */
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

    /**
     * do logout
     */
    private void logout() {
        AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
        alertDialogUtil.createPromptDialog(SettingActivity.this, getString(R.string.alert), getString(R.string.logout_confirm_title));
        alertDialogUtil.setButtonText(getString(R.string.confirm), getString(R.string.cancel));
        alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                alertDialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                alertDialogUtil.dismissDialog();
                createDialog();
                ThirdPartySDKManager.getInstance(mContext).logout(mContext);
                PreferenceManager.getInstance(mContext).clearThirdPartyLoginMsg();
                try {
                    FileManager.recommendList.get().clear();
                    FileManager.favorGourmets.get().clear();
                } catch (Exception e) {
                }
                Router.getOAuthModule().visitor(new LambdaExpression() {
                    @Override
                    public void action() {
                        backToMain();
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        Router.getInstance().setCurrentUser(Optional.empty());
                        backToMain();
                    }
                });
            }
        });
        alertDialogUtil.showDialog();
    }

    private void backToMain() {
        finalizeDialog();
        setResult(MainActivity.LOGOUT_RESULT);
        finish();
    }

    @Override
    protected void findViews() {

    }

    /**
     * switcher listener ;
     */
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
                buttonView.setEnabled(false);
                boolean switchTo = false;
                ConfigurationType type = ConfigurationType.Follow;
                switch (buttonView.getId()) {
                    case R.id.setting_sb_comment_me:
                        type = ConfigurationType.Comment;
                        switchTo = !mConfiguration.isNotifyComment();
                        successCallback = new LambdaExpression() {
                            @Override
                            public void action() {
                                mConfiguration.setNotifyComment(!mConfiguration.isNotifyComment());
                            }
                        };
                        break;
                    case R.id.setting_sb_favor_me:
                        type = ConfigurationType.Like;
                        switchTo = !mConfiguration.isNotifyLike();
                        successCallback = new LambdaExpression() {
                            @Override
                            public void action() {
                                mConfiguration.setNotifyLike(!mConfiguration.isNotifyLike());
                            }
                        };
                        break;
                    case R.id.setting_sb_follow_me:
                        type = ConfigurationType.Follow;
                        switchTo = !mConfiguration.isNotifyFollowed();
                        successCallback = new LambdaExpression() {
                            @Override
                            public void action() {
                                mConfiguration.setNotifyFollowed(!mConfiguration.isNotifyFollowed());
                            }
                        };
                        break;
                }
                Router.getCommonModule().updateConfiguration(type, switchTo, new LambdaExpression() {
                    @Override
                    public void action() {
                        buttonView.setEnabled(true);
                        successCallback.action();
                        mPreferMng.saveConfig(mConfiguration);
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buttonView.setEnabled(true);
                                bFromUser = false;
                                buttonView.toggle();
                                Toast.makeText(mContext, getString(R.string.notify_net3), Toast.LENGTH_SHORT).show();
                            }
                        }, 500);
                    }
                });
            }
        }
    }
}
