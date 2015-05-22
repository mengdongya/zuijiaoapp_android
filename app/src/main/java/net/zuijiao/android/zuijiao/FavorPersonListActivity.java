package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.model.user.WouldLikeToEatUser;
import com.zuijiao.controller.FileManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.utils.StrUtil;

/**
 * Created by xiaqibo on 2015/3/30.
 */
@ContentView(R.layout.activity_favor_person)
public class FavorPersonListActivity extends BaseActivity {
    @ViewInject(R.id.favor_person_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.lv_favor_person)
    private ListView mList = null;
    private LayoutInflater mInflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(String.format(getResources().getString(R.string.favor_label), FileManager.tmpWouldLikeList.get().getCount() + ""));
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mListener);
        mInflater = LayoutInflater.from(this);
    }

    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TinyUser user = FileManager.tmpWouldLikeList.get().getUsers().get(position);
            Intent intent = new Intent();
            intent.setClass(mContext, UserInfoActivity.class);
            intent.putExtra("tiny_user", user);
            startActivity(intent);
        }
    };
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return FileManager.tmpWouldLikeList.get().getCount();
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
            WouldLikeToEatUser user = FileManager.tmpWouldLikeList.get().getUsers().get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.favor_person_item, null);
                holder = new ViewHolder();
                holder.head = (ImageView) convertView.findViewById(R.id.favor_person_head);
                holder.location = (TextView) convertView.findViewById(R.id.favor_person_location);
                holder.time = (TextView) convertView.findViewById(R.id.favor_person_time);
                holder.userName = (TextView) convertView.findViewById(R.id.favor_person_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (user.getAvatarURL().isPresent())
                Picasso.with(getApplicationContext())
                        .load(user.getAvatarURL().get())
                        .placeholder(R.drawable.default_user_head)
                        .into(holder.head);
            String location = DBOpenHelper.getmInstance(getApplicationContext()).getLocationByIds(user.getProvinceID(), user.getCityID());
            if (location == null || location.equals("")) {
                location = "";
            }
            int recommendationCount = user.getRecommendationCount();
            String locationAndRecommendCount = location;
            if (recommendationCount > 0) {
                if (!location.equals("")) {
                    location = location + getString(R.string.splite_dot);
                }
                locationAndRecommendCount = location + String.format(getString(R.string.recommendation_count), recommendationCount);
            }
            holder.location.setText(locationAndRecommendCount);
            holder.userName.setText(user.getNickName());
            holder.time.setText(StrUtil.formatTime(user.getDate(), getApplicationContext()));
            return convertView;
        }
    };

    private class ViewHolder {
        ImageView head;
        TextView userName;
        TextView time;
        TextView location;
    }

    protected void findViews() {

    }

    protected void registerViews() {

    }

}
