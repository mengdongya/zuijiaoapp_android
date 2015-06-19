package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.thirdopensdk.WeixinPay;

/**
 * Created by xiaqibo on 2015/6/15.
 */
@ContentView(R.layout.activity_banquet_order)
public class BanquetOrderActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.banquet_order_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.banquet_order_bottom_order)
    private Button mPayBtn;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPayBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banquet_order_bottom_order:
                new WeixinPay().pay(BanquetOrderActivity.this);
                break;
        }
    }
}
