package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.message.Messages;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.RefreshAndInitListView;
import com.zuijiao.view.RefreshAndInitListView.MyListViewListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * display notifications or comments , included in message-activity ;
 */
@SuppressLint("ValidFragment")
public class MessageFragment extends Fragment implements FragmentDataListener,
        MyListViewListener {
    public RefreshAndInitListView mListView = null;
    public MessageAdapter mAdapter = null;
    private View mContentView = null;
    private LayoutInflater mInflater = null;
    private Context mContext;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Message msg = mAdapter.getItem(position - 1);
            if (msg.getType() == Message.Type.Favorite || msg.getType() == Message.Type.Comment) {
                if (msg.getGourmet().isPresent()) {
                    Gourmet gourmet = msg.getGourmet().get();
                    Intent intent = new Intent();
                    intent.putExtra("selected_gourmet", gourmet);
                    intent.setClass(mContext, GourmetDetailActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_gourmet_related), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.getType() == Message.Type.Follower) {
                if (msg.getFromUser() != null) {
                    TinyUser user = msg.getFromUser();
                    Intent intent = new Intent();
                    intent.setClass(mContext, UserInfoActivity.class);
                    intent.putExtra("tiny_user", user);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_user_related), Toast.LENGTH_SHORT).show();
                }
            } else {

            }
