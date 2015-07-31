package com.zuijiao.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.zuijiao.utils.OSUtil;

import net.zuijiao.android.zuijiao.R;

import java.io.Serializable;

/**
 * Created by yitianhao on 2015/7/31.
 */
public class BaseService extends Service {

    private static int notificationCount = 1;
    private static final String APP_NAME = "zuijiao";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private Intent mNotificationIntent;
    private Vibrator mVibrator;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, APP_NAME);
        initNotification();
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected void showNotification(String title, String content, String ticker, String key, Serializable value, String key1, long value1, Class cls) {
        mWakeLock.acquire();
        setNotification(title, content, ticker);
        mNotificationIntent = new Intent(getApplicationContext(), cls);
        mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mNotificationIntent.putExtra(key, value);
        mNotificationIntent.putExtra(key1, value1);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notificationCount, mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notificationCount++, mNotificationBuilder.build());
        mWakeLock.release();
    }

    private void setNotification(String title, String content, String ticker) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        int smallIcon = 0;
        if (OSUtil.getAPILevel() > 20) {
            smallIcon = R.drawable.notification_icon_21;
        } else {
            smallIcon = R.drawable.notification_icon;
        }
        mNotificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        mNotificationBuilder.setSmallIcon(smallIcon, 1000)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(ticker)
                .setNumber(12)
                .setLargeIcon(bm)
                .setAutoCancel(true);
    }
}
