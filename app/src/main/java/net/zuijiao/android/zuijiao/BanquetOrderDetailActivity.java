package net.zuijiao.android.zuijiao;


import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Attendee;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.network.Router;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by mengdongya on 2015/8/11.
 */
@ContentView(R.layout.activity_banquet_order_detail)
public class BanquetOrderDetailActivity extends BaseActivity{
    @ViewInject(R.id.banquet_order_detail_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.banquet_order_detail_userinfo)
    private LinearLayout mUserInfo;
    @ViewInject(R.id.banquet_order_detail_head)
    private ImageView mUserImage;
    @ViewInject(R.id.banquet_order_detail_nickname)
    private TextView mUserNickname;
    @ViewInject(R.id.banquet_order_detail_order_number)
    private TextView orderNumber;
    @ViewInject(R.id.banquet_order_detail_status)
    private TextView orderStatus;
    @ViewInject(R.id.banquet_order_detail_ordertime)
    private TextView orderTime;
    @ViewInject(R.id.banquet_order_detail_banquet_name)
    private TextView mBanquetName;
    @ViewInject(R.id.banquet_order_detail_starttime)
    private TextView banquetStartTime;
    @ViewInject(R.id.banquet_order_detail_person_number)
    private TextView orderPersonNum;
    @ViewInject(R.id.banquet_order_detail_telephone)
    private TextView orderTelephone;
    @ViewInject(R.id.banquet_order_detail_remark)
    private TextView banquetRemark;
    private Order mOrder;
    private Banquent mBanquent;
    private long mSurplusTime = -1;// sec
    private String[] weekDays;
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.msg_page));
        mUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
                Router.getAccountModule().banquetUserInfo(mBanquent.getMaster().getUserId(), new OneParameterExpression<Attendee>() {
                    @Override
                    public void action(Attendee attendee) {
                        finalizeDialog();
                        Intent intent = new Intent(mContext, HostAndGuestActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("attendee_info", attendee);
                        mContext.startActivity(intent);
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        Toast.makeText(mContext, R.string.notify_net2, Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });
            }
        });

       weekDays = mContext.getResources().getStringArray(R.array.week_days);
       /* if (mTendIntent != null) {
            mBanquent = (Banquent)mTendIntent.getSerializableExtra("banquent");
            mOrder = (Order) mTendIntent.getSerializableExtra("order");
            if (mSurplusTime == -1) {
                mSurplusTime = mTendIntent.getLongExtra("surplusTime", -1);
            }
        }
        if (mOrder == null) {
            finish();
            return;
        }
        if (mBanquent == null){
            finish();
            return;
        }*/
//        netWork();
    }

    /*private void netWork(){
        if (mBanquent.getMaster().getAvatarURLSmall().isPresent()) {
            Picasso.with(mContext)
                    .load(mBanquent.getMaster().getAvatarURLSmall().get())
                    .placeholder(R.drawable.default_user_head)
                    .fit()
                    .centerCrop()
                    .into(mUserImage);
        }
        mUserNickname.setText(mBanquent.getMaster().getNickName());
        mBanquetName.setText(mOrder.getEvent().getTitle());
        banquetStartTime.setText(formatDate(mBanquent.getTime()));
        orderNumber.setText(mOrder.getSerialNumber());
        orderTime.setText(formatDate(mOrder.getCreateTime()));
        mBanquetName.setText(mOrder.getEvent().getTitle());
        banquetStartTime.setText(formatDate(mOrder.getEvent().getTime()));
        orderPersonNum.setText(mOrder.getQuantity()+getString(R.string.people));
        orderTelephone.setText(mOrder.getPhoneNumber());
        if (mOrder.getRemark()==null){
            banquetRemark.setText(getString(R.string.none));
        }else {
            banquetRemark.setText(mOrder.getRemark());
        }

        String statusStr = "";
        switch (mOrder.getStatus()) {
            case Canceled:
                statusStr = getString(R.string.canceled);
                break;
            case Waiting:
            case Unpaid:
                statusStr = getString(R.string.ongoing);
                break;
            case Finished:
                statusStr = getString(R.string.finished);
                break;
            default:
                statusStr = getString(R.string.ongoing);
                break;
        }
        orderStatus.setText(statusStr);
    }*/

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

}