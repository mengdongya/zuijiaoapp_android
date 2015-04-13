package net.zuijiao.android.zuijiao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.entity.AuthorInfo;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {
    private final static String EMAIL_REGEX = "^[\\w_\\.+-]*[\\w_\\.-]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    @ViewInject(R.id.register_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.et_regist_name)
    private EditText mEtNickName = null;
    @ViewInject(R.id.et_regist_email)
    private EditText mEtEmail = null;
    @ViewInject(R.id.et_regist_password)
    private EditText mEtPwd = null;
    @ViewInject(R.id.et_regist_password_comfirm)
    private EditText mEtPwdConfirm = null;
    private String mEmail = null;
    private String mNickName = null;
    private String mPwd = null;
    private String mPwdConfirm = null;
    private String mErrorCode = null;
    private ProgressDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                mDialog = ProgressDialog.show(RegisterActivity.this, null, getString(R.string.on_loading));
                if (checkInputState()) {
                    Router.getOAuthModule().registerEmailRoutine(mNickName, null, mEmail, mPwd, Optional.<String>empty(), Optional.<String>empty(), () -> {
                        //register success !
                        Router.getOAuthModule().loginEmailRoutine(mEmail, mPwd, Optional.<String>empty(), Optional.<String>empty(), () -> {

                            //Login success !
                            TinyUser user = Router.getInstance().getCurrentUser().get();
                            AuthorInfo authorInfo = new AuthorInfo();
                            authorInfo.setUserName(user.getNickName());
                            authorInfo.setPassword(mPwd);
                            authorInfo.setPlatform("");
                            authorInfo.setEmail(mEmail);
                            PreferenceManager.getInstance(getApplicationContext()).saveThirdPartyLoginMsg(authorInfo);
                            Intent intent = new Intent();
                            intent.setAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
                            sendBroadcast(intent);
                            intent.setClass(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finallizeDialog();
                        }, errorMessage -> {
                            finallizeDialog();
                            Toast.makeText(getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            //login failed !
                        });
                    }, errorMessage -> {
                        finallizeDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        /// register failed !
                    });
                } else {
                    Toast.makeText(getApplicationContext(), mErrorCode, Toast.LENGTH_SHORT).show();
                    finallizeDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finallizeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private boolean checkInputState() {
        mNickName = mEtNickName.getText().toString().trim();
        if (mNickName == null || mNickName.equals("")) {
            mErrorCode = getString(R.string.register_error_name);
            return false;
        }
        mEmail = mEtEmail.getText().toString().trim();
        if (mEmail == null || mEmail.equals("")) {
            mErrorCode = getString(R.string.register_error_email);
            return false;
        }
        if (!mEmail.matches(EMAIL_REGEX)) {
            mErrorCode = String.format(getString(R.string.register_error_email_format), mEmail);
            return false;
        }
        mPwd = mEtPwd.getText().toString().trim();
        if (mPwd == null || mPwd.equals("")) {
            mErrorCode = getString(R.string.register_error_pwd);
            return false;
        }
        mPwdConfirm = mEtPwdConfirm.getText().toString().trim();
        if (mPwdConfirm == null || !mPwdConfirm.equals(mPwd)) {
            mErrorCode = getString(R.string.register_error_pwd_imsame);
            return false;
        }
        return true;
    }

    @Override
    protected void registeViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
}
