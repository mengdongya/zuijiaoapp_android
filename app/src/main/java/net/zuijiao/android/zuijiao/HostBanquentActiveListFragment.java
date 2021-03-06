package net.zuijiao.android.zuijiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.view.RefreshAndInitListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mengdongya on 2015/8/13.
 */
public class HostBanquentActiveListFragment extends Fragment implements
        RefreshAndInitListView.MyListViewListener,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{

    private View mContentView;
    private RefreshAndInitListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private View emptyView;
    private int tabIndex = 0;
    private List<Order> orderList;
    private Integer lastedId = null;
    private String[] weekDays;
    private TextView mBlankText;
    private Orders mOrders;
    private UpdateBroadCast updateBroadCast;


    @Override
    public void onResume() {
        super.onResume();
    }

    public static HostBanquentActiveListFragment newInstance(int position) {
        HostBanquentActiveListFragment fragment = new HostBanquentActiveListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public HostBanquentActiveListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mContentView != null) {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
            return mContentView;
        }
        tabIndex = getArguments().getInt("position");
        updateBroadCast = new UpdateBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageDef.ACTION_ORDER_CREATED);
        getActivity().registerReceiver(updateBroadCast, filter);
        weekDays = getResources().getStringArray(R.array.week_days);
        mContentView = inflater.inflate(R.layout.fragment_order_list, null);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.order_fragment_swipe_refresh);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_order_list);
        emptyView = mContentView.findViewById(R.id.order_empty_content);
        mBlankText = (TextView) mContentView.findViewById(R.id.order_list_blank_text);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mRefreshLayout.setOnRefreshListener(this);
        mListView.setListViewListener(this);
        mListView.setOnItemClickListener(this);
        networkStep(true);
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * fetch order list
     *
     * @param bRefresh
     */
    private void networkStep(boolean bRefresh) {
        /**
         * check if current user is exist ,if not ,try login first or notify login
         */
        if (!Router.getInstance().getCurrentUser().isPresent()) {
            ((BaseActivity) getActivity()).tryLoginFirst(new LambdaExpression() {
                @Override
                public void action() {
                    if (!Router.getInstance().getCurrentUser().isPresent()) {
                        if (orderList != null)
                            orderList.clear();
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                        ((BaseActivity) getActivity()).notifyLogin(new LambdaExpression() {
                            @Override
                            public void action() {
                                if (Router.getInstance().getCurrentUser().isPresent()) {
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
                    Toast.makeText(getActivity(), R.string.notify_net2, Toast.LENGTH_LONG).show();
                    mRefreshLayout.setRefreshing(false);
                }
            });
            return;
        }
        OrderStatus status;
        switch (tabIndex) {
            case 0:
                status = OrderStatus.Waiting;
                break;
            case 1:
                status = OrderStatus.Finished;
                break;
            case 2:
                status = OrderStatus.Canceled;
                break;
            default:
                status = OrderStatus.Unpaid;
        }
        mRefreshLayout.setRefreshing(true);
        Router.getBanquentModule().sellerOrders(status.toString(), lastedId, 20, new OneParameterExpression<Orders>() {
            @Override
            public void action(Orders orders) {
                mOrders = orders;
                if (bRefresh) {
                    orderList = orders.getOrderList();
                    if (orderList.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        mBlankText.setText(getString(R.string.no_going_banquent_order));
                    } else
                        emptyView.setVisibility(View.GONE);
                    lastedId = null;
                } else {
                    if (orderList == null)
                        orderList = new ArrayList<Order>();
                    orderList.addAll(orders.getOrderList());
                    lastedId = orderList.get(orderList.size() - 1).getIdentifier();
                }
                if (orders.getOrderList().size() < 20)
                    mListView.setPullLoadEnable(false);
                else
                    mListView.setPullLoadEnable(true);
                if (mListView.getAdapter() == null)
                    mListView.setAdapter(mAdapter);
                else
                    mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (tabIndex != 1)
            getActivity().unregisterReceiver(updateBroadCast);
        super.onDestroy();
    }

    /**
     * orders list adapter
     */
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (orderList == null)
                return 0;
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.mybanquet_order_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            Order order = orderList.get(position);
            if (order.getUser().getAvatarUrl().isPresent()){
                Picasso.with(getActivity().getApplicationContext()).load(order.getUser().getAvatarURLSmall().get()).placeholder(R.drawable.default_user_head).fit().centerCrop().into(holder.image);
            }else {
                holder.image.setImageResource(R.drawable.default_user_head);
            }
            holder.title.setText(order.getEvent().getTitle());
            holder.date.setText(formatDate(order.getEvent().getTime()));
            holder.personNumber.setText(order.getQuantity() + getString(R.string.people));
            holder.nickname.setText(order.getUser().getNickName());
            String remark = order.getRemark() ;
            if(remark == null || remark.trim().equals("")){
//            if (order.getRemark().length() == 0|| "".equals(order.getRemark())){
                holder.remark.setText(getString(R.string.none));
            }else {
                holder.remark.setText(order.getRemark());
            }
            return convertView;
        }
    };

    @Override
    public void onRefresh() {
        networkStep(true);
    }

    @Override
    public void onLoadMore() {
        networkStep(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= 1;
        Intent intent = new Intent(getActivity().getApplicationContext(), BanquetOrderDetailActivity.class);
        intent.putExtra("order", orderList.get(position));
        startActivity(intent);
    }

    class ViewHolder {
        @ViewInject(R.id.my_banquet_order_item_head)
        ImageView image;
        @ViewInject(R.id.my_banquet_order_item_nickname)
        TextView nickname;
        @ViewInject(R.id.my_banquent_order_item_title)
        TextView title;
        @ViewInject(R.id.my_banquent_order_item_datetime)
        TextView date;
        @ViewInject(R.id.my_banquet_order_item_person_count)
        TextView personNumber;
        @ViewInject(R.id.my_banquet_order_item_remark)
        TextView remark;

        public ViewHolder(View convertView) {
            com.lidroid.xutils.ViewUtils.inject(this, convertView);
        }

    }

    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(getString(R.string.month_day), date.getMonth() + 1, date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(String.format(getString(R.string.banquet_format_time), date.getHours(), date.getMinutes()));
        strBuilder.append(" ");
        return strBuilder.toString();
    }

    private class UpdateBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (tabIndex != intent.getIntExtra("tabIndex", -1))
                onRefresh();
        }
    }
}
