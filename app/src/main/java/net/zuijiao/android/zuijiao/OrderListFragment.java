package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zuijiao.view.RefreshAndInitListView;

/**
 * Created by xiaqibo on 2015/6/16.
 */
public class OrderListFragment extends Fragment {
    private View mContentView;
    private RefreshAndInitListView mListView;
    private int tabIndex = 0;

    public OrderListFragment(int index) {
        super();
        this.tabIndex = index;
    }

    public OrderListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if(mContentView == null )
//            mContentView= inflater.inflate(R.layout.fragment_order_list , null ) ;
//
//        mListView = (ListView) mContentView.findViewById(R.id.order_list);
//        mListView.setAdapter(mAdapter);
//        return mContentView ;
        if (mContentView != null) {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
            return mContentView;
        }
        mContentView = inflater.inflate(R.layout.fragment_order_list, null);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_order_list);
        mListView.setPullRefreshEnable(false);
        mListView.setAdapter(mAdapter);
        return mContentView;

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
            if (convertView == null)
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.banquet_order_item, null);
            return convertView;
        }
    };
}
