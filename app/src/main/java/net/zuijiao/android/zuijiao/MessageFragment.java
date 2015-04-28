package net.zuijiao.android.zuijiao;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.utils.StrUtil;
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
    private Context mContext;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileManager.tmpMessageGourmet = mAdapter.getItem(position - 1).getGourmet().get();
            Intent intent = new Intent(mContext, FoodDetailActivity.class);
            startActivity(intent);
        }
    };
    private LinearLayout mLayout = null;
    private Activity mActivity = null;

    public MessageFragment() {
        super();
    }

    public MessageFragment(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_message, null);
        this.mInflater = inflater;
        mActivity = getActivity();
        mListView = (RefreshAndInitListView) mContentView
                .findViewById(R.id.lv_msg);
//        mTextView = (TextView) mContentView.findViewById(R.id.tv_main_fm_blank);
        mLayout = (LinearLayout) mContentView.findViewById(R.id.message_empty_content);
//        mAdapter = new MesssageAdapter();
//        mListView.setAdapter(mAdapter);
//        mListView.setListViewListener(this);
//        mListView.setOnItemClickListener(mItemClickListener);
//        mListView.setPullLoadEnable(true);
//        mListView.autoResetHeadView();

        mAdapter = new MesssageAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setPullLoadEnable(true);
        mListView.setListViewListener(this);
        firstInit();
        return mContentView;
    }

    private void firstInit() {
        mListView.autoResetHeadView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                networkStep(true);
            }
        }, 400);
    }

    public void clearMessage() {
        try {
            mAdapter.mData.clear();
            mAdapter.notifyDataSetChanged();
            mListView.setPullLoadEnable(false);
        } catch (Throwable t) {
            System.err.println("no message");
            ;
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
        new Handler().postDelayed(() -> {
            networkStep(true);
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        networkStep(false);
    }

    private void networkStep(boolean bRefresh) {
        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {
            ((BaseActivity) getActivity()).tryLoginFirst(() -> {
                if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {
                    mAdapter.mData.clear();
                    mAdapter.notifyDataSetChanged();
                    mListView.setPullLoadEnable(false);
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                    ((BaseActivity) getActivity()).notifyLogin(() -> {
                        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {

                        } else {
                            networkStep(bRefresh);
                        }
                    });
                } else {
                    networkStep(bRefresh);
                }
            }, error -> {
                Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                mListView.stopRefresh();
                mListView.stopLoadMore();
            });
            return;
        }
        mListView.setRefreshTime(new Date(PreferenceManager.getInstance(getActivity()).getMsgLastRefreshTime()).toLocaleString());
        List<Message> tmpMessages = Optional.of(mAdapter.mData).orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;
        if (!bRefresh) {
            Message theLatestOne = tmpMessages.get(tmpMessages.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        Router.getMessageModule().message(News.NotificationType.Comment, theLastOneIdentifier, null, 20, msg -> {
            if (bRefresh) {
                if (!msg.getAllMessage().isEmpty()) {
                    mLayout.setVisibility(View.GONE);
                    List<Message> msgList = msg.getAllMessage();
                    if (mAdapter == null) {
                        mAdapter = new MesssageAdapter();
                    }
                    mAdapter.setData(msgList);
                    mListView.setAdapter(mAdapter);
                    if (msg.getAllMessage().size() < 20) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                } else {
                    mListView.setPullLoadEnable(false);
                    mAdapter.setData(new ArrayList<Message>());
                    mAdapter.notifyDataSetChanged();
                    mLayout.setVisibility(View.VISIBLE);
                }
                mListView.stopRefresh();
            } else {
                if (!msg.getAllMessage().isEmpty()) {
                    if (mAdapter.mData == null) {
                        mAdapter.mData = new ArrayList<Message>();
                    }
                    mAdapter.mData.addAll(msg.getAllMessage());
                    mAdapter.notifyDataSetChanged();
                    if (msg.getAllMessage().size() < 20) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                } else {
                    mListView.setPullLoadEnable(false);
                    Toast.makeText(mContext, getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                }
                mListView.stopLoadMore();
            }
            PreferenceManager.getInstance(mContext).saveMsgLastRefreshTime(new Date().getTime());
        }, errorMsg -> {
            Toast.makeText(mContext, getString(R.string.notify_net), Toast.LENGTH_LONG).show();
            mListView.stopRefresh();
            mListView.stopLoadMore();
        });
    }

    private class MesssageAdapter extends BaseAdapter {
        public List<Message> mData = new ArrayList<>();

        @Override
        public int getCount() {
            if (mData == null) {
                return 0;
            }
            return mData.size();
        }

        @Override
        public Message getItem(int position) {
            return mData.get(position);
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
            if (msg.getFromUser().getAvatarURL().isPresent())
                Picasso.with(parent.getContext())
                        .load(msg.getFromUser().getAvatarURL().get())
                        .placeholder(R.drawable.default_user_head)
                        .into(holder.userhead);
            holder.time.setText(StrUtil.formatTime(msg.getCreateTime(), mContext));
            if (msg.getGourmet().get().getImageURLs() != null && msg.getGourmet().get().getImageURLs().size() != 0) {
                Optional<String> gourmetImage = Optional.of(msg.getGourmet().get().getImageURLs().get(0) + "!Thumbnails");
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

//            if (gourmetImage.isPresent()) {
//
//            } else {
//
//            }
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
}
