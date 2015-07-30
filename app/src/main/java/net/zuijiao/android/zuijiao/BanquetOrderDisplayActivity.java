package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.AlertDialogUtil;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by xiaqibo on 2015/6/25.
 * display a banquet order detail information , called by my order list ;
 */
@ContentView(R.layout.activity_banquet_order_display)
public class BanquetOrderDisplayActivity extends BaseActivity {
    @ViewInject(R.id.order_display_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.banquet_order_display_image_view)
    private ImageView mImage;
    @ViewInject(R.id.banquet_order_display_title)
    private TextView title;
    @ViewInject(R.id.banquet_order_display_date_location)
    private TextView dateLocation;
    //    @ViewInject(R.id.banquet_order_display_status)
//    private TextView status;
    @ViewInject(R.id.order_detail_oder_id)
    private View mOrderNum;
    @ViewInject(R.id.order_detail_oder_total_price)
    private View mOrderTotalPrice;
    @ViewInject(R.id.order_detail_oder_phone)
    private View mOrderPhone;
    @ViewInject(R.id.order_detail_oder_remark)
    private View mOrderRemark;
    @ViewInject(R.id.order_detail_oder_time)
    private View mOrderDate;
    //click to show the banquet detail activity
    @ViewInject(R.id.order_display_clickable_group)
    private View clickableView;
    @ViewInject(R.id.order_notice)
    private TextView mNoticeText;
    @ViewInject(R.id.banquet_order_surplus_status)
    private LinearLayout mOrder_ll_surplus;
    @ViewInject(R.id.banquet_order_surplus_time)
    private TextView mOrder_tv_surplus;
    @ViewInject(R.id.order_detail_status)
    private View mOrderStatus;
    @ViewInject(R.id.order_detail_oder_price)
    private View mOrderPrice;
    @ViewInject(R.id.order_detail_attendee_num)
    private View mAttendeeNum;
    @ViewInject(R.id.order_cancel)
    private Button mOrderCancel;
    @ViewInject(R.id.order_detail_bottom)
    private LinearLayout mOrderBottom;
    @ViewInject(R.id.order_detail_total_price)
    private TextView mOrderDetailTotalPrice;
    @ViewInject(R.id.order_detail_pay)
    private Button mOrderPay;
    private Order mOrder;
    private String[] weekDays;
    private long mSurplusTime = -1;// sec

