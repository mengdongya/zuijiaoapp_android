package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Review;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.ReviewRatingBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/6/15.
 */
@ContentView(R.layout.activity_banquet_comment)
public class BanquetCommentActivity extends BaseActivity implements RefreshAndInitListView.MyListViewListener {
    @ViewInject(R.id.banquet_comment_list_view)
    private RefreshAndInitListView mCommentList;
    @ViewInject(R.id.banquet_swipe_refresh)
    private SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.banquet_comment_tool_bar)
    private Toolbar mToolBar;
    private Integer lastedId = null;
    private int host_id;
    private Reviews mReviews;
    private List<Review> reviewList;
    private String[] weekDays;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        weekDays = mContext.getResources().getStringArray(R.array.week_days);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(((ActivityTask) getApplication()).getDefaultDisplayImageOptions()).memoryCacheExtraOptions(50, 50)
                .threadPoolSize(5).build();
        ImageLoader.getInstance().init(config);
        int totalCount = 0;
        if (mTendIntent != null) {
            host_id = mTendIntent.getIntExtra("host_id", -1);
            totalCount = mTendIntent.getIntExtra("totalCount", 0);
        }
        if (host_id == -1) {
            finish();
            return;
        }
        mCommentList.setPullRefreshEnable(false);
        mCommentList.setAdapter(mAdapter);
        mCommentList.setOnItemClickListener(itemClickListener);
        getSupportActionBar().setTitle(String.format(getString(R.string.toolbar_comments), totalCount));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                netWorkStep(true);
            }
        });
        mCommentList.setPullRefreshEnable(false);
        mCommentList.setPullLoadEnable(false);
        mCommentList.setListViewListener(this);
        netWorkStep(true);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent(mContext, HostAndGuestActivity.class);
            intent.putExtra("attendee_id", reviewList.get(position - 1).getReviewer().getIdentifier());
            intent.putExtra("b_host", false);
            startActivity(intent);
        }
    };


    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (reviewList == null) {
                return 0;
            } else {
                return reviewList.size();
            }

        }

        @Override
        public Object getItem(int position) {
            return reviewList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.banquet_comment_item, null);
                holder = new ViewHolder();
                holder.head = (ImageView) convertView.findViewById(R.id.banquet_comment_item_head);
                holder.name = (TextView) convertView.findViewById(R.id.banquet_comment_item_user_name);
                holder.issue = (TextView) convertView.findViewById(R.id.banquet_comment_item_issue);
                holder.stars = (ReviewRatingBar) convertView.findViewById(R.id.banquet_comment_item_stars);
                holder.comment = (TextView) convertView.findViewById(R.id.banquet_comment_item_comment);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Review review = reviewList.get(position);
            ImageLoader.getInstance().displayImage("file://" + review.getReviewer().getAvatarUrl(), holder.head);
            holder.name.setText(review.getReviewer().getNickName());
            holder.issue.setText(review.getEvent().getTitle() + " · " + formatDate(review.getCreatedAt()));
            holder.stars.setRating(review.getScore());
            holder.comment.setText(review.getContent());
            return convertView;
        }
    };

    @Override
    public void onRefresh() {
        netWorkStep(true);
    }

    @Override
    public void onLoadMore() {
        netWorkStep(false);
    }

    class ViewHolder {
        ImageView head;
        TextView name;
        TextView issue;
        ReviewRatingBar stars;
        TextView comment;
    }

    private void netWorkStep(boolean bRefresh) {
        if (bRefresh) {
            mRefreshLayout.setRefreshing(true);
            lastedId = null;
        }
        Router.getBanquentModule().commentsofBanquent(host_id, lastedId, 20, new OneParameterExpression<Reviews>() {
            @Override
            public void action(Reviews reviews) {
                mReviews = reviews;
                if (bRefresh) {
                    reviewList = mReviews.getReviewList();
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (reviewList == null) {
                        reviewList = new ArrayList<Review>();
                    }
                    reviewList.addAll(mReviews.getReviewList());
                    mAdapter.notifyDataSetChanged();
                }
                lastedId = reviewList.get(reviewList.size() - 1).getIdentifier();
                if (reviewList.size() < 20)
                    mCommentList.setPullLoadEnable(false);
                else
                    mCommentList.setPullLoadEnable(true);
                mRefreshLayout.setRefreshing(false);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * show the date in the specified format
     *
     * @param date
     * @return formatted string
     */
    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(mContext.getString(R.string.month_day), date.getMonth() + 1, date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(String.format(mContext.getString(R.string.banquet_format_time), date.getHours(), date.getMinutes()));
        strBuilder.append(" ");
        return strBuilder.toString();
    }
}
