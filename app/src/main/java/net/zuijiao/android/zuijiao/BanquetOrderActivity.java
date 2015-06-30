package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.OrderAuth;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.thirdopensdk.Alipay;
import com.zuijiao.thirdopensdk.WeixinPay;

import java.util.Date;

/**
 * Created by xiaqibo on 2015/6/15.
 */
@ContentView(R.layout.activity_banquet_order)
public class BanquetOrderActivity extends BaseActivity implements View.OnClickListener {
    private static final int VERIFY_PHONE = 9001;
    @ViewInject(R.id.banquet_order_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.banquet_order_banquet_time)
    private TextView mBanquetTime;
    @ViewInject(R.id.banquet_order_banquet_price)
    private TextView mBanquetPrice;
    @ViewInject(R.id.banquet_order_banquet_location)
    private TextView mBanquetLocation;
    @ViewInject(R.id.banquet_order_banquet_phone)
    private TextView mBanquetPhone;
    @ViewInject(R.id.banquet_order_banquet_remark)
    private TextView mBanquetRemark;
    @ViewInject(R.id.banquet_order_bottom_pay)
    private Button mPayBtn;
    @ViewInject(R.id.banquet_order_bottom_price)
    private TextView mBottomPrice;
    @ViewInject(R.id.banquet_order_bottom_pay_way)
    private TextView mBottomPayWay;

    @ViewInject(R.id.zhifubao_pay)
    private LinearLayout ZhiFubao;
    @ViewInject(R.id.weixin_pay)
    private LinearLayout WeiXin;
    @ViewInject(R.id.payby_alipay)
    private ImageView AlipayImage;
    @ViewInject(R.id.payby_weixin)
    private ImageView WeixinImage;

    private Banquent mBanquent;
    private String[] weekDays;
    private String mRemark = "hao";
    private String verifyCode;
    private String phoneNum;
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        if (mTendIntent != null) {
            mBanquent = (Banquent) mTendIntent.getSerializableExtra("banquet");
        }
        if (mBanquent == null) {
            finish();
            return;
        }
        initViewsByBanquet();
        mBanquetPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VerifyPhoneNumActivity.class);
                intent.putExtra("from_order", true);
                startActivityForResult(intent, VERIFY_PHONE);
            }
        });
        mBanquetRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGeneralEditTextDialog(null, getString(R.string.remark), getString(R.string.remark_to_host), 1, 150, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBanquetRemark.setText(mRemark);
                        mBanquetRemark.setTextColor(getResources().getColor(R.color.tv_deep_gray));
                    }
                });
            }
        });
        mPayBtn.setOnClickListener(this);
        ZhiFubao.setOnClickListener(this);
        WeiXin.setOnClickListener(this);
        WeixinImage.setOnClickListener(this);
        AlipayImage.setOnClickListener(this);
        WeixinImage.setVisibility(View.INVISIBLE);
    }

    private void initViewsByBanquet() {
        mBanquetTime.setText(formatDate(mBanquent.getTime()));
        mBanquetPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
        mBanquetLocation.setText(mBanquent.getAddress());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VERIFY_PHONE && resultCode == RESULT_OK) {
            verifyCode = data.getStringExtra("verify_code");
            phoneNum = data.getStringExtra("verified_phone_num");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zhifubao_pay:
                if(0==AlipayImage.getVisibility()){
                    AlipayImage.setVisibility(View.INVISIBLE);
                    WeixinImage.setVisibility(View.VISIBLE);
                }else{
                    AlipayImage.setVisibility(View.VISIBLE);
                    WeixinImage.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.weixin_pay:
                if(0==WeixinImage.getVisibility()){
                    WeixinImage.setVisibility(View.INVISIBLE);
                    AlipayImage.setVisibility(View.VISIBLE);
                }else{
                    WeixinImage.setVisibility(View.VISIBLE);
                    AlipayImage.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.banquet_order_bottom_pay:
                payForOrder();
                break;
        }
    }

    private void payForOrder(){

        if(0==AlipayImage.getVisibility()){
            Router.getBanquentModule().createOrder(mBanquent.getIdentifier(), phoneNum, verifyCode, mRemark, "alipay", new OneParameterExpression<OrderAuth>() {
                @Override
                public void action(OrderAuth orderAuth) {
                    Log.d("pay_interface", "result_success");
                    String query = orderAuth.getQueryString();
                    new Alipay(BanquetOrderActivity.this).pay(query);
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Log.d("pay_interface", "result_failed");
                }
            });
        }else {
            Router.getBanquentModule().createOrder(mBanquent.getIdentifier(), phoneNum, verifyCode, mRemark, "wxpay", new OneParameterExpression<OrderAuth>() {
                @Override
                public void action(OrderAuth orderAuth) {
                    Log.d("pay_interface", "result_success");
                    new WeixinPay(BanquetOrderActivity.this).pay(orderAuth);
                }
            }, new OneParameterExpression<String>() {
                @Override
                public void action(String s) {
                    Log.d("pay_interface", "result_failed");
                }
            });
        }
    }
    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(mContext.getString(R.string.month_day), date.getMonth(), date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(date.getHours() + ":00");
        strBuilder.append(" ");
        return strBuilder.toString();
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
                textView.setText(String.format(getString(R.string.nick_name_watcher), s.length(), maxText));
                mRemark = s.toString();
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
        AlertDialog alertDialog = new AlertDialog.Builder(BanquetOrderActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save), finishListener).create();
        alertDialog.show();
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
