package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.SellerAccount;
import com.zuijiao.android.zuijiao.model.Banquent.SellerStatus;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.AlertDialogUtil;

/**
 * Created by mengdongya on 2015/8/7.
 */
@ContentView(R.layout.activity_receiving_account)
public class ReceivingAccountActivity extends BaseActivity {
    @ViewInject(R.id.receiving_account_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.receiving_account_name)
    private TextView mAccountName;
    @ViewInject(R.id.receiving_account_bank)
    private TextView mAccountBank;
    @ViewInject(R.id.receiving_account_cardnumber)
    private TextView mAccountCardNumber;
    @ViewInject(R.id.account_bank_account_container)
    private LinearLayout mBankAccountContainer;
    @ViewInject(R.id.account_bank_name_container)
    private LinearLayout mBankNameContainer;
    @ViewInject(R.id.account_real_name_container)
    private LinearLayout mRealNameContainer;
    @ViewInject(R.id.receiving_account_notice)
    private TextView mReceivingAccountNotice;
    private String mCurrentEdit = null;
    private static final int CARD_NUMBER_LENGTH = 19;

    private SellerStatus mSellerStatus = null;
    private String mEditName , mEditBank , mEditAccount ;


    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.receiving_account));
        if (Router.getInstance().getSellerStatus().isPresent()) {
            mSellerStatus = Router.getInstance().getSellerStatus().get();
            registerViewsByStatus();
        } else {
            checkSellerStatus();
        }
    }

    private void registerViewsByStatus() {
        if (mSellerStatus == null)
            return;
        SellerStatus.BankStatus bankStatus = mSellerStatus.getBankStatus();
        if (bankStatus == null || bankStatus.equals(SellerStatus.BankStatus.unfinished)) {
            mReceivingAccountNotice.setVisibility(View.INVISIBLE);
            mBankAccountContainer.setOnClickListener(mListener);
            mBankNameContainer.setOnClickListener(mListener);
            mRealNameContainer.setOnClickListener(mListener);
        }else{
            mBankAccountContainer.setOnClickListener(null);
            mBankNameContainer.setOnClickListener(null);
            mRealNameContainer.setOnClickListener(null);
            Router.getBanquentModule().sellerAccount(new OneParameterExpression<SellerAccount>() {

                @Override
                public void action(SellerAccount sellerAccount) {
                    mAccountName.setText(sellerAccount.getName());
                    mAccountBank.setText(sellerAccount.getBank());
                    mAccountCardNumber.setText(sellerAccount.getCard());
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Toast.makeText(mContext , R.string.notify_net2 , Toast.LENGTH_SHORT).show();
                }
            });
            mReceivingAccountNotice.setVisibility(View.VISIBLE);
            mReceivingAccountNotice.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            mReceivingAccountNotice.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mSellerStatus == null
                || mSellerStatus.getBankStatus().equals(SellerStatus.BankStatus.unfinished)){
            getMenuInflater().inflate(R.menu.account_bind, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_bind_account){
            // todo network operation ;
            if(checkStr(mEditAccount)&& checkStr(mEditBank )&& checkStr(mEditName)){
                AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                String content = String.format(getString(R.string.format_bank_information) , mEditName, mEditBank , mEditAccount) ;
                alertDialogUtil.createPromptDialog(ReceivingAccountActivity.this, getString(R.string.confirm_bank_information),content);
                alertDialogUtil.setButtonText(getString(R.string.confirm), getString(R.string.cancel));
                alertDialogUtil.setContentTextSize(20);
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        alertDialogUtil.dismissDialog();
                       Router.getBanquentModule().updateBankInfo(mEditBank, mEditName, mEditAccount, new LambdaExpression() {
                                   @Override
                                   public void action() {
                                       checkSellerStatus() ;
                                   }
                               },
                               new OneParameterExpression<String>() {
                                   @Override
                                   public void action(String error) {
                                       Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                   }
                               });
                    }
                });
                alertDialogUtil.showDialog();
            }else{
                Toast.makeText(mContext , R.string.bank_information_uncompleted , Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkSellerStatus() {
        Router.getAccountModule().sellerStatus(new OneParameterExpression<SellerStatus>() {
            @Override
            public void action(SellerStatus sellerStatus) {
                Router.getInstance().setSellerStatus(Optional.of(sellerStatus));
                mSellerStatus = sellerStatus ;
                registerViewsByStatus();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.account_real_name_container:
                    createGeneralEditTextDialog(null, getString(R.string.account_name), getString(R.string.real_name_hint), 1, 5, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(checkStr(mCurrentEdit)){
                                mEditName = mCurrentEdit ;
                                mAccountName.setText(mEditName);
                            }
                        }
                    });
                    break;
                case R.id.account_bank_name_container:
                    createGeneralEditTextDialog(null, getString(R.string.account_bank), getString(R.string.bank_name_hint), 1, 20, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(checkStr(mCurrentEdit)) {
                                mEditBank = mCurrentEdit;
                                mAccountBank.setText(mEditBank);
                            }
                        }
                    });
                    break;
                case R.id.account_bank_account_container:
                    createGeneralEditTextDialog(null, getString(R.string.card_number), getString(R.string.bank_account_hint), 2, CARD_NUMBER_LENGTH, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(checkStr(mCurrentEdit)) {
                                mEditAccount = mCurrentEdit;
                                mAccountCardNumber.setText(mEditAccount);
                            }
                        }
                    });
                    break;
            }
        }
    };

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
        if(title.equals(getString(R.string.card_number))){
            editText.setKeyListener(new DigitsKeyListener(false, true));
        }
        if (message != null && !message.equals("")) {
            editText.setText(message);
            editText.setSelection(0, message.length());
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        AlertDialog alertDialog = new AlertDialog.Builder(ReceivingAccountActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save), finishListener).create();
        Window window = alertDialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCurrentEdit = null;
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
    boolean checkStr( String str){
        return !(str == null || str.equals(""));
    }
}
