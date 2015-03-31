package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;

import java.util.ArrayList;
import java.util.Random;

public class MessageFragment extends Fragment implements FragmentDataListener,
		MyListViewListener {
	private View mContentView = null;
	private RefreshAndInitListView mListView = null;
	private BaseAdapter mAdapter = null;
	private LayoutInflater mInflater = null;
	private int test_count = 1 ;
	private TextView mTextView = null ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_message, null);
		this.mInflater = inflater;
		mListView = (RefreshAndInitListView) mContentView
				.findViewById(R.id.lv_msg);
		mTextView  = (TextView) mContentView.findViewById(R.id.tv_main_fm_blank);
		mAdapter = new MesssageAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setListViewListener(this);
        mListView.setOnItemClickListener(mItemClickListener);
		return mContentView;
	}
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity() , FoodDetailActivity.class) ;
            getActivity().startActivity(intent);
        }
    } ;
	private class MesssageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return test_count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = mInflater.inflate(R.layout.msg_item, null);
			return itemView;
		}

	}

	@Override
	public ArrayList<Object> initCache(int type) {
		return null;
	}

	@Override
	public ArrayList<Object> getContentFromNetWork(String Url) {
		return null;
	}

	@Override
	public void NotifyData() {

	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				test_count = new Random().nextInt(2);
				if (test_count != 0) {
					mTextView.setVisibility(View.GONE);
				} else {
					mTextView.setVisibility(View.VISIBLE);
				}
				mListView.setAdapter(mAdapter);
				mListView.stopRefresh();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
}
