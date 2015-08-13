package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.message.Messages;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.message.Notification;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.RefreshAndInitListView;

import java.util.ArrayList;
import java.util.List;

/**
 * display notifications and comments , called from main activity menu button .
 * Created by xiaqibo on 2015/6/9.
 */
@ContentView(R.layout.mybanquet_order_item)
public class MessageActivity extends BaseActivity implements RefreshAndInitListView.MyListViewListener {
    @ViewInject(R.id.message_toolbar)
    private Toolbar mToolbar;
    /*@ViewInject(R.id.message_tabs)
    private PagerSlidingTab mTabs = null;
    @ViewInject(R.id.message_view_pager)
    private ViewPager mViewPager = null;*/
    @ViewInject(R.id.message_list_view)
    private RefreshAndInitListView mMsgListView=null;
    @ViewInject(R.id.message_empty_content)
    private LinearLayout mNullMsgList;
    @ViewInject(R.id.message_swipe_refresh)
    private SwipeRefreshLayout mRefreshLayout = null;
//    private MessageFragment mMsgFragment = null;
//    private MessageFragment mNotifyFragment = null;
    private View mContentView = null;
    public MessageAdapter mAdapter = null;
    private int msgType = -1;
//    private MessagePagerAdapter mPagerAdapter = null;
    private int msgPicIcons[] = {R.drawable.msg_banquent_list,R.drawable.msg_goto_comment,R.drawable.msg_apply_host,R.drawable.msg_improve_personal};
//    private int msgContains[] = {R.string.msg_draw_back_money,R.string.msg_go_to_comment,R.string.msg_thanks_register,R.string.msg_apply_host};

    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        initTabsValue();
//        mPagerAdapter = new MessagePagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(mPagerAdapter);
//        mTabs.setViewPager(mViewPager);
       /* mMsgListView.setOnItemClickListener(onItemClickListener);
        mMsgListView.setPullLoadEnable(false);
        mMsgListView.setPullRefreshEnable(false);
        mMsgListView.setListViewListener(this);
        mMsgListView.setAdapter(mAdapter);*/
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Notification notification = (Notification)mAdapter.getItem(position -1);
            if ("order".equals(notification.getMsgType())||"seller".equals(notification.getMsgType())){
                if (notification.getMsgLinkID() != null){
                    Intent intent = new Intent();
                    intent.putExtra("tradeNo",notification.getMsgLinkID() );
                    intent.setClass(mContext, BanquetOrderDetailActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext,getString(R.string.no_gourmet_related),Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

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

    private void networkStep(boolean bRefresh) {
        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {
            if(mContext != null)
                ((BaseActivity) mContext).tryLoginFirst(new LambdaExpression() {
                    @Override
                    public void action() {
                        if (Router.getInstance().getCurrentUser().equals(Optional.empty())) {
                            mAdapter.mData.clear();
                            mAdapter.notifyDataSetChanged();
                            mMsgListView.setPullLoadEnable(false);
                            mMsgListView.stopRefresh();
                            mMsgListView.stopLoadMore();
                            mRefreshLayout.setRefreshing(false);
                            if(mContext != null)
                                ((BaseActivity) mContext).notifyLogin(new LambdaExpression() {
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
                        if(mContext != null)
                            Toast.makeText(mContext, getResources().getString(R.string.notify_net2), Toast.LENGTH_LONG).show();
                        mRefreshLayout.setRefreshing(false);
                        mMsgListView.stopLoadMore();
                    }
                });
            return;
        }

    }

    public class MessageAdapter extends BaseAdapter{
        public List<Notification> mData = new ArrayList<Notification>();
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
            Notification msg = mData.get(i);
            String msgType = msg.getMsgType();
            if("order".equals(msgType)) {
                holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[0]));
            }else if ("sellerOrder".equals(msgType)) {
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[0]));
            }else if ("comment".equals(msgType)) {
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[1]));
            }else if ("profile".equals(msgType)) {
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[3]));
            }else if ("application".equals(msgType)) {
                    holder.msgPic.setImageDrawable(getDrawable(msgPicIcons[2]));
            }else {
            }
            holder.msgContent.setText(msg.getMsgContent());
            holder.dateTime.setText(msg.getCreateTime());
            return view;
        }

        public void setMessage(List<Notification> list) {
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
