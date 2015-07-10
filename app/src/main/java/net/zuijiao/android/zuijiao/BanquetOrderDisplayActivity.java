package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.network.Router;

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
    @ViewInject(R.id.banquet_order_display_status)
    private TextView status;
    @ViewInject(R.id.order_detail_oder_id)
    private View mOrderNum;
    @ViewInject(R.id.order_detail_oder_price)
    private View mOrderPrice;
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
    private Order mOrder;
    private String[] weekDays;


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
        }
        if (mOrder == null) {
            finish();
            return;
        }
        Picasso.with(mContext).load(mOrder.getImageUrl()).placeholder(R.drawable.empty_view_greeting).into(mImage);
        title.setText(mOrder.getTitle());
        String formatDate = formatDate(mOrder.getHoldTime());
        dateLocation.setText(formatDate + getString(R.string.center_dot) + mOrder.getAddress());
        switch (mOrder.getStatus()) {
            case Canceled:
                status.setText(getString(R.string.canceled_banquet));
                break;
            case Waiting:
                status.setText(getString(R.string.waiting_fo_you));
                break;
            case Finished:
                status.setText(getString(R.string.finished_banquet));
                break;
            default:
                status.setText(getString(R.string.waiting_banquet));
                break;
        }
        fillGenInfo(mOrderNum, getString(R.string.order_num), mOrder.getSerialNumber());
        fillGenInfo(mOrderPrice, getString(R.string.total_price), mOrder.getPrice() + getString(R.string.yuan));
        fillGenInfo(mOrderPhone, getString(R.string.mobile_phone), mOrder.getPhoneNumber());
        fillGenInfo(mOrderRemark, getString(R.string.remark), mOrder.getRemark());
        fillGenInfo(mOrderDate, getString(R.string.order_time), mOrder.getCreateTime().toLocaleString());
        mNoticeText.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        mNoticeText.setMovementMethod(LinkMovementMethod.getInstance());
        System.out.println("mOrder.getBanquentIdentifier():"+mOrder.getBanquentIdentifier());
        clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
                Router.getBanquentModule().theme(mOrder.getBanquentIdentifier(), new OneParameterExpression<Banquent>() {
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
}
