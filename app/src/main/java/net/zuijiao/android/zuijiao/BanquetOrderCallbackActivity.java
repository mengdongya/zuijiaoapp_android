package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

import java.util.Date;

/**
 * Created by xiaqibo on 2015/6/26.
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
        bSuccess = mTendIntent.getBooleanExtra("b_success", false);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        mFinishBtn.setOnClickListener(this);
        mCompleteTv.setOnClickListener(this);
        if (bSuccess) {
            mBanquet = BanquetOrderActivity.mBanquent;
            showSuccess();
        } else {
            showFailed();
        }
    }

    private void showFailed() {
        mBgImage.setImageResource(R.drawable.order_callback_failed_bg);
        mTitleTv.setVisibility(View.GONE);
        mWaitingTv.setVisibility(View.GONE);
        mDetailTv.setText(R.string.notify_order_failed);
        mDetailTv.setAutoLinkMask(Linkify.PHONE_NUMBERS);
//        mDetailTv.setText(getClickableSpan());
        mDetailTv.setMovementMethod(LinkMovementMethod.getInstance());
        mNotifyTv.setVisibility(View.GONE);
        mCompleteTv.setVisibility(View.GONE);
    }

    private SpannableString getClickableSpan() {
        SpannableString spanableInfo = new SpannableString(
                "This is a test, Click Me");
        int start = 16;
        int end = spanableInfo.length();
        spanableInfo.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(mContext, "Click Success", Toast.LENGTH_SHORT)
                        .show();
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }


    private void showSuccess() {
        mTitleTv.setText(mBanquet.getTitle());
        String detail;
        detail = formatDate(mBanquet.getTime()) + "\n" + mBanquet.getAddress();
        mDetailTv.setText(detail);
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
                finish();
                break;
            default:
                break;
        }
    }
}
