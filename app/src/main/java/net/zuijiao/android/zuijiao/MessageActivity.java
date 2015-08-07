package net.zuijiao.android.zuijiao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.PagerSlidingTab;
import com.zuijiao.view.RefreshAndInitListView;

import java.util.ArrayList;
import java.util.List;

/**
 * display notifications and comments , called from main activity menu button .
 * Created by xiaqibo on 2015/6/9.
 */
@ContentView(R.layout.activity_message)
public class MessageActivity extends BaseActivity implements MessageFragment.OnMessageFetch {
    @ViewInject(R.id.message_toolbar)
    private Toolbar mToolbar;
    /*@ViewInject(R.id.message_tabs)
    private PagerSlidingTab mTabs = null;
    @ViewInject(R.id.message_view_pager)
    private ViewPager mViewPager = null;*/
   /* @ViewInject(R.id.message_list_view)
    private ListView mMsgListView=null;*/
//    private MessageFragment mMsgFragment = null;
//    private MessageFragment mNotifyFragment = null;
    private View mContentView = null;
    public MessageAdapter mAdapter = null;
    private int msgType = -1;
//    private MessagePagerAdapter mPagerAdapter = null;
    private int msgPicIcons[] = {R.drawable.share_btn,R.drawable.share_qq,R.drawable.share_weibo,R.drawable.share_weixin};
    private int msgContains[] = {R.string.msg_draw_back_money,R.string.msg_go_to_comment,R.string.msg_thanks_register,R.string.msg_apply_host};

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        initTabsValue();
//        mPagerAdapter = new MessagePagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(mPagerAdapter);
//        mTabs.setViewPager(mViewPager);
//        mMsgListView.setAdapter(mAdapter);
    }

    /*private void initTabsValue() {
        //cursor color
        mTabs.setIndicatorColor(Color.WHITE);
        //tab divider color
        mTabs.setDividerColor(getResources().getColor(R.color.toolbar));
        //tab background
//        mTabs.setBackgroundColor(Color.parseColor("#373737"));
        mTabs.setBackgroundColor(getResources().getColor(R.color.toolbar));
        //tab bottom height
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        //cursor height
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics()));
        //selected tab text color
        mTabs.setSelectedTextColor(Color.parseColor("#eeeeee"));
        //unselected tab text color
        mTabs.setTextColor(getResources().getColor(R.color.unselected_tab_text_color));
    }*/

    @Override
    public void onFetch(int tabIndex, int unReadCount) {

    }

   /* private class MessageAdapter extends BaseAdapter {
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
            if (msg.getType() == Message.Type.Comment)
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
            if (msg.getType() == Message.Type.Favorite && msg.getType() == Message.Type.Follower) {
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
    }*/

    public class MessageAdapter extends BaseAdapter{
        public List<Message> mData = new ArrayList<>();
        @Override
        public int getCount() {
            if (mData == null)
                return 0;
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.msg_item1, null);
                holder = new ViewHolder();
                holder.msgPic = (ImageView)view.findViewById(R.id.msg_item_image);
                holder.msgContent = (TextView)view.findViewById(R.id.msg_item_msg_content);
                holder.dateTime = (TextView)view.findViewById(R.id.msg_item_date);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            Message msg = mData.get(i);
            Message.Type msgType = msg.getType();
          /*  switch (msgType){
                case 0:
                    holder.msgContent.setText(String.format(getString(R.string.msg_draw_back_money),mBanquet.getTitle()));
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }*/
            holder.dateTime.setText(StrUtil.formatTime(msg.getCreateTime(), mContext));
            return view;
        }

        public void setMessage(List<Message> list) {
            mData = list;
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView dateTime;
            TextView msgContent;
            ImageView msgPic;
        }

    }

    /**
     * view-pager adapter
     */
    /*public class MessagePagerAdapter extends FragmentPagerAdapter {


        public MessagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            *//*if (position == 1) {
                return getString(R.string.comment);
            } else if (position == 0) {
                return getString(R.string.notification);
            }*//*
            return "!";
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            if (mNotifyFragment == null) {
                mNotifyFragment = new MessageFragment(mContext, MessageActivity.this, 0);
            }
            if (mMsgFragment == null) {
                mMsgFragment = new MessageFragment(mContext, MessageActivity.this, 1);
            }
//            if (position == 0) {
//                return mNotifyFragment;
//            } else if (position == 1) {
//                return mMsgFragment;
//            } else {
                return new MessageFragment(mContext, MessageActivity.this);
//            }
        }
    }*/
}
