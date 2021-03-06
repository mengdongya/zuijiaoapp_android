package net.zuijiao.android.zuijiao;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.zuijiao.android.util.MD5;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.ThirdPartySDKManager;
import com.zuijiao.entity.AuthorInfo;

import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

/**
 * log in activity ,called from sidebar of main activity ;
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements OnClickListener {
    @ViewInject(R.id.iv_weixin)
    private ImageButton mBtnWechat = null;
    @ViewInject(R.id.iv_weibo)
    private ImageButton mBtnWebo = null;
    @ViewInject(R.id.iv_qq)
    private ImageButton mBtnQQ = null;
    private int mLoginType = ThirdPartySDKManager.CLOUD_TYPE_NONE;
//    private ProgressDialog mDialog = null;
    @ViewInject(R.id.login_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.et_login_email)
    private EditText mEmailEdit = null;
    @ViewInject(R.id.et_login_passsword)
    private EditText mPwdEdit = null;
    @ViewInject(R.id.tv_login_register)
    private TextView mRegisterText = null;
    @ViewInject(R.id.button1)
    private Button testBtn = null;
    private String mEmail = null;
    private String mPassword = null;


    /**
     * receive broad cast from third-party sdk log in result ;
     */
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MessageDef.ACTION_LOGIN_FINISH)) {
                onLoginFinish();
            } else if (intent.getAction().equals(MessageDef.ACTION_GET_THIRD_PARTY_USER)) {
                onUserInfoGot(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageDef.ACTION_LOGIN_FINISH);
        filter.addAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void registerViews() {
        mBtnQQ.setOnClickListener(this);
        mBtnWebo.setOnClickListener(this);
        mBtnWechat.setOnClickListener(this);
        mRegisterText.setOnClickListener(this);
        initRegisterTextview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.login) {
            mEmail = mEmailEdit.getText().toString().trim();
            if (mEmail == null || mEmail.equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.notify_empty_email), Toast.LENGTH_SHORT).show();
//                mEmail = "2@2.2" ;
                return super.onOptionsItemSelected(item);
            }
            mPassword = mPwdEdit.getText().toString().trim();
            if (mPassword == null || mPassword.equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.notify_empty_password), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
//                mPassword = "c81e728d9d4c2f636f067f89cc14862c" ;
            }

            try {
                mPassword = MD5.crypt(mPassword);
            } catch (NoSuchAlgorithmException e) {
            }

            mDialog = ProgressDialog.show(LoginActivity.this, null, getResources().getString(R.string.on_loading));
            // login by zuijiao account ;
            Router.getOAuthModule().loginEmailRoutine(mEmail, mPassword, Optional.of(PreferenceManager.mDeviceToken), Optional.<String>empty(), new LambdaExpression() {
                @Override
                public void action() {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    TinyUser user = Router.getInstance().getCurrentUser().get();
                    AuthorInfo auth = new AuthorInfo();
                    auth.setEmail(mEmail);
                    auth.setPlatform("");
                    try {
                        auth.setHeadPath(user.getAvatarURLSmall().get());
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                        auth.setHeadPath("");
                    }
                    auth.setPassword(mPassword);
                    auth.setUserName(user.getNickName());
                    auth.setUserId(user.getIdentifier());
                    mPreferMng.saveThirdPartyLoginMsg(auth);
                    Intent intent = new Intent();
                    intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                    sendBroadcast(intent);
                    finalizeDialog();
                }
            }, new OneParameterExpression<Integer>() {
                @Override
                public void action(Integer errorMessage) {
                    if (errorMessage == 401) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_password), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                    }
                    finalizeDialog();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_weixin:
                mLoginType = ThirdPartySDKManager.CLOUD_TYPE_WEIXIN;
                break;
            case R.id.iv_weibo:
                mLoginType = ThirdPartySDKManager.CLOUD_TYPE_WEIBO;
                break;
            case R.id.iv_qq:
                mLoginType = ThirdPartySDKManager.CLOUD_TYPE_QQ;
                break;
            case R.id.tv_login_register:
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                return;
            default:
                break;
        }
        if (mLoginType != ThirdPartySDKManager.CLOUD_TYPE_NONE) {
            if(mLoginType == ThirdPartySDKManager.CLOUD_TYPE_QQ){
                createDialog();
            }
            authMng.login(mLoginType);
        }
    }

    /**
     * login call back
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * login call back
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authMng.onLoginResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void onUserInfoGot(boolean bSuccess) {
        finalizeDialog();
        if (bSuccess) {
            finish();
        }
    }

    /**
     * login call back
     */
    private void onLoginFinish() {
        if (mLoginType != ThirdPartySDKManager.CLOUD_TYPE_WEIXIN) {
            mDialog = ProgressDialog.show(LoginActivity.this, null, getResources().getString(R.string.on_loading));
        }
    }

    private void initRegisterTextview() {
        String str = getResources().getString(R.string.notify_register);
        SpannableStringBuilder style = new SpannableStringBuilder(str);
//        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); //
//        Spannable wordSpanner = new SpannableString("") ;
        style.setSpan(new ForegroundColorSpan(Color.rgb(137, 60, 52)), str.length() - 4,
                str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        TextView tvColor = (TextView) findViewById(R.id.tv_login_register);
        tvColor.setText(style);
    }
}
