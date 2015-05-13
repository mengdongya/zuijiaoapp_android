package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

/**
 * Created by xiaqibo on 2015/4/22.
 */
@ContentView(R.layout.activity_recommend_and_favor)
public class RecommendAndFavorActivity extends BaseActivity {

    protected int mContentType = MainFragment.RECOMMEND_PAGE;
    protected TinyUser mCurrentUser = null;
    @ViewInject(R.id.recommend_and_favor_fragment)
    private MainFragment mFragment = null;
    @ViewInject(R.id.recommend_and_favor_toolbar)
    private Toolbar mToolbar;

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
        mContentType = mTendIntent.getIntExtra("content_type", MainFragment.FAVOR_PAGE);
        mCurrentUser = (TinyUser) mTendIntent.getSerializableExtra("tiny_user");
        if (mContentType == MainFragment.FAVOR_PAGE) {
            getSupportActionBar().setTitle(String.format(getString(R.string.whose_favor), mCurrentUser.getNickName()));
        } else if (mContentType == MainFragment.RECOMMEND_PAGE) {
            getSupportActionBar().setTitle(String.format(getString(R.string.whose_recommend), mCurrentUser.getNickName()));
        }
        mFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.recommend_and_favor_fragment);
    }
}