    /**
     * handler and runnable for surplus time
     */
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mOrder_tv_surplus.setText(formatTime(mSurplusTime));
            if (mSurplusTime == 0) {
                mOrderPay.setText(getString(R.string.timeout_pay));
                mOrderPay.setTextColor(getResources().getColor(R.color.tv_light_gray));
                mOrderPay.setEnabled(false);
                mOrderCancel.setTextColor(getResources().getColor(R.color.tv_light_gray));
                mOrderCancel.setEnabled(false);
                mOrderCancel.setBackgroundResource(R.drawable.order_timeout_btn);
                handler.removeCallbacks(this);
                setResult(MainActivity.ORDER_CANCEL);
            }
            mSurplusTime--;
            handler.postDelayed(this, 1000);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        if (mTendIntent != null) {
            mOrder = (Order) mTendIntent.getSerializableExtra("order");
            if (mSurplusTime == -1) {
                mSurplusTime = mTendIntent.getLongExtra("surplusTime", -1);
            }
        }
        if (mOrder == null) {
            finish();
            return;
        }
        Picasso.with(mContext).load(mOrder.getEvent().getImageUrl()).placeholder(R.drawable.empty_view_greeting).fit().centerCrop().into(mImage);
        title.setText(mOrder.getEvent().getTitle());
        String formatDate = formatDate(mOrder.getEvent().getTime());
        dateLocation.setText(formatDate + getString(R.string.center_dot) + mOrder.getEvent().getAddress());
        String statusStr = "";
        switch (mOrder.getStatus()) {
            case Canceled:
                statusStr = getString(R.string.canceled_banquet);
                break;
            case Waiting:
                statusStr = getString(R.string.waiting_fo_you);
                break;
            case Finished:
                statusStr = getString(R.string.finished_banquet);
                break;
            case Uncomment:
                statusStr = getString(R.string.waiting_comment);
                break;
            case Unpaid:
                statusStr = getString(R.string.waiting_pay);
                break;
            default:
                statusStr = getString(R.string.waiting_fo_you);
                break;
        }
        if (mOrder.getStatus() == OrderStatus.Unpaid) {
            mOrder_ll_surplus.setVisibility(View.VISIBLE);
            mOrderCancel.setVisibility(View.VISIBLE);
            mOrderBottom.setVisibility(View.VISIBLE);
            mOrderDetailTotalPrice.setText(String.format(getString(R.string.order_total_price), mOrder.getTotalPrice()));
            //runnable.run();
            handler.removeCallbacks(runnable);
            handler.post(runnable);
            mOrderCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createPromptDialog(BanquetOrderDisplayActivity.this, null, getString(R.string.order_cancel_content));
                    alertDialogUtil.setButtonText(getString(R.string.order_cancel), getString(R.string.not_cancel));
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            alertDialogUtil.dismissDialog();
                            Router.getBanquentModule().cancelOrder(mOrder.getIdentifier(), new LambdaExpression() {
                                @Override
                                public void action() {
                                    Toast.makeText(mContext, getString(R.string.order_cancel_success), Toast.LENGTH_SHORT).show();
                                    setResult(MainActivity.ORDER_CANCEL);
                                    finish();
                                }
                            }, new LambdaExpression() {
                                @Override
                                public void action() {
                                    Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            });
            mOrderPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BanquetOrderActivity.class);
                    intent.putExtra("order", mOrder);
                    intent.putExtra("surplusTime", mSurplusTime);
                    intent.putExtra("isCreate", false);
                    intent.putExtra("attendeeNum", mOrder.getQuantity());
                    //startActivity(intent);
                    startActivityForResult(intent, MainActivity.ORDER_REQUEST);
                }
            });
        } else {
            mOrder_ll_surplus.setVisibility(View.GONE);
            mOrderCancel.setVisibility(View.GONE);
            mOrderBottom.setVisibility(View.GONE);
        }

        //test end

        fillGenInfo(mOrderStatus, getString(R.string.order_status), statusStr);
        fillGenInfo(mOrderNum, getString(R.string.order_num), mOrder.getSerialNumber());
        fillGenInfo(mOrderPrice, getString(R.string.price), String.format("%.2f", mOrder.getRealPrice()) + getString(R.string.price_unit));
        fillGenInfo(mAttendeeNum, getString(R.string.num), mOrder.getQuantity() + getString(R.string.people));
        fillGenInfo(mOrderTotalPrice, getString(R.string.total_price), String.format("%.2f", mOrder.getTotalPrice()) + getString(R.string.yuan));
        fillGenInfo(mOrderPhone, getString(R.string.mobile_phone), mOrder.getPhoneNumber());
        fillGenInfo(mOrderRemark, getString(R.string.remark), mOrder.getRemark());
        fillGenInfo(mOrderDate, getString(R.string.order_time), mOrder.getCreateTime().toLocaleString());


        mNoticeText.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        mNoticeText.setMovementMethod(LinkMovementMethod.getInstance());
        System.out.println("mOrder.getBanquentIdentifier():" + mOrder.getEvent().getIdentifier());
        clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
                Router.getBanquentModule().theme(mOrder.getEvent().getIdentifier(), new OneParameterExpression<Banquent>() {
                    @Override
                    public void action(Banquent banquent) {
                        Intent intent = new Intent(mContext, BanquetDetailActivity.class);
                        intent.putExtra("banquet", banquent);
                        startActivity(intent);
                        finalizeDialog();
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String errorMsg) {
                        Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });

            }
        });
    }

    private void fillGenInfo(View parentView, String title, String content) {
        ((TextView) parentView.findViewById(R.id.order_detail_item_title)).setText(title);
        TextView contentText = ((TextView) parentView.findViewById(R.id.order_detail_item_content));
        if (content == null || content.equals("")) {
            contentText.setText(getString(R.string.none));
            contentText.setTextColor(getResources().getColor(R.color.tv_light_gray));
        } else {
            contentText.setText(content);
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

    private String formatTime(long time) {
        long minute, second, hour;
        String strTime;
        DecimalFormat df = new DecimalFormat("00");
        if (time <= 0) {
            strTime = String.format(mContext.getString(R.string.surplus_time), "00", "00");
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                strTime = String.format(mContext.getString(R.string.surplus_time), df.format(minute), df.format(second));
            } else {
                hour = minute / 60;
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                strTime = String.format(mContext.getString(R.string.surplus_time), df.format(minute), df.format(second));
            }
        }
        return strTime;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
