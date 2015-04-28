package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/4/22.
 */
@ContentView(R.layout.activity_recommend_and_favor)
public class RecommendAndFavorActivity extends BaseActivity {

    @ViewInject(R.id.recommend_and_favor_fragment)
    private Fragment mFragment = null;
    @ViewInject(R.id.recommend_and_favor_toolbar)
    private Toolbar mToolbar;
    protected int mContentType = MainFragment.RECOMMEND_PAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mTendIntent != null) {
            mContentType = mTendIntent.getIntExtra("content_type", MainFragment.FAVOR_PAGE);
        }
//        mFragment = new MainFragment() ;
    }
}