//            Router.getMessageModule().markAsRead();
        }
    };
    private int mType = 0;
    private LinearLayout mLayout = null;
    private SwipeRefreshLayout mRefreshLayout = null;
    private Activity mActivity = null;
    private OnMessageFetch messageListener;

    public MessageFragment() {
        super();
    }

    public MessageFragment(Context context, OnMessageFetch messageListener) {
        super();
        this.mContext = context;
        this.messageListener = messageListener;
    }

    public MessageFragment(Context context, OnMessageFetch messageListener, int type) {
        super();
        this.mContext = context;
        this.messageListener = messageListener;
        this.mType = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_message, null);
        }
        this.mInflater = inflater;
        mListView = (RefreshAndInitListView) mContentView
                .findViewById(R.id.lv_msg);
        mLayout = (LinearLayout) mContentView.findViewById(R.id.message_empty_content);
        mAdapter = new MessageAdapter();
        mListView.setAdapter(mAdapter);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.message_fragment_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkStep(true);
            }
        });
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(false);
        mListView.setListViewListener(this);
        firstInit();
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void firstInit() {
//        mListView.autoResetHeadView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                networkStep(true);
            }
        }, 400);
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
                networkStep(true);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        networkStep(false);
    }


    public String getSize() {
        if (mAdapter == null) return "";
        return mAdapter.getCount() + "";
    }

    private void networkStep(boolean bRefresh) {
        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {
            if(mActivity != null)
                ((BaseActivity) mActivity).tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {
                            mAdapter.mData.clear();
                            mAdapter.notifyDataSetChanged();
                            mListView.setPullLoadEnable(false);
                            mListView.stopRefresh();
                            mListView.stopLoadMore();
                            mRefreshLayout.setRefreshing(false);
                            if(mActivity != null)
                                ((BaseActivity) mActivity).notifyLogin(new LambdaExpression() {
                                    @Override
                                    public void action() {
                                        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {

                                        } else {
                                            networkStep(bRefresh);
                                        }
                                    }
                                });
                        } else {
                            networkStep(bRefresh);
                        }
                    }
                }, new OneParameterExpression<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        if(mActivity != null)
                            Toast.makeText(mActivity, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        mRefreshLayout.setRefreshing(false);
                        mListView.stopLoadMore();
                    }
                });
            return;
        }
        List<Message> tmpMessages = Optional.of(mAdapter.mData).orElse(new ArrayList<>());
        Integer theLastOneIdentifier = null;
        if (!bRefresh) {
            Message theLatestOne = tmpMessages.get(tmpMessages.size() - 1);
            theLastOneIdentifier = theLatestOne.getIdentifier();
        }
        News.NotificationType type = News.NotificationType.Empty;
        if (mType == 0) {
            type = News.NotificationType.Notice;
        } else if (mType == 1) {
            type = News.NotificationType.Comment;
        }
        Router.getMessageModule().message(type, theLastOneIdentifier, null, 500, new OneParameterExpression<Messages>() {
            @Override
            public void action(Messages msg) {
                onMessageFetchSuccess(bRefresh, msg);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, getString(R.string.notify_net), Toast.LENGTH_LONG).show();
//            mListView.stopRefresh();
                mListView.stopLoadMore();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null ;
    }

    private void onMessageFetchSuccess(boolean bRefresh, Messages msg) {
        if (bRefresh) {
            if (!msg.getAllMessage().isEmpty()) {
                mLayout.setVisibility(View.GONE);
                List<Message> msgList = msg.getAllMessage();
                if (mAdapter == null) {
                    mAdapter = new MessageAdapter();
                }
                mAdapter.setMessage(msgList);
                mListView.setAdapter(mAdapter);
                if (msg.getAllMessage().size() < 20) {
                    mListView.setPullLoadEnable(false);
                } else {
                    mListView.setPullLoadEnable(true);
                }
            } else {
                mListView.setPullLoadEnable(false);
                mAdapter.setMessage(new ArrayList<Message>());
                mAdapter.notifyDataSetChanged();
                mLayout.setVisibility(View.VISIBLE);
            }
//            mListView.stopRefresh();
            mRefreshLayout.setRefreshing(false);
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
        int unReadCount = 0;
        for (Message message : msg.getAllMessage()) {
            if (!message.getIsRead()) {
                unReadCount++;
            }
        }
        if (messageListener != null)
            messageListener.onFetch(mType, unReadCount);
//        PreferenceManager.getInstance(mContext).saveMsgLastRefreshTime(new Date().getTime());
    }

    public void clearMessage() {
        try {
            mAdapter.mData.clear();
            mAdapter.notifyDataSetChanged();
            mListView.setPullLoadEnable(false);
        } catch (Throwable t) {
            System.err.println("no message");
        }
    }

    public void markRead() {
        for (Message msg : mAdapter.mData) {
            msg.setIsRead(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    public interface OnMessageFetch {
        void onFetch(int tabIndex, int unReadCount);
    }

    //    public List<Message> mData = new ArrayList<>();
    private class MessageAdapter extends BaseAdapter {
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
            return position;
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
                holder.nameOrPic = (FrameLayout) convertView.findViewById(R.id.msg_item_food);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Message msg = mData.get(position);
            holder.userName.setText(msg.getFromUser().getNickName());
            if (msg.getFromUser().getAvatarURLSmall().isPresent())
                Picasso.with(parent.getContext())
                        .load(msg.getFromUser().getAvatarURLSmall().get())
                        .placeholder(R.drawable.default_user_head)
                        .fit()
                        .centerCrop()
                        .into(holder.userhead);
            if (mType == 1)
                holder.comment.setText(msg.getDescription());
            else {
                String description = null;
                if (msg.getType() == Message.Type.Favorite) {
                    description = String.format(getString(R.string.favor_your_recommendation), msg.getGourmet().isPresent() ? msg.getGourmet().get().getName() : "");
                } else if (msg.getType() == Message.Type.Follower) {
                    description = getString(R.string.follow_you);
                }
                holder.comment.setText(description);
            }
            if (msg.getIsRead())
                holder.comment.setTextColor(Color.GRAY);
            else
                holder.comment.setTextColor(Color.BLACK);
            holder.time.setText(StrUtil.formatTime(msg.getCreateTime(), mContext));
            if (mType == 0) {
                holder.nameOrPic.setVisibility(View.GONE);
            } else {
                if (msg.getGourmet().isPresent() && msg.getGourmet().get().getImageURLs() != null && msg.getGourmet().get().getImageURLs().size() != 0) {
                    Optional<String> gourmetImage = Optional.of(msg.getGourmet().get().getImageURLs().get(0) + "!Thumbnails");
                    Picasso.with(parent.getContext())
                            .load(gourmetImage.get())
                            .placeholder(R.drawable.empty_view_greeting)
                            .fit()
                            .centerCrop()
                            .into(holder.gourmetPic);
                    holder.gourmetName.setVisibility(View.GONE);
                    holder.gourmetPic.setVisibility(View.VISIBLE);
                } else {
                    if (msg.getGourmet().isPresent())
                        holder.gourmetName.setText(msg.getGourmet().get().getName());
                    holder.gourmetName.setVisibility(View.VISIBLE);
                    holder.gourmetPic.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        public void setMessage(List<Message> list) {
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
            FrameLayout nameOrPic;
        }
    }
}
