package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.network.Router;

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
    @ViewInject(R.id.banquent_order_activity_ll)
    private LinearLayout ajkdhsk;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.receiving_account));
//        Router.getAccountModule().masterInfo();
        ajkdhsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(mContext,BanquetOrderDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}
