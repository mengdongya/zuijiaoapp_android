package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Notifications;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.message.Message;
import com.zuijiao.android.zuijiao.model.user.TinyUser;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.utils.CacheUtils;
import com.zuijiao.utils.StrUtil;
import com.zuijiao.view.RefreshAndInitListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * display notifications and comments , called from main activity menu button .
 * Created by xiaqibo on 2015/6/9.
 */
@ContentView(R.layout.activity_message)
public class MessageActivity extends BaseActivity  {
    @ViewInject(R.id.message_toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.message_list_view)
    private RefreshAndInitListView mListView =null;
    @ViewInject(R.id.message_empty_content)
    private LinearLayout mEmptyContentView;
    @ViewInject(R.id.message_swipe_refresh)
    private SwipeRefreshLayout mRefreshLayout;
    public NotificationAdapter mAdapter = null;
    private int msgPicIcons[] = {R.drawable.msg_banquent_list,R.drawable.msg_goto_comment,R.drawable.msg_apply_host,R.drawable.msg_improve_personal};
    private int msgContains[] = {R.string.msg_draw_back_money,R.string.msg_go_to_comment,R.string.msg_thanks_register,R.string.msg_apply_host};
    private Integer nextCursor ;
    private ArrayList<Notifications.Notification> mNotifications ;
    private String[] weekDays;
    @Override
    protected void registerViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        weekDays = getResources().getStringArray(R.array.week_days);
        mAdapter= new NotificationAdapter() ;
        mListView.setAdapter(mAdapter);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setOnItemClickListener(onItemClickListener);
        networkStep(true);
    }



    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Notifications.Notification notification = mNotifications.get(position -1) ;
            Integer orderID = notification.getLinkID() ;
            List<Integer> ids = new ArrayList<>() ;
            ids.add(notification.getId()) ;
            Router.getMessageModule().markBanquetMsgAsRead(ids, new LambdaExpression() {
                @Override
                public void action() {
                    notification.setIsRead(true);
                    mAdapter.notifyDataSetChanged();
                }
            }, null);
            Intent intent = new Intent();
            switch (notification.getType()){
                case comment :
//                    intent.setClass(mContext, ReviewActivity.class);
//                    intent.putExtra("orderId", orderID);
//                    startActivity(intent);
//                    break ;
                case order:
                    Router.getBanquentModule().order(orderID, new OneParameterExpression<Orders>() {
                        @Override
                        public void action(Orders orders) {
                            Order order = orders.getOrder() ;
                            Intent intent = new Intent(mContext, BanquetOrderDisplayActivity.class);
                            intent.putExtra("order", order);
                            startActivityForResult(intent, MainActivity.ORDER_REQUEST);
                        }
                    }, new OneParameterExpression<String>() {
                        @Override
                        public void action(String s) {
                            Toast.makeText(mContext,  R.string.notify_net2 , Toast.LENGTH_SHORT) .show();
                        }
                    });
                    break ;
                case sellerOrder:
                   break ;
                case profile:
                    intent.setClass(mContext , EditUserInfoActivity.class) ;
                    Optional<TinyUser> user = Router.getInstance().getCurrentUser();
                    if (!user.isPresent()) {
                        user = Optional.of(new TinyUser());
                        AuthorInfo authInfo = PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg();
                        user.get().setNickName(authInfo.getUserName());
                        user.get().setAvatarURL(authInfo.getHeadPath());
                        user.get().setIdentifier(authInfo.getUserId());
                    }
                    intent.putExtra("tiny_user", user.get());
                    startActivity(intent);
                  break ;
                case application:
                    break ;
                default:
                   break ;
            }
        }
    };

    @Override
    protected void fetchContent() {
        networkStep(true);
    }

    public void networkStep(boolean bRefresh){
        Router.getMessageModule().banquetNotifications(null, nextCursor, 20, new OneParameterExpression<Notifications>() {
            @Override
            public void action(Notifications notifications) {
                if (bRefresh) {
                    mRefreshLayout.setRefreshing(false);
                    mNotifications = notifications.getItems() ;
                } else {
                    if(mNotifications == null)
                        mNotifications = new ArrayList<Notifications.Notification>() ;
                    mNotifications.addAll(notifications.getItems()) ;
                }
                if (notifications.getItemCount() < 20 && !bRefresh)
                    mListView.setPullLoadEnable(false);
                else
                    mListView.setPullLoadEnable(true);
                if(mAdapter.getCount() == 0){
                    mEmptyContentView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyContentView.setVisibility(View.GONE);
                }
                nextCursor = notifications.getNextCursor() ;
                mAdapter.notifyDataSetChanged();
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(mContext, R.string.notify_net2 , Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class NotificationAdapter extends BaseAdapter{
//        public List<Message> mData = new ArrayList<>();
        @Override
        public int getCount() {
            if (mNotifications == null)
                return 0;
            return mNotifications.size();
        }

        @Override
        public Object getItem(int position) {
            return mNotifications.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public Notifications.NotificationType getItemType(int position ){
            if (mNotifications == null)
                return null;
            return mNotifications.get(position).getType() ;
        }
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.msg_item1, null);
                holder = new ViewHolder();
                holder.msgPic = (ImageView)view.findViewById(R.id.msg_item_image);
                holder.msgContent = (TextView)view.findViewById(R.id.msg_item_msg_content);
                holder.dateTime = (TextView)view.findViewById(R.id.msg_item_date);
                holder.unread = view.findViewById(R.id.msg_item_unread_view) ;
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            Notifications.Notification notification = mNotifications.get(position);
            Notifications.NotificationType msgType = notification.getType();
            if(notification.getIsRead())
                holder.unread.setVisibility(View.GONE);
            else
                holder.unread.setVisibility(View.VISIBLE);
            holder.msgContent.setText(notification.getContent());
            holder.dateTime.setText(formatDate(notification.getCreateDate()));
            switch (msgType){
                case order:
                   holder.msgPic.setImageResource(R.drawable.msg_goto_comment);
                    break;
                case sellerOrder:
                    holder.msgPic.setImageResource(R.drawable.msg_banquent_list);
                    break;
                case comment:
                   holder.msgPic.setImageResource(R.drawable.msg_goto_comment);
                    break;
                case profile:
                    holder.msgPic.setImageResource(R.drawable.msg_improve_personal);
                    break;
                case application :
                    holder.msgPic.setImageResource(R.drawable.msg_apply_host);
                    break ;
                default :
                    break ;
            }
            return view;
        }

//        public void setMessage(List<Message> list) {
//            notifyDataSetChanged();
//        }

        class ViewHolder {
            TextView dateTime;
            TextView msgContent;
            ImageView msgPic;
            View unread ;
        }

    }


    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(getString(R.string.month_day), date.getMonth() + 1, date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(String.format(mContext.getString(R.string.banquet_format_time), date.getHours(), date.getMinutes()));
        strBuilder.append(" ");
        return strBuilder.toString();
    }
}
