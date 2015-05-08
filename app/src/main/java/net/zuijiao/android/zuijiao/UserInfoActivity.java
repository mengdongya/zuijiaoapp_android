package net.zuijiao.android.zuijiao;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.entity.SimpleImage;

import java.util.ArrayList;

@ContentView(R.layout.activity_user_info)
public final class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.user_info_list)
    private ListView mInfoList;
    @ViewInject(R.id.user_info_toolbar)
    private Toolbar mToolbar = null;
    @ViewInject(R.id.user_info_user_head)
    private ImageView mUserHead = null;
    @ViewInject(R.id.user_info_recommend)
    private Button mBtnRecommend;
    @ViewInject(R.id.user_info_wish)
    private Button mBtnFavor;
    @ViewInject(R.id.user_info_follow)
    private Button mBtnFollow;
    @ViewInject(R.id.user_info_fans)
    private Button mBtnFans;

//    private User mUser = null ;
//    private ParcelableUser mParcelableUser = null ;

    private boolean bSelf = false;
    private AdapterView.OnItemClickListener mUserInfoItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), "parent" + position, Toast.LENGTH_SHORT).show();
        }
    };
    private BaseAdapter mFlavorAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 1;
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
            return LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.user_info_favor_item, null);
        }
    };
    private AdapterView.OnItemClickListener mFlavorItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
        }
    };
    private BaseAdapter mInfoAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.user_info_item, null);
            TextView title = (TextView) contentView.findViewById(R.id.user_info_item_title);
            TextView content = (TextView) contentView.findViewById(R.id.user_info_item_content);
            if (position == 0) {
                title.setText(getString(R.string.base_info));
                content.setText(getString(R.string.no_base_info));
            } else if (position == 1) {
                title.setText(getString(R.string.personal_introduction));
                content.setText(getString(R.string.no_experience));
            } else if (position == 2) {
                title.setText(getString(R.string.flavor_hobby));
                if (true) {
                    GridView gdView = (GridView) contentView.findViewById(R.id.gv_user_info_favor);
                    gdView.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                    gdView.setOnItemClickListener(mFlavorItemListener);
                    gdView.setFocusable(false);
                    gdView.setFocusableInTouchMode(false);
                    gdView.setAdapter(mFlavorAdapter);
                    setListViewHeightBasedOnChildren(gdView);
                } else {
                    content.setText(getString(R.string.no_hobby));
                }
            }
            return contentView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_self_info:
                Intent intent = new Intent();
                intent.setClass(UserInfoActivity.this, EditUserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_follow_someone:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (bSelf) {
            getMenuInflater().inflate(R.menu.user_info, menu);
        } else {
            getMenuInflater().inflate(R.menu.user_info_self, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mTendIntent != null) {
            bSelf = mTendIntent.getBooleanExtra("b_self", false);
        }
        mBtnFans.setText(String.format(getString(R.string.fans_count), 10));
        mBtnFavor.setText(String.format(getString(R.string.favorite_count), 10));
        mBtnFollow.setText(String.format(getString(R.string.follow_count), 10));
        mBtnRecommend.setText(String.format(getString(R.string.recommend_count), 10));
        mBtnRecommend.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnFans.setOnClickListener(this);
        mBtnFavor.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        mInfoList.setAdapter(mInfoAdapter);
        mInfoList.setItemsCanFocus(true);
        mInfoList.setOnItemClickListener(mUserInfoItemListener);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.user_info_recommend:
                intent.setClass(UserInfoActivity.this, RecommendAndFavorActivity.class);
                break;
            case R.id.user_info_fans:
                intent.setClass(UserInfoActivity.this, FriendActivity.class);
                intent.putExtra("friend_index", 1);
                break;
            case R.id.user_info_follow:
                intent.setClass(UserInfoActivity.this, FriendActivity.class);
                intent.putExtra("friend_index", 0);
                break;
            case R.id.user_info_wish:
                intent.setClass(UserInfoActivity.this, RecommendAndFavorActivity.class);
                break;
            case R.id.user_info_user_head:
                intent.setClass(UserInfoActivity.this, BigImageActivity.class);
                ArrayList<SimpleImage> avatarUrl = new ArrayList<>();
                avatarUrl.add(new SimpleImage("", "", "http://g.hiphotos.baidu.com/image/pic/item/c8177f3e6709c93d3e2755f49d3df8dcd00054df.jpg"));
                intent.putParcelableArrayListExtra("edit_images", avatarUrl);
                break;
        }
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setListViewHeightBasedOnChildren(GridView gdView) {
        ListAdapter listAdapter = gdView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i += 5) {
            View listItem = listAdapter.getView(i, null, gdView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gdView.getLayoutParams();
        params.height = totalHeight
                + (gdView.getVerticalSpacing() * (listAdapter.getCount()));

    }
}
