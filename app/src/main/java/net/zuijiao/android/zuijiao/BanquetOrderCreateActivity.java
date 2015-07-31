package net.zuijiao.android.zuijiao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.BanquentCapacity;
import com.zuijiao.android.zuijiao.model.Banquent.OrderCreateErrorMessage;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.model.OrderAuth;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.thirdopensdk.Alipay;
import com.zuijiao.thirdopensdk.WeixinPay;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by yitianhao on 2015/7/22.
 * the activity you can make a banquet order , and call the third party payment ;
 */
@ContentView(R.layout.activity_banquet_order_create)
public class BanquetOrderCreateActivity extends BaseActivity implements View.OnClickListener {
    private static final int VERIFY_PHONE = 9001;
    @ViewInject(R.id.banquet_order_create_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.banquet_order_create_name)
    private TextView mBanquetName;
    @ViewInject(R.id.banquet_order_create_time)
    private TextView mBanquetTime;
    @ViewInject(R.id.banquet_order_create_price)
    private TextView mBanquetPrice;
    @ViewInject(R.id.banquet_order_create_phone)
    private TextView mBanquetPhone;
    @ViewInject(R.id.banquet_order_create_remark)
    private TextView mBanquetRemark;
    @ViewInject(R.id.banquet_order_create_num)
    private TextView mBanquetNum;
    @ViewInject(R.id.banquet_order_create_content)
    private TextView mBanquetContent;
    @ViewInject(R.id.banquet_order_create_total_price)
    private TextView mBanquetTotalPrice;


