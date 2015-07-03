package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.view.RefreshAndInitListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaqibo on 2015/6/16.
 */
@SuppressLint("ValidFragment")
public class OrderListFragment extends Fragment implements
        RefreshAndInitListView.MyListViewListener,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {
    private View mContentView;
    private RefreshAndInitListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private View emptyView;
    private int tabIndex = 0;
    private List<Order> orderList;
    private Integer lastedId = null;
    private String[] weekDays;

    public OrderListFragment(int index) {
        super();
        this.tabIndex = index;
    }

    public OrderListFragment() {
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
        weekDays = getResources().getStringArray(R.array.week_days);
        mContentView = inflater.inflate(R.layout.fragment_order_list, null);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.order_fragment_swipe_refresh);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_order_list);
        emptyView = mContentView.findViewById(R.id.order_empty_content);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mRefreshLayout.setOnRefreshListener(this);
        mListView.setListViewListener(this);
        mListView.setOnItemClickListener(this);
        networkStep(true);
        return mContentView;
    }

    private void networkStep(boolean bRefresh) {
        OrderStatus status;
        switch (tabIndex) {
            case 0:
                status = OrderStatus.Waiting;
                break;
            case 1:
                status = OrderStatus.Finished;
                break;
            case 2:
                status = OrderStatus.All;
                break;
            default:
                status = OrderStatus.All;
        }
        mRefreshLayout.setRefreshing(true);
        Router.getBanquentModule().orders(status, lastedId, 20, new OneParameterExpression<Orders>() {
            @Override
            public void action(Orders orders) {
                if (bRefresh) {
                    orderList = orders.getOrderList();
                    if (orderList.size() == 0)
                        emptyView.setVisibility(View.VISIBLE);
                    else
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

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (orderList == null)
                return 0;
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.banquet_order_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            Order order = orderList.get(position);
            Picasso.with(getActivity().getApplicationContext()).load(order.getImageUrl()).placeholder(R.drawable.empty_view_greeting).into(holder.image);
            holder.title.setText(order.getTitle());
            String dateInfo = formatDate(order.getCreateTime());
            holder.date.setText(dateInfo + order.getAddress());
            switch (order.getStatus()) {
                case Canceled:
                    holder.situation.setText(getString(R.string.canceled_banquet));
                    break;
                case Waiting:
                    holder.situation.setText(getString(R.string.waiting_fo_you));
                    break;
                case Finished:
                    holder.situation.setText(getString(R.string.finished_banquet));
                    break;
                default:
                    holder.situation.setText(getString(R.string.waiting_banquet));
                    break;
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
        Intent intent = new Intent(getActivity(), BanquetOrderDisplayActivity.class);
        intent.putExtra("order", orderList.get(position));
        startActivity(intent);
    }

    class ViewHolder {
        @ViewInject(R.id.banquet_order_item_image)
        ImageView image;
        @ViewInject(R.id.banquet_order_item_name)
        TextView title;
        @ViewInject(R.id.banquet_order_item_date_location)
        TextView date;
        @ViewInject(R.id.banquet_order_item_status)
        TextView situation;

        public ViewHolder(View convertView) {
            com.lidroid.xutils.ViewUtils.inject(this, convertView);
        }
    }

    private String formatDate(Date date) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format(getString(R.string.month_day), date.getMonth(), date.getDate()));
        strBuilder.append(" ");
        strBuilder.append(weekDays[date.getDay()]);
        strBuilder.append(" ");
        strBuilder.append(date.getHours() + ":00");
        strBuilder.append(" ");
        return strBuilder.toString();
    }
}
