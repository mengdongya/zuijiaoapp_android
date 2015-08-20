package net.zuijiao.android.zuijiao;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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

import javax.crypto.Mac;

/**
 * show one status of my order , included in my order fragment
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
    //different index shows different status of order
    //index =0 : status coming
    //index =1 : to be evaluated
    //index =2 : all order
    private int tabIndex = 0;
    private List<Order> orderList;
    private Integer lastedId = null;
    private String[] weekDays;
    private TextView mBlankText;
    private int[] mBlankTextRes = {R.string.no_coming_order, R.string.no_finished_order, R.string.no_order};
    private Orders mOrders;
    //private long currentSystemTime;//sec
    private UpdateBroadCast updateBroadCast;

//    public OrderListFragment(int index) {
//        super();
//        this.tabIndex = index;
//    }

//    Handler handler = new Handler();
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            currentSystemTime++;
//            handler.postDelayed(this, 1000);
//        }
//    };

    @Override
    public void onResume() {
        super.onResume();
    }

    public static OrderListFragment newInstance(int position) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
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
//                status = OrderStatus.Waiting;
//                //status = OrderStatus.Unclosed;
                status = OrderStatus.unFinished;
                break;
            case 1:
//                status = OrderStatus.Finished;
                status = OrderStatus.unComment;
                break;
            case 2:
                status = OrderStatus.All;
                break;
            default:
                status = OrderStatus.All;
        }
        if(bRefresh)
            mRefreshLayout.setRefreshing(true);
        Router.getBanquentModule().orders(status, lastedId, 20, new OneParameterExpression<Orders>() {
            @Override
            public void action(Orders orders) {
                mOrders = orders;
                //currentSystemTime = orders.getCurrentServerTime();
                //runnable.run();
//                if (tabIndex != 1) {
//                    handler.removeCallbacks(runnable);
//                    handler.post(runnable);
//                }
                if (bRefresh) {
                    orderList = orders.getOrderList();
                    if (orderList.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        mBlankText.setText(mBlankTextRes[tabIndex]);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.banquet_order_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            Order order = orderList.get(position);
            Picasso.with(getActivity().getApplicationContext()).load(order.getEvent().getImageUrl()).placeholder(R.drawable.empty_view_greeting).fit().centerCrop().into(holder.image);
            holder.title.setText(order.getEvent().getTitle());
            String dateInfo = formatDate(order.getEvent().getTime());
            //holder.date.setText(dateInfo + order.getAddress());
            holder.date.setText(dateInfo + " · " + order.getQuantity() + getString(R.string.people));
//            if (tabIndex == 0) {
//                if (position % 2 == 0) {
//                    holder.review.setVisibility(View.VISIBLE);
//                    holder.review.setText(getString(R.string.pay_right_now));
//                    holder.review.setEnabled(true);
//                    holder.review.setTextColor(getResources().getColor(R.color.banquet_theme));
//                } else {
//                    holder.review.setVisibility(View.GONE);
//                }
//            }
            holder.review.setVisibility(View.GONE);
            if (order.getStatus() == OrderStatus.Unpaid) {
                holder.review.setVisibility(View.VISIBLE);
                holder.review.setEnabled(true);
                holder.review.setTextColor(getResources().getColor(R.color.banquet_theme));
                if (order.getStatus() == OrderStatus.Unpaid) {
                    holder.review.setText(getString(R.string.pay_right_now));
                    holder.review.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), BanquetOrderActivity.class);
                            intent.putExtra("order", order);
                            intent.putExtra("surplusTime", (order.getDeadline().getTime() / 1000) - mOrders.getCurrentServerTime());
                            intent.putExtra("isCreate", false);
                            intent.putExtra("attendeeNum", order.getQuantity());
                            //startActivity(intent);
                            getActivity().startActivityForResult(intent, MainActivity.ORDER_REQUEST);
                        }
                    });
                }
            }
//                if (order.getStatus() == OrderStatus.Uncomment) {
//                    holder.review.setText(getString(R.string.to_evaluate));
//                    holder.review.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent();
//                            intent.setClass(getActivity(), ReviewActivity.class);
//                            intent.putExtra("orderId", order.getIdentifier());
//                            intent.putExtra("tabIndex", tabIndex);
//                            getActivity().startActivityForResult(intent, MainActivity.COMMENT_REQUEST);
//                        }
//                    });
//                }
            if (tabIndex >= 1) {
                if (order.getStatus() != OrderStatus.Unpaid && order.getStatus() != OrderStatus.Canceled && order.getStatus() != OrderStatus.Waiting) {
                    holder.review.setVisibility(View.VISIBLE);
                    if (order.getIsCommented()) {
                        holder.review.setText(getString(R.string.over_evaluate));
                        holder.review.setEnabled(false);
                        holder.review.setTextColor(getResources().getColor(R.color.tv_light_gray));
                    } else {
                        holder.review.setText(getString(R.string.to_evaluate));
                        holder.review.setEnabled(true);
                        holder.review.setTextColor(getResources().getColor(R.color.banquet_theme));
                        holder.review.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), ReviewActivity.class);
                                intent.putExtra("orderId", order.getIdentifier());
                                intent.putExtra("tabIndex", tabIndex);
                                getActivity().startActivityForResult(intent, MainActivity.COMMENT_REQUEST);
                            }
                        });
                    }
                }
            }
            //       }
//
//        else
//
//        {
//            holder.review.setEnabled(false);
//            holder.review.setVisibility(View.GONE);
//        }

//            if (tabIndex > 0) {
//                if (order.getStatus() != OrderStatus.Waiting && order.getStatus() != OrderStatus.Canceled) {
//                    holder.review.setVisibility(View.VISIBLE);
//                    if (order.getIsCommented()) {
//                        holder.review.setText(getString(R.string.over_evaluate));
//                        holder.review.setEnabled(false);
//                        holder.review.setTextColor(getResources().getColor(R.color.tv_light_gray));
//                    } else {
//                        holder.review.setText(getString(R.string.to_evaluate));
//                        holder.review.setEnabled(true);
//                        holder.review.setTextColor(getResources().getColor(R.color.banquet_theme));
//                        holder.review.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent();
//                                intent.setClass(getActivity(), ReviewActivity.class);
//                                intent.putExtra("orderId", order.getIdentifier());
//                                intent.putExtra("tabIndex", tabIndex);
//                                getActivity().startActivityForResult(intent, MainActivity.COMMENT_REQUEST);
//                            }
//                        });
//                    }
//                } else {
//                    holder.review.setVisibility(View.GONE);
//                }
//                //  }
//            }
            switch (order.getStatus())

            {
                case Canceled:
                    holder.situation.setText(getString(R.string.canceled_banquet));
                    break;
                case Waiting:
                    holder.situation.setText(getString(R.string.waiting_fo_you));
                    break;
                case Finished:
                    holder.situation.setText(getString(R.string.finished_banquet));
                    break;
                case Unpaid:
                    if (order.getDeadline().getTime() / 1000 - mOrders.getCurrentServerTime() < 0) {
                        holder.review.setVisibility(View.GONE);
                        holder.situation.setText(getString(R.string.timeout_pay));
                    } else {
                        holder.situation.setText(getString(R.string.waiting_pay));
                    }
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
        long surplusTime = (orderList.get(position).getDeadline().getTime() / 1000) - mOrders.getCurrentServerTime();
        intent.putExtra("surplusTime", surplusTime);
        //startActivity(intent);
        getActivity().startActivityForResult(intent, MainActivity.ORDER_REQUEST);
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
        @ViewInject(R.id.banquet_order_item_review)
        TextView review;

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