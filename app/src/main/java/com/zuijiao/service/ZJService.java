package com.zuijiao.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.zuijiao.android.zuijiao.model.Banquent.Order;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Orders;
import com.zuijiao.controller.MessageDef;

import net.zuijiao.android.zuijiao.BanquetOrderActivity;

import java.util.List;

/**
 * Created by yitianhao on 2015/7/31.
 */
public class ZJService extends BaseService {

    private final IBinder binder = new ZJBinder();
    private Handler handler = new Handler();
    private List<Order> orderList;
    private Order order;
    private long mSurplusTime = -1;
    private OrderTimeBroadCast orderTimeBroadCast = new OrderTimeBroadCast();
    private boolean sendNotification = true;

    public class ZJBinder extends Binder {
        public ZJService getService() {
            return ZJService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(orderTimeBroadCast, new IntentFilter(MessageDef.ACTION_ORDER_SURPLUS_TIME));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        unregisterReceiver(orderTimeBroadCast);
    }

    private class OrderTimeBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Orders orders = (Orders) intent.getSerializableExtra("orders");
            orderList = orders.getOrderList();
            for (int i = orderList.size() - 1; i < 0; i--) {
                if (orderList.get(i).getStatus() != OrderStatus.Unpaid)
                    break;
                order = orderList.get(i);
                mSurplusTime = order.getDeadline().getTime() / 1000 - orders.getCurrentServerTime();
                handler.removeCallbacks(runnable);
                handler.post(runnable);
                return;
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mSurplusTime-- <= 0) {
                Intent intent = new Intent();
                intent.setAction(MessageDef.ACTION_ORDER_CREATED);
                intent.putExtra("tabIndex", 1);
                sendBroadcast(intent);
                handler.removeCallbacks(this);
            }
            if (mSurplusTime <= 60) {
                if (sendNotification) {
                    showNotification(order.getEvent().getTitle(), "该订单的支付剩余时间不足一分钟了哦！",
                            "您有一个订单即将过期", "order", order, "surplusTime", mSurplusTime, BanquetOrderActivity.class);
                    sendNotification = false;
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}
