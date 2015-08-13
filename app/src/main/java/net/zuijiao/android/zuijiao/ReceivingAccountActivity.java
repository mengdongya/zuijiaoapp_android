package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by mengdongya on 2015/8/7.
 */
@ContentView(R.layout.activity_receiving_account)
public class ReceivingAccountActivity extends BaseActivity{
    @ViewInject(R.id.receiving_account_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.receiving_account_name)
    private TextView mAccountName;
    @ViewInject(R.id.receiving_account_bank)
    private TextView mAccountBank;
    @ViewInject(R.id.receiving_account_cardnumber)
    private TextView mAccountCardNumber;
    @ViewInject(R.id.account_bank_name_container)
    private LinearLayout mBankAccountContainer;
    @ViewInject(R.id.account_bank_name_container)
    private LinearLayout mBankNameContainer;
    @ViewInject(R.id.account_real_name_container)
    private LinearLayout mRealNameContainer ;
    private String mCurrentEdit = null ;
    private static final int CARD_NUMBER_LENGTH = 19 ;
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.receiving_account));
//        Router.getAccountModule().masterInfo();
        mBankAccountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGeneralEditTextDialog(null, getString(R.string.card_number), getString(R.string.bank_account_hint), 2, CARD_NUMBER_LENGTH, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });
        mBankNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGeneralEditTextDialog(null, getString(R.string.account_bank), getString(R.string.bank_name_hint), 1, 20, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });

        mRealNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGeneralEditTextDialog(null, getString(R.string.account_name), getString(R.string.real_name_hint), 1, 5, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });
    }


    private void createGeneralEditTextDialog(String message, String title, String etHint, int lineNum, int maxText, DialogInterface.OnClickListener finishListener) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.nick_name_input_dialog, null);
        TextView textView = (TextView) contentView.findViewById(R.id.tv_et_watcher);
        EditText editText = (EditText) contentView.findViewById(R.id.et_nick_name_input);
        if (maxText == 0) {
            textView.setVisibility(View.GONE);
        } else {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxText)});
        }
        textView.setText(String.format(getString(R.string.nick_name_watcher), 0, maxText));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentEdit = s.toString().trim();
                textView.setText(String.format(getString(R.string.nick_name_watcher), s.length(), maxText));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (lineNum > 1) {
            editText.setHorizontallyScrolling(false);
        }
//        editText.setLines(lineNum);
        editText.setHint(etHint);
        if (message != null && !message.equals("")) {
            editText.setText(message);
            editText.setSelection(0, message.length());
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        AlertDialog alertDialog = new AlertDialog.Builder(ReceivingAccountActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save), finishListener).create();
        Window window = alertDialog.getWindow() ;
        window.setWindowAnimations(R.style.dialogWindowAnim);
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCurrentEdit = null ;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 200);
    }
}
