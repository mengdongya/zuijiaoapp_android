package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.utils.MyTextWatcher;
import com.zuijiao.view.ReviewRatingBar;

/**
 * Create by yitianhao on 2015/7/7
 */
@ContentView(R.layout.activity_review)
public class ReviewActivity extends BaseActivity {
    @ViewInject(R.id.banquet_review_tool_bar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.edit_review_description_listener)
    private TextView mTvDescriptionListener = null;
    @ViewInject(R.id.et_comment)
    private EditText mEtReviewContent = null;
    @ViewInject(R.id.review_ratingbar)
    private ReviewRatingBar mReviewRatingbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvDescriptionListener.setText(String.format(getString(R.string.nick_name_watcher), 0, 500));
        mEtReviewContent.addTextChangedListener(new MyTextWatcher(mTvDescriptionListener, 500, mContext));
        mReviewRatingbar.setRating(0);
        mReviewRatingbar.setNumStars(5);
        mReviewRatingbar.setStepSize(0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_review) {
            float rating = mReviewRatingbar.getRating();
            if (rating == 0) {
                Toast.makeText(mContext, getString(R.string.notify_review_score_empty), 0).show();
                return super.onOptionsItemSelected(item);
            }
            String reviewContent = mEtReviewContent.getText().toString().trim();
            if (reviewContent == "" || reviewContent.equals("") || reviewContent.isEmpty()) {
                Toast.makeText(mContext, getString(R.string.notify_review_content_empty), 0).show();
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
