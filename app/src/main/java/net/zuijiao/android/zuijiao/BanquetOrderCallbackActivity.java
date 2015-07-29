package net.zuijiao.android.zuijiao;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.MessageDef;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by xiaqibo on 2015/6/26.
 * called after third party payment activity finished
 */
@ContentView(R.layout.activity_banquet_order_callback)
public class BanquetOrderCallbackActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.order_callback_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.order_callback_bg)
    private ImageView mBgImage;
    @ViewInject(R.id.order_callback_title)
    private TextView mTitleTv;
    @ViewInject(R.id.order_callback_detail)
    private TextView mDetailTv;
    @ViewInject(R.id.order_callback_waiting)
    private TextView mWaitingTv;
    @ViewInject(R.id.order_callback_finish_btn)
    private Button mFinishBtn;
    @ViewInject(R.id.order_callback_notify_complete)
    private TextView mNotifyTv;
    @ViewInject(R.id.order_callback_complete_info)
    private TextView mCompleteTv;
    private boolean bSuccess = false;
    private Banquent mBanquet;
    private String[] weekDays;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.detail_order));
        if(mTendIntent != null)
            bSuccess = mTendIntent.getBooleanExtra("b_success", false);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        mFinishBtn.setOnClickListener(this);
        mCompleteTv.setOnClickListener(this);
        if (bSuccess) {
            mBanquet = BanquetOrderActivity.mBanquent;
            Intent intent = new Intent();
            intent.setAction(MessageDef.ACTION_ORDER_CREATED);
            sendBroadcast(intent);
            showSuccess();
        } else {
            showFailed();
        }
    }

    /**
     * notify pay failed
     */
    private void showFailed() {
        mBgImage.setImageResource(R.drawable.order_callback_failed_bg);
        mTitleTv.setVisibility(View.GONE);
        mWaitingTv.setVisibility(View.GONE);
        mDetailTv.setText(R.string.notify_order_failed);
        mDetailTv.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        mDetailTv.setMovementMethod(LinkMovementMethod.getInstance());
        mNotifyTv.setVisibility(View.GONE);
        mCompleteTv.setVisibility(View.GONE);
    }


    /**
     * notify pay success
     */
    private void showSuccess() {
        mTitleTv.setText(mBanquet.getTitle());
        String detail;
        detail = formatDate(mBanquet.getTime()) + "\n" + mBanquet.getAddress();
        mDetailTv.setText(detail);
    }

    /**
     * display date in a specified format
     *
     * @param date
     * @return
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(bSuccess){
            LinkedList<Activity> list = ActivityTask.getInstance().getActivitiesList() ;
            for(Activity activity: list){
                if(activity instanceof BanquetDetailActivity
                        ||activity instanceof BanquetOrderCreateActivity
                        || activity instanceof BanquetOrderActivity ){
                    try{
                        activity.finish();
                    }catch(Throwable t){
                        t.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent(BanquetOrderCallbackActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_callback_complete_info:
                createDialog();
                Router.getAccountModule().fetchMyInfo(new OneParameterExpression<User>() {
                    @Override
                    public void action(User user) {
                        finalizeDialog();
                        mFileMng.setFullUser(user);
                        Intent intent = new Intent(mContext, EditUserInfoActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new OneParameterExpression<String>() {
                    @Override
                    public void action(String s) {
                        Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                        finalizeDialog();
                    }
                });
                break;
            case R.id.order_callback_finish_btn:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
