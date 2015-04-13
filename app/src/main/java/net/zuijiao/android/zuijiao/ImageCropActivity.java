package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;

/**
 * Created by xiaqibo on 2015/4/10.
 */
public class ImageCropActivity extends BaseActivity {

    private Toolbar mToolbar = null;

    protected void findViews() {

    }

    ;

    protected void registeViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
