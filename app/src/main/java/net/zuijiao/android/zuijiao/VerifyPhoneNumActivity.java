package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/5/5.
 */
@ContentView(R.layout.activity_verify_phone)
public class VerifyPhoneNumActivity extends BaseActivity {

    @ViewInject(R.id.verify_number_toolbar)
    private Toolbar mToolbar = null;

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verify_phone, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
