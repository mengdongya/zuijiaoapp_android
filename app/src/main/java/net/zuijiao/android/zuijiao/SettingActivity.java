package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

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
    }
}