    //    @ViewInject(R.id.order_create_total_price)
//    private TextView mOrderTotalPrice;
    @ViewInject(R.id.banquet_order_create_plus)
    private Button mBanquetPlus;
    @ViewInject(R.id.banquet_order_create_subtract)
    private Button mBanquetSubtract;
    //    @ViewInject(R.id.order_create_pay)
//    private Button mOrderPay;
    @ViewInject(R.id.banquet_order_create_scrollview)
    private ScrollView mScrollView;
    @ViewInject(R.id.order_create_bottom)
    private RelativeLayout mBottomView;
    @ViewInject(R.id.banquet_detail_bottom_price)
    private TextView mBottomPriceTv;
    @ViewInject(R.id.banquet_detail_bottom_text2)
    private TextView mBottomPriceUnitTv;
    @ViewInject(R.id.banquet_detail_bottom_date)
    private TextView mBottomOrderCountTv;
    @ViewInject(R.id.banquet_detail_bottom_order)
    private Button mBottomCommitBtn;
    @ViewInject(R.id.banquet_detail_bottom_text1)
    private TextView mBottomText1;
    public static Banquent mBanquent;
    private String[] weekDays;
    private String mRemark;
    private String editRemark = null;
    private String verifyCode = "";
    private String phoneNum;
    private int attendeeNum = 1;


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
        //getSupportActionBar().setTitle(getString(R.string.order_create));
        if (phoneNum != null && !phoneNum.equals("")) {
            mBanquetPhone.setText(phoneNum);
            mBanquetPhone.setTextColor(getResources().getColor(R.color.tv_deep_gray));
        }
        initViewsByBanquet();
        mBanquetPhone.setOnClickListener(this);
        mBanquetRemark.setOnClickListener(this);
        mBanquetPlus.setOnClickListener(this);
        mBanquetSubtract.setOnClickListener(this);
        mBottomCommitBtn.setText(R.string.place_the_order);
        mBottomOrderCountTv.setText(String.format(getString(R.string.total_person_count), attendeeNum));
        mBottomCommitBtn.setOnClickListener(this);
        mBottomText1.setVisibility(View.VISIBLE);
        mBottomPriceUnitTv.setText(R.string.yuan);
        mBanquetNum.setText(attendeeNum + getString(R.string.people));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBottomView.getLayoutParams();
        mBottomPriceTv.measure(0, 0);
        int priceHeight = mBottomPriceTv.getMeasuredHeight();
        mBottomOrderCountTv.measure(0, 0);
        int dateHeight = mBottomOrderCountTv.getMeasuredHeight();
        int margin = (int) (3 * getResources().getDimension(R.dimen.end_z));
        params.height = priceHeight + dateHeight + margin;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
        layoutParams.bottomMargin = params.height;
    }

    private void initViewsByBanquet() {
        mBanquetTime.setText(formatDate(mBanquent.getTime()));
        mBanquetPrice.setText(String.format(getString(R.string.price_per_one), mBanquent.getPrice()));
        mBanquetTotalPrice.setText(String.format("%.2f", mBanquent.getPrice()) + getString(R.string.yuan));
        mBanquetName.setText(mBanquent.getTitle());
        mBottomPriceTv.setText(String.format(getString(R.string.order_total_price), mBanquent.getPrice()));
        BanquentCapacity banquentCapacity = mBanquent.getBanquentCapacity();
        if (banquentCapacity.getMax() == banquentCapacity.getMin()) {
            mBanquetContent.setText(String.format(getString(R.string.banquent_capacity_simple),
                    banquentCapacity.getMin(),
                    banquentCapacity.getCount()));
        } else {
            mBanquetContent.setText(String.format(getString(R.string.banquent_capacity_muilt),
                    banquentCapacity.getMin(),
                    banquentCapacity.getMax(),
                    banquentCapacity.getCount()));
        }
        if (attendeeNum <= 1) {
            attendeeNum = 1;
            mBanquetSubtract.setEnabled(false);
            mBanquetSubtract.setTextColor(Color.WHITE);
            if (banquentCapacity.getMax() - banquentCapacity.getCount() <= 1) {
                mBanquetPlus.setEnabled(false);
                mBanquetPlus.setTextColor(Color.WHITE);
            }
        }
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
            case R.id.banquet_detail_bottom_order:
                if (phoneNum == null || phoneNum.equals("")) {
                    AlertDialog dialog = new AlertDialog.Builder(BanquetOrderCreateActivity.this)
                            .setTitle(getString(R.string.alert))
                            .setMessage(getString(R.string.input_phone_num))
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, VerifyPhoneNumActivity.class);
                                    intent.putExtra("from_order", true);
                                    startActivityForResult(intent, VERIFY_PHONE);
                                }
                            }).create();
                    Window window = dialog.getWindow();
                    window.setWindowAnimations(R.style.dialogWindowAnim);
                    dialog.show();
                    return;
                }
                createDialog();
                if (mRemark == null || mRemark.equals("")) {
                    mRemark = " ";
                }

                Router.getBanquentModule().createOrder(mBanquent.getIdentifier(), mRemark, attendeeNum, phoneNum, verifyCode, new OneParameterExpression<Orders>() {
                    @Override
                    public void action(Orders orders) {
                        Intent intent1 = new Intent(mContext, BanquetOrderActivity.class);
                        intent1.putExtra("order", orders.getOrder());
                        intent1.putExtra("contact_phone_num", phoneNum);
                        intent1.putExtra("attendeeNum", attendeeNum);
                        intent1.putExtra("isCreate", true);
                        startActivity(intent1);
                        finalizeDialog();
                        finish();
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        mRemark = null;
                        if (s.contains("retrofit.RetrofitError:")) {
                            Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                            finalizeDialog();
                        } else {
                            try {
                                JSONObject object = new JSONObject(s);
                                JSONObject json = object.getJSONObject("error");
                                String reason = json.getString("reason");
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.MOBILE)) {
                                    Toast.makeText(mContext, getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                                }
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.EVENTID)) {
                                    Toast.makeText(mContext, getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                                }
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.QUANTITY)) {
                                    Toast.makeText(mContext, getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                                }
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.CLOSED)) {
                                    Toast.makeText(mContext, getString(R.string.event_close), Toast.LENGTH_SHORT).show();
                                }
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.OVERTIME)) {
                                    Toast.makeText(mContext, getString(R.string.event_overtime), Toast.LENGTH_SHORT).show();
                                }
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.OUTOFSTOCK)) {
                                    Toast.makeText(mContext, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                                }
                                if (reason.equalsIgnoreCase(OrderCreateErrorMessage.BEYONDMAXQUANTITY)) {
                                    Toast.makeText(mContext, getString(R.string.beyond_max_quantity), Toast.LENGTH_SHORT).show();
                                }
                                LinkedList<Activity> list = ActivityTask.getInstance().getActivitiesList();
                                for (Activity activity : list) {
                                    if (activity instanceof BanquetDetailActivity) {
                                        try {
                                            activity.finish();
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }
                                finalizeDialog();
                                Intent intent = new Intent();
                                intent.setAction(MessageDef.ACTION_ORDER_CREATED);
                                intent.putExtra("tabIndex", 1);
                                sendBroadcast(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                break;
            case R.id.banquet_order_create_phone:
                Intent intent = new Intent(mContext, VerifyPhoneNumActivity.class);
                intent.putExtra("from_order", true);
                startActivityForResult(intent, VERIFY_PHONE);
                break;
            case R.id.banquet_order_create_remark:
                createGeneralEditTextDialog(mRemark
                        , getString(R.string.remark)
                        , getString(R.string.remark_to_host)
                        , 1, 150);
                break;
            case R.id.banquet_order_create_subtract:
                if (!mBanquetPlus.isEnabled()) {
                    mBanquetPlus.setEnabled(true);
                    mBanquetPlus.setTextColor(Color.BLACK);
                }
                if (--attendeeNum <= 1) {
                    attendeeNum = 1;
                    mBanquetSubtract.setEnabled(false);
                    mBanquetSubtract.setTextColor(Color.WHITE);
                }
                mBanquetNum.setText(attendeeNum + getString(R.string.people));
                mBottomOrderCountTv.setText(String.format(getString(R.string.total_person_count), attendeeNum));
                mBanquetTotalPrice.setText(String.format("%.2f", mBanquent.getPrice() * attendeeNum) + getString(R.string.yuan));
                mBottomPriceTv.setText(String.format(getString(R.string.order_total_price), mBanquent.getPrice() * attendeeNum));
                break;
            case R.id.banquet_order_create_plus:
                if (!mBanquetSubtract.isEnabled()) {
                    mBanquetSubtract.setEnabled(true);
                    mBanquetSubtract.setTextColor(Color.BLACK);
                }
                if (++attendeeNum >= mBanquent.getBanquentCapacity().getMax() - mBanquent.getBanquentCapacity().getCount()) {
                    attendeeNum = mBanquent.getBanquentCapacity().getMax() - mBanquent.getBanquentCapacity().getCount();
                    mBanquetPlus.setEnabled(false);
                    mBanquetPlus.setTextColor(Color.WHITE);
                    Toast.makeText(mContext, String.format(getString(R.string.max_quantify), attendeeNum), Toast.LENGTH_SHORT).show();
                }
                mBanquetNum.setText(attendeeNum + getString(R.string.people));
                mBottomOrderCountTv.setText(String.format(getString(R.string.total_person_count), attendeeNum));
                mBanquetTotalPrice.setText(String.format("%.2f", mBanquent.getPrice() * attendeeNum) + getString(R.string.yuan));
                mBottomPriceTv.setText(String.format(getString(R.string.order_total_price), mBanquent.getPrice() * attendeeNum));
                break;
        }
    }

    /**
     * show date as a specified format;
     *
     * @param date
     * @return formatted date
     */
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
        AlertDialog alertDialog = new AlertDialog.Builder(BanquetOrderCreateActivity.this).setView(contentView).setTitle(title).setPositiveButton(getString(R.string.save),
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
        Window window = alertDialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
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
