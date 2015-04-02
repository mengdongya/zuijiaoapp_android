package net.zuijiao.android.zuijiao;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;
import com.zuijiao.view.WordWrapView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements FragmentDataListener,
		MyListViewListener {
	private View mContentView = null;
	private RefreshAndInitListView mListView = null;
	private int test_count = 2;
	private TextView mTextView = null;
	private LayoutInflater mInflater = null;
	private MainAdapter mAdapter = null ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_main, null);
		mListView = (RefreshAndInitListView) mContentView
				.findViewById(R.id.content_items);
		mTextView = (TextView) mContentView.findViewById(R.id.tv_main_fm_blank);
		mAdapter = new MainAdapter() ;
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mItemClickListener);
        mListView.setPullLoadEnable(true) ;
		mListView.setListViewListener(this);
		return mContentView;
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
			startActivity(intent);
		}
	};
//	private BaseAdapter mContentListAdapter = new BaseAdapter() {
//
//	};
	//private String[] test_label = {"我每周必吃","真是一次难忘的回忆","主人萌萌哒","强力推荐","我"};
	private class MainAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.main_content_item,
						null);
				holder = new ViewHolder();
				holder.image_food = (ImageView) convertView
						.findViewById(R.id.content_item_image);
				holder.image_user_head = (ImageView) convertView
						.findViewById(R.id.content_item_user_head);
				holder.label = (WordWrapView) convertView
						.findViewById(R.id.view_wordwrap);
				holder.text1_food_name = (TextView) convertView
						.findViewById(R.id.content_item_title);
				holder.text2_personal = (TextView) convertView
						.findViewById(R.id.content_item_personal_tip);
				holder.text4_user_name = (TextView) convertView
						.findViewById(R.id.content_item_user_name);
				holder.text_intro = (TextView) convertView
						.findViewById(R.id.content_item_comment);
				convertView.setTag(holder);
                String[] test_label = getResources().getStringArray(R.array.test_label) ;
                for (int i = 0; i < test_label.length; i++) {
					TextView textview = new TextView(getActivity());
					textview.setBackgroundResource(R.drawable.bg_label);
					textview.setTextSize(16);
					textview.setTextColor(getResources().getColor(R.color.main_label));
					textview.setTextSize(18);
					textview.setText(test_label[i]);
					holder.label .addView(textview);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getCount() {
			return 8;
		}
	}

	private class ViewHolder {
		public ImageView image_food;
		public TextView text1_food_name;
		public TextView text2_personal;
		public WordWrapView label;
		public TextView text_intro;
		public ImageView image_user_head;
		public TextView text4_user_name;
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
				if (test_count != 0) {
					mTextView.setVisibility(View.GONE);
				} else {
					mTextView.setVisibility(View.VISIBLE);
				}
				mAdapter.notifyDataSetChanged();
				mListView.stopRefresh();
			}
		}, 2000);
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
                if (test_count != 0) {
                    mTextView.setVisibility(View.GONE);
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                }
                fetchData(true);

            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        Log.i("load more", " ^ - _ - ^");
    }

    private void fetchData(Boolean isRefresh) {

        List<Gourmet> tmpGourmets = mAdapter.gourmets.orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;

        if (!isRefresh) { // fetch more
            Gourmet theLatestOne = tmpGourmets.get(tmpGourmets.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }

        Router.getGourmetModule().fetchOurChoice(theLastOneIdentifier
                , null
                , 20
                , gourmets ->
        {
            if (isRefresh) {
                tmpGourmets.addAll(gourmets.getGourmets());
                mAdapter.gourmets = Optional.of(tmpGourmets);
            }
            else {
                mAdapter.gourmets = Optional.of(gourmets.getGourmets());
            }
            mAdapter.notifyDataSetChanged();
            mListView.stopRefresh();
        }
                , errorMessage ->
        {
            mListView.stopRefresh();
        });
    }

}
