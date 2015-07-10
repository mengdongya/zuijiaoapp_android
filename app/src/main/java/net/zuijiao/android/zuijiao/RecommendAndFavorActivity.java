package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.user.TinyUser;

/**
 * display user's recommendation or favor gourmet ,
 * called from user-info-activity ,
 * including one gourmet display fragment
 * Created by xiaqibo on 2015/4/22.
 */
@ContentView(R.layout.activity_recommend_and_favor)
public class RecommendAndFavorActivity extends BaseActivity {

    protected int mContentType = GourmetDisplayFragment.RECOMMEND_PAGE;
    protected Optional<TinyUser> mCurrentUser = null;
    @ViewInject(R.id.recommend_and_favor_fragment)
    private GourmetDisplayFragment mFragment = null;
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
        mContentType = mTendIntent.getIntExtra("content_type", GourmetDisplayFragment.FAVOR_PAGE);
        mCurrentUser = Optional.of((TinyUser) mTendIntent.getSerializableExtra("tiny_user"));
        if (mContentType == GourmetDisplayFragment.FAVOR_PAGE) {
            getSupportActionBar().setTitle(String.format(getString(R.string.whose_favor), mCurrentUser.get().getNickName()));
        } else if (mContentType == GourmetDisplayFragment.RECOMMEND_PAGE) {
            getSupportActionBar().setTitle(String.format(getString(R.string.whose_recommend), mCurrentUser.get().getNickName()));
        }
        mFragment = (GourmetDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.recommend_and_favor_fragment);
    }
}
