package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.zuijiao.model.common.TasteTag;
import com.zuijiao.android.zuijiao.network.Cache;

import java.util.ArrayList;
import java.util.List;

/**
 * edit taste tag ,called from edit-user-info-activity;
 * Created by xiaqibo on 2015/5/14.
 */

@ContentView(R.layout.activity_taste)
public class TasteActivity extends BaseActivity {
    @ViewInject(R.id.gv_taste)
    private GridView mGdView = null;
    private ArrayList<String> mUserTaste = null;
    @ViewInject(R.id.taste_toolbar)
    private Toolbar mToolbar = null;
    private MenuItem mMenuBtn = null;
    //all taste tag ,cached when app is launched
    private List<TasteTag> tasteTags = Cache.INSTANCE.tasteTags;

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserTaste = mTendIntent.getStringArrayListExtra("my_taste_tag");
        if (mUserTaste == null) {
            mUserTaste = new ArrayList<>();
        }
        mGdView.setAdapter(mAdapter);
        mGdView.setOnItemClickListener(mListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taste_tag, menu);
        mMenuBtn = menu.findItem(R.id.taste_tag);
        int totalSize = 9;
        if (tasteTags != null) {
            totalSize = tasteTags.size();
        }
        mMenuBtn.setTitle(String.format(getString(R.string.sure_with_num), mUserTaste.size(), totalSize));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.taste_tag) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("my_taste_tag", mUserTaste);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //taste tag list listener ;
    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (mUserTaste.contains(tasteTags.get(position).getName())) {
                mUserTaste.remove(tasteTags.get(position).getName());
            } else {
                mUserTaste.add(tasteTags.get(position).getName());
            }
            mMenuBtn.setTitle(String.format(getString(R.string.sure_with_num), mUserTaste.size(), tasteTags.size()));
            mAdapter.getView(position, view, parent);
        }
    };
    // taste tag adapter
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (tasteTags != null) {
                return tasteTags.size();
            }
            return 0;
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
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.taste_select_item, null);
            TextView text = (TextView) convertView.findViewById(R.id.taste_item_text);
            ImageView image = (ImageView) convertView.findViewById(R.id.taste_item_image);
            ImageView shadow = (ImageView) convertView.findViewById(R.id.taste_item_shadow);
            Picasso.with(mContext).load(tasteTags.get(position).getImageURL()).placeholder(R.drawable.default_user_head).into(image);
            text.setText(tasteTags.get(position).getName());
            if (mUserTaste.contains(tasteTags.get(position).getName())) {
                shadow.setVisibility(View.VISIBLE);
            } else {
                shadow.setVisibility(View.GONE);
            }
            return convertView;
        }
    };
}
