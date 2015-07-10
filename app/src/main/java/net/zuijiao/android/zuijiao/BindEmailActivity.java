package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/5/19.
 * bind a email address , not used current ;
 */

@ContentView(R.layout.activity_bind_email)
public class BindEmailActivity extends BaseActivity {

    private final static String EMAIL_REGEX = "^[\\w_\\.+-]*[\\w_\\.-]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    @ViewInject(R.id.bind_email_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.bind_email_et_email)
    private EditText mEtEmail;
    @ViewInject(R.id.bind_email_et_password)
    private EditText mEtPwd;
    @ViewInject(R.id.bind_email_password_confirm)
    private EditText mEtPwdConfirm;


    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bind_email, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_bind_email) {
            String email = mEtEmail.getText().toString().trim();
            if (email == null | email.equals("")) {
                Toast.makeText(mContext, getString(R.string.register_error_email), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!email.matches(EMAIL_REGEX)) {
                Toast.makeText(mContext, getString(R.string.register_error_email_format), Toast.LENGTH_SHORT).show();
                return false;
            }
            String pwd = mEtPwd.getText().toString().trim();
            if (pwd == null || pwd.equals("")) {
                Toast.makeText(mContext, getString(R.string.register_error_pwd), Toast.LENGTH_SHORT).show();
                return false;
            }
            String pwdConfirm = mEtPwdConfirm.getText().toString().trim();
            if (pwdConfirm == null || !pwdConfirm.equals(pwd)) {
                Toast.makeText(mContext, getString(R.string.register_error_pwd_imsame), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (true) {
                Intent intent = new Intent();
                intent.putExtra("bound_email", email);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void findViews() {

    }

}
