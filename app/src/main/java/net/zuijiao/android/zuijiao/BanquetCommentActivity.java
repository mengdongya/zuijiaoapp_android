package net.zuijiao.android.zuijiao;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/6/15.
 */
@ContentView(R.layout.activity_banquet_comment)
public class BanquetCommentActivity extends BaseActivity {
    @ViewInject(R.id.banquet_comment_list_view)
    private ListView mCommentList;

    @ViewInject(R.id.banquet_comment_tool_bar)
    private Toolbar mToolBar;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("20comments");
        mCommentList.setAdapter(mAdapter);
    }


    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return position;
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
                holder.stars = (LinearLayout) convertView.findViewById(R.id.banquet_comment_item_stars);
                holder.comment = (TextView) convertView.findViewById(R.id.banquet_comment_item_comment);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }
    };

    class ViewHolder {
        ImageView head;
        TextView name;
        TextView issue;
        LinearLayout stars;
        TextView comment;
    }
}
