package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.network.Router;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiaqibo on 2015/5/5.
 */
@ContentView(R.layout.activity_verify_phone)
public class VerifyPhoneNumActivity extends BaseActivity {
    //phone num format checker
    private static Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
    @ViewInject(R.id.verify_number_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.verify_number_et_number)
    private EditText mPhoneNumEditor = null;

    @ViewInject(R.id.verify_number_et_code)
    private EditText mVerifyNumEditor = null;
    @ViewInject(R.id.verify_number_btn_send)
    private Button mSendBtn = null;
    private boolean securityCodeSent = false;

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPhoneNumEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkMobileNumber(s.toString().trim())) {
                    mSendBtn.setEnabled(true);
                } else {
                    mSendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendBtn.setEnabled(false);
                Router.getCommonModule().requestSecurityCode(mPhoneNumEditor.getText().toString().trim(), () -> {
                    Message msg = Message.obtain();
                    msg.arg1 = 60;
                    mHandler.sendMessage(msg);
                    mSendBtn.setText(getString(R.string.re_send_verification_code));
                    securityCodeSent = true;
                }, () -> {
                    mSendBtn.setEnabled(true);
                });
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int second = msg.arg1;
            if (second == 0) {
                mSendBtn.setText(getString(R.string.re_send_verification_code));
                mSendBtn.setEnabled(true);
                return;
            }
            mSendBtn.setText(String.format(getString(R.string.re_send_verification_code_delayed), msg.arg1));
            Message msg1 = Message.obtain();
            msg1.arg1 = --second;
            mHandler.sendMessageDelayed(msg1, 1000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verify_phone, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_confirm_phone_num) {
            //test
//            if(true){
//                Intent intent = new Intent() ;
//                intent.putExtra("verified_phone_num" ,mPhoneNumEditor.getText().toString().trim()) ;
//                setResult(RESULT_OK , intent);
//                finish();
//                return super.onOptionsItemSelected(item) ;
//            }
            if (!securityCodeSent) {
                return super.onOptionsItemSelected(item);
            }
            String securityCode = mVerifyNumEditor.getText().toString().trim();
            if (securityCode == null || securityCode.equals("")) {
                Toast.makeText(mContext, getString(R.string.input_verification_num), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            createDialog();
            Router.getAccountModule().updatePhoneNumber(mPhoneNumEditor.getText().toString().trim(), securityCode, () -> {
                finallizeDialog();
                Intent intent = new Intent();
                intent.putExtra("verified_phone_num", mPhoneNumEditor.getText().toString().trim());
                setResult(RESULT_OK, intent);
                finish();
            }, errorMsg -> {
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                finallizeDialog();
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
