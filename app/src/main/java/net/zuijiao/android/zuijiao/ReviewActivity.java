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
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Review;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.MyTextWatcher;
import com.zuijiao.view.ReviewRatingBar;

import java.util.List;

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

    private int orderId;

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
        mReviewRatingbar.setStepSize(1);
        if (mTendIntent != null) {
            orderId = mTendIntent.getIntExtra("orderId", -1);
        }
        if (orderId == -1) {
            finish();
            return;
        }
    }

    private void networkStep() {
        int score = (int)mReviewRatingbar.getRating();
        String content = mEtReviewContent.getText().toString();
        System.out.println("orderId:" + orderId + "," + content + "," + score);
        Router.getBanquentModule().createComment(orderId, content, score, new LambdaExpression() {
            @Override
            public void action() {
                Toast.makeText(mContext, getString(R.string.send_comment_success), Toast.LENGTH_SHORT).show();
                setResult(MainActivity.COMMENT_SUCCESS);
                finish();
            }
        }, new LambdaExpression() {
            @Override
            public void action() {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
            }
        });
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
            networkStep();

//            Router.getBanquentModule().commentsofBanquent(29, null, 20, new OneParameterExpression<Reviews>() {
//                @Override
//                public void action(Reviews reviews) {
//                    List<Review> reviewList = reviews.getReviewList();
//                    System.out.println("test success");
//                    for (Review review : reviewList) {
//                        int id = review.getIdentifier();
//                        String content = review.getContent();
//                        String time = review.getCreatedAt();
//                        System.out.println("Review:" + id + "," + content + "," + time);
//                    }
//
//                }
//            }, new OneParameterExpression<String>() {
//                @Override
//                public void action(String s) {
//                    System.out.println("test error：" + s);
//                }
//            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //setResult(MainActivity.COMMENT_SUCCESS);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
