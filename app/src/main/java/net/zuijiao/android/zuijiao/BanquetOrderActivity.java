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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    @ViewInject(R.id.banquet_order_pay_way_list)
    private ListView mPayWayList;
    public static Banquent mBanquent;
    private String[] weekDays;
    private String mRemark;
    private String editRemark = null;
    private String verifyCode = "";
    private String phoneNum;
    //for display
    private String[] payWayRes;
    //for network request
    private String[] payWayStr;
    private int mSelectedPayWay = 0;

    private AdapterView.OnItemClickListener mPayWaySwitcher = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSelectedPayWay == position) return;
            mSelectedPayWay = position;
            mBottomPayWay.setText(getString(R.string.use) + payWayRes[mSelectedPayWay]);
            mPayWayAdapter.notifyDataSetChanged();
        }
    };
    private BaseAdapter mPayWayAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return payWayRes.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.pay_way_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.pay_way_text);
            ImageView image = (ImageView) convertView.findViewById(R.id.pay_way_image);
            if (mSelectedPayWay == position) {
                image.setVisibility(View.VISIBLE);
            } else image.setVisibility(View.INVISIBLE);
            textView.setText(payWayRes[position]);
            return convertView;
        }
    };

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        if (mTendIntent != null) {
            mBanquent = (Banquent) mTendIntent.getSerializableExtra("banquet");
            phoneNum = mTendIntent.getStringExtra("contact_phone_num");
        }
        if (mBanquent == null) {
            finish();
            return;
        }
//        getSupportActionBar().setTitle(mBanquent.getTitle());
        getSupportActionBar().setTitle(getString(R.string.detail_order));
        payWayRes = getResources().getStringArray(R.array.pay_way);
        payWayStr = getResources().getStringArray(R.array.pay_way_str);
        mPayWayList.setAdapter(mPayWayAdapter);
//        setListViewHeightBasedOnChildren(mPayWayList);
        mPayWayList.setOnItemClickListener(mPayWaySwitcher);
        if (phoneNum != null && !phoneNum.equals("")) {
            mBanquetPhone.setText(phoneNum);
            mBanquetPhone.setTextColor(getResources().getColor(R.color.tv_deep_gray));
        }
        initViewsByBanquet();
        mBanquetPhone.setOnClickListener(this);
        mBanquetRemark.setOnClickListener(this);
        mPayBtn.setOnClickListener(this);
    }

    private void initViewsByBanquet() {
        mBanquetTime.setText(formatDate(mBanquent.getTime()));
        mBanquetPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
//        mBottomPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
        mBottomPrice.setText(String.valueOf(mBanquent.getPrice()));
        mBottomPayWay.setText(getString(R.string.use) + payWayRes[mSelectedPayWay]);
        mBanquetLocation.setText(mBanquent.getAddress());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VERIFY_PHONE && resultCode == RESULT_OK) {
            verifyCode = data.getStringExtra("verify_code");
            phoneNum = data.getStringExtra("verified_phone_num");
            mBanquetPhone.setText(phoneNum);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banquet_order_bottom_pay:
                if (phoneNum == null || phoneNum.equals("")) {
                    new AlertDialog.Builder(BanquetOrderActivity.this)
                            .setTitle(getString(R.string.alert))
                            .setMessage(getString(R.string.input_phone_num))
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, VerifyPhoneNumActivity.class);
                                    intent.putExtra("from_order", true);
                                    startActivityForResult(intent, VERIFY_PHONE);
                                }
                            }).create().show();
                    ;
                    return;
                }
                createDialog();
                if (mRemark == null || mRemark.equals("")) {
                    mRemark = " ";
                }
                Router.getBanquentModule().createOrder(mBanquent.getIdentifier(), phoneNum, verifyCode, mRemark, payWayStr[mSelectedPayWay], new OneParameterExpression<OrderAuth>() {
                    @Override
                    public void action(OrderAuth orderAuth) {
                        Log.d("pay_interface", "result_success");
                        if (mSelectedPayWay == 0) {
                            new WeixinPay(BanquetOrderActivity.this).pay(orderAuth);
                        } else {
                            new Alipay(BanquetOrderActivity.this).pay(orderAuth.getQuery());
                        }
                        finalizeDialog();
//                        finalizeDialog();
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        mRemark = null;
                        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                        Log.d("pay_interface", "result_failed");
                    }
                });
                break;
            case R.id.banquet_order_banquet_phone:
                Intent intent = new Intent(mContext, VerifyPhoneNumActivity.class);
                intent.putExtra("from_order", true);
                startActivityForResult(intent, VERIFY_PHONE);
                break;
            case R.id.banquet_order_banquet_remark:
                createGeneralEditTextDialog(mRemark
                        , getString(R.string.remark)
                        , getString(R.string.remark_to_host)
                        , 1, 150);
                break;
        }
    }

    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(mContext.getString(R.string.month_day), date.getMonth() + 1, date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(String.format(mContext.getString(R.string.banquet_format_time), date.getHours(), date.getMinutes()));
        strBuilder.append(" ");
        return strBuilder.toString();
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null)
                continue;
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));
    }


    private void createGeneralEditTextDialog(String message, String title, String etHint, int lineNum, int maxText) {
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
                editRemark = s.toString();
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
        AlertDialog alertDialog = new AlertDialog.Builder(BanquetOrderActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRemark = editRemark;
                        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive())
                            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                        if (mRemark != null && !mRemark.equals("")) {
                            mBanquetRemark.setText(mRemark);
                            mBanquetRemark.setTextColor(getResources().getColor(R.color.tv_deep_gray));
                        } else {
                            mBanquetRemark.setText(getString(R.string.none));
                            mBanquetRemark.setTextColor(getResources().getColor(R.color.tv_deep_gray));
                        }
                    }
                }).create();
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
