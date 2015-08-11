package net.zuijiao.android.zuijiao;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.ImageItem;
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
    @ViewInject(R.id.message_list_view)
    private ListView mMsgListView=null;
    @ViewInject(R.id.message_empty_content)
    private LinearLayout mNullMsgList;
//    private MessageFragment mMsgFragment = null;
//    private MessageFragment mNotifyFragment = null;
    private View mContentView = null;
    public MessageAdapter mAdapter = null;
    private int msgType = -1;
//    private MessagePagerAdapter mPagerAdapter = null;
    private int msgPicIcons[] = {R.drawable.msg_banquent_list,R.drawable.msg_goto_comment,R.drawable.msg_apply_host,R.drawable.msg_improve_personal};
    private int msgContains[] = {R.string.msg_draw_back_money,R.string.msg_go_to_comment,R.string.msg_thanks_register,R.string.msg_apply_host};

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        initTabsValue();
//        mPagerAdapter = new MessagePagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(mPagerAdapter);
//        mTabs.setViewPager(mViewPager);
        mMsgListView.setAdapter(mAdapter);
        mMsgListView.setOnItemClickListener(onItemClickListener);
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Message msg = (Message)mAdapter.getItem(position -1);
            if (msg.getType() == Message.Type.Favorite || msg.getType() == Message.Type.Comment){
                if (msg.getGourmet().isPresent()){
                    Gourmet gourmet = msg.getGourmet().get();
                    Intent intent = new Intent();
                    intent.putExtra("selected_gourmet", gourmet);
                    intent.setClass(mContext, GourmetDetailActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext,getString(R.string.no_gourmet_related),Toast.LENGTH_SHORT).show();
                }
            }else if (msg.getType() == Message.Type.Follower){
                if (msg.getFromUser() != null) {
                    TinyUser user = msg.getFromUser();
                    Intent intent = new Intent();
                    intent.setClass(mContext, UserInfoActivity.class);
                    intent.putExtra("tiny_user", user);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_user_related), Toast.LENGTH_SHORT).show();
                }
            }else {

            }
        }
    };

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
            if (mData.size() == 0){
                mNullMsgList.setVisibility(View.VISIBLE);
            }
            Message msg = mData.get(i);
            Message.Type msgType = msg.getType();
            switch (msgType){
                case Comment:
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[0]));
                    holder.msgContent.setText(String.format(getString(msgContains[0]),0));
                    break;
                case Favorite:
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[2]));
                    holder.msgContent.setText(getString(msgContains[2]));
                    break;
                case Follower:
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[1]));
                    holder.msgContent.setText(String.format(getString(msgContains[1]),0));
                    break;
                case Empty:
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[3]));
                    holder.msgContent.setText(getString(msgContains[3]));
                    break;
            }
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
