package net.zuijiao.android.zuijiao;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * Created by xiaqibo on 2015/4/10.
 */
public class ImageChooserActivity extends BaseActivity {

    private Toolbar mToolbar = null;
    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getContentResolver();
    }

    protected void findViews() {

    }

    ;

    protected void registeViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
