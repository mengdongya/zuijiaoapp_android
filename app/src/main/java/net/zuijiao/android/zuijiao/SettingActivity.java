package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Router;
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

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLogoutBtn.setOnClickListener((View view) -> {
            logout();
        });
        mAccountLayout.setOnClickListener((View view) -> {
            bindEmail();
        });
    }

    private void bindEmail() {
        User user = null;
        if (user.getContactInfo().get().getIsEmailBound().equals("")) {

        }
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
            Router.getOAuthModule().visitor(() -> {
                backToMain();
            }, errorMessage -> {
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
}
