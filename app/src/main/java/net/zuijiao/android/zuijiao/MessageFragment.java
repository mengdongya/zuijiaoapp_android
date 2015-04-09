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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageFragment extends Fragment implements FragmentDataListener,
        MyListViewListener {
    private View mContentView = null;
    private RefreshAndInitListView mListView = null;
    private MesssageAdapter mAdapter = null;
    private LayoutInflater mInflater = null;
    private int test_count = 1;
    private TextView mTextView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_message, null);
        this.mInflater = inflater;
        mListView = (RefreshAndInitListView) mContentView
                .findViewById(R.id.lv_msg);
        mTextView = (TextView) mContentView.findViewById(R.id.tv_main_fm_blank);
//        mAdapter = new MesssageAdapter();
//        mListView.setAdapter(mAdapter);
        mListView.setListViewListener(this);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setPullLoadEnable(true);
        mListView.autoResetHeadView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                networkStep();
            }
        }, 2000);
        return mContentView;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
            getActivity().startActivity(intent);
        }
    };

    private class MesssageAdapter extends BaseAdapter {
        private List<Message> mData = null;

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.msg_item, null);
                holder = new ViewHolder();
                holder.comment = (TextView) convertView.findViewById(R.id.msg_item_msg_content);
                holder.gourmetName = (TextView) convertView.findViewById(R.id.msg_item_food_name);
                holder.gourmetPic = (ImageView) convertView.findViewById(R.id.msg_item_food_image);
                holder.time = (TextView) convertView.findViewById(R.id.msg_item_date);
                holder.userhead = (ImageView) convertView.findViewById(R.id.msg_item_user_head);
                holder.userName = (TextView) convertView.findViewById(R.id.msg_item_user_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Message msg = mData.get(position);
            holder.userName.setText(msg.getFromUser().getNickName());
            Picasso.with(parent.getContext())
                    .load(msg.getFromUser().getAvatarURL().get())
                    .placeholder(R.drawable.default_user_head)
                    .into(holder.userhead);
            holder.time.setText(msg.getCreateTime().toString());
            Optional<String> gourmetImage = Optional.of(msg.getGourmet().get().getImageURLs().get(0) + "!Thumbnails");

            if (gourmetImage.isPresent()) {
                Picasso.with(parent.getContext())
                        .load(gourmetImage.get())
                        .placeholder(R.drawable.empty_view_greeting)
                        .into(holder.gourmetPic);
                holder.gourmetName.setVisibility(View.GONE);
                holder.gourmetPic.setVisibility(View.VISIBLE);
            } else {
                holder.gourmetName.setText(msg.getGourmet().get().getName());
                holder.gourmetName.setVisibility(View.VISIBLE);
                holder.gourmetPic.setVisibility(View.GONE);
            }
            holder.comment.setText(msg.getDescription());
            return convertView;
        }

        public void setData(List<Message> list) {
            mData = list;
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView userName;
            TextView time;
            TextView comment;
            TextView gourmetName;
            ImageView userhead;
            ImageView gourmetPic;
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
                networkStep();
//                test_count = new Random().nextInt(2);
//                if (test_count != 0) {
//                    mTextView.setVisibility(View.GONE);
//                } else {
//                    mTextView.setVisibility(View.VISIBLE);
//                }
//                mListView.setAdapter(mAdapter);

            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

    private void networkStep() {
        if(Router.INSTANCE.getCurrentUser() == null){
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getResources().getString(R.string.none_msg));
            return ;
        }
        mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getMsgLastRefreshTime()).toString());
        Router.getMessageModule().message(News.NotificationType.Comment, null, null, 20, msg -> {
            if (!msg.getAllMessage().isEmpty()) {
                mTextView.setVisibility(View.GONE);
                List<Message> msgList = msg.getAllMessage();
                if (mAdapter == null) {
                    mAdapter = new MesssageAdapter();
                }
                mAdapter.setData(msgList);
                mListView.setAdapter(mAdapter);
                mListView.stopRefresh();
            } else {
                mListView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(getResources().getString(R.string.none_msg));
            }
            PreferenceManager.getInstance(getActivity().getApplicationContext()).saveMsgLastRefreshTime(new Date().getTime());
        }, errorMsg -> {
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.notify_net),Toast.LENGTH_LONG).show();
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getResources().getString(R.string.notify_net));
            mListView.stopRefresh();
        });
    }
}
