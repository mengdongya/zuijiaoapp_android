package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * Created by xiaqibo on 2015/3/30.
 */
@ContentView(R.layout.activity_favor_person)
public class FavorPersonListActivity extends BaseActivity {
    @ViewInject(R.id.favor_person_toolbar)
    private Toolbar mToolbar = null ;
    @ViewInject(R.id.lv_favor_person)
    private ListView mList = null;
    private LayoutInflater mInflater= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mList.setAdapter(mAdapter);
        mInflater = LayoutInflater.from(this) ;
    }
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 10;
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
            return mInflater.inflate(R.layout.favor_person_item , null);
        }
    };
    protected void findViews() {

    }

    protected void registeViews() {

    }

}
