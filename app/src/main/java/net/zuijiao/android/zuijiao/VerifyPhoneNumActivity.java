package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.network.Router;

/**
 * Created by xiaqibo on 2015/5/5.
 */
@ContentView(R.layout.activity_verify_phone)
public class VerifyPhoneNumActivity extends BaseActivity {
    //phone num format checker
//    private static Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-9]))|(18[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
    @ViewInject(R.id.verify_number_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.verify_number_et_number)
    private EditText mPhoneNumEditor = null;

    @ViewInject(R.id.verify_number_et_code)
    private EditText mVerifyNumEditor = null;
    @ViewInject(R.id.verify_number_btn_send)
    private Button mSendBtn = null;
    private boolean securityCodeSent = false;
    private boolean bFromOrder = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int second = msg.arg1;
            mPhoneNumEditor.setEnabled(false);
            if (second == 0) {
                mSendBtn.setText(getString(R.string.re_send_verification_code));
                mSendBtn.setEnabled(true);
                mPhoneNumEditor.setEnabled(true);
                return;
            }
            mSendBtn.setText(String.format(getString(R.string.re_send_verification_code_delayed), msg.arg1));
            Message msg1 = Message.obtain();
            msg1.arg1 = --second;
            mHandler.sendMessageDelayed(msg1, 1000);
        }
    };

    public static boolean checkMobileNumber(String mobileNumber) {
//        boolean flag = false;
//        try {
//            Matcher matcher = regex.matcher(mobileNumber);
//            flag = matcher.matches();
//        } catch (Exception e) {
//            flag = false;
//        }
        if (mobileNumber == null)
            return false;
        if (mobileNumber.startsWith("1") && mobileNumber.length() == 11)
            return true;
        return false;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bFromOrder = mTendIntent.getBooleanExtra("from_order", false);
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

                String phoneNum = mPhoneNumEditor.getText().toString().trim();
                if (checkMobileNumber(phoneNum)) {
                    mSendBtn.setEnabled(false);
                    mPhoneNumEditor.setEnabled(false);
                    Router.getCommonModule().requestSecurityCode(mPhoneNumEditor.getText().toString().trim(), new LambdaExpression() {
                        @Override
                        public void action() {
                            Message msg = Message.obtain();
                            msg.arg1 = 60;
                            mHandler.sendMessage(msg);
                            mSendBtn.setText(getString(R.string.re_send_verification_code));
                            securityCodeSent = true;
                        }
                    }, new LambdaExpression() {
                        @Override
                        public void action() {
                            mSendBtn.setEnabled(true);
                        }
                    });
                } else
                    Toast.makeText(mContext, getString(R.string.input_telephone), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verify_phone, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        InputMethodManager imm = (InputMethodManager) mVerifyNumEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(mVerifyNumEditor.getApplicationWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_confirm_phone_num) {
            if (!securityCodeSent) {
                return super.onOptionsItemSelected(item);
            }
            String securityCode = mVerifyNumEditor.getText().toString().trim();
            if (securityCode == null || securityCode.equals("")) {
                Toast.makeText(mContext, getString(R.string.input_verification_num), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (bFromOrder) {
                createDialog();
                Router.getCommonModule().verifyPhoneNumber(mPhoneNumEditor.getText().toString().trim(), securityCode, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        finalizeDialog();
                        showKeepDialog();
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        finalizeDialog();
                        if (s.contains("412")) {
                            Toast.makeText(mContext, getString(R.string.error_verify_code), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                keepPhoneNum(securityCode, null);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void keepPhoneNum(String securityCode, AlertDialog dialog) {
        createDialog();
        Router.getAccountModule().updatePhoneNumber(mPhoneNumEditor.getText().toString().trim(), securityCode, new LambdaExpression() {
            @Override
            public void action() {
                if (dialog != null)
                    dialog.dismiss();
                if (!bFromOrder) {
                    finalizeDialog();
                    Intent intent = new Intent();
                    intent.putExtra("verified_phone_num", mPhoneNumEditor.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String errorMsg) {
                if (dialog != null)
                    dialog.dismiss();
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                finalizeDialog();
            }
        });
    }

    private void showKeepDialog() {
        View logoutView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.logout_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(VerifyPhoneNumActivity.this).setView(logoutView).create();
        ((TextView) logoutView.findViewById(R.id.logout_content)).setText(getString(R.string.keep_phone_confirm));
        Button negativeBtn = (Button) logoutView.findViewById(R.id.logout_btn_cancel);
        Button positiveBtn = (Button) logoutView.findViewById(R.id.logout_btn_confirm);
        negativeBtn.setText(getString(R.string.no));
        positiveBtn.setText(getString(R.string.yes));
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keepPhoneNum(mVerifyNumEditor.getText().toString().trim(), dialog);
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                intent.putExtra("verified_phone_num", mPhoneNumEditor.getText().toString().trim());
                intent.putExtra("verify_code", mVerifyNumEditor.getText().toString().trim());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
