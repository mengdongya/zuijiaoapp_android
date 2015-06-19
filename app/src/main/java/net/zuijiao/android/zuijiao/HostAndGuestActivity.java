package net.zuijiao.android.zuijiao;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by xiaqibo on 2015/6/15.
 */
@ContentView(R.layout.activity_host_guest)
public class HostAndGuestActivity extends BaseActivity {
    @ViewInject(R.id.host_guest_tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.host_guest_history_list)
    private ListView mHistoryList;

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHistoryList.setAdapter(mHistoryAdapter);
        setListViewHeightBasedOnChildren(mHistoryList);
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()));
    }

    private BaseAdapter mHistoryAdapter = new BaseAdapter() {
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
            return LayoutInflater.from(mContext).inflate(R.layout.banquet_history_item, null);
        }
    };
}
