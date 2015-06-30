package net.zuijiao.android.zuijiao;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.utils.OSUtil;

import org.android.agoo.client.BaseConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaqibo on 2015/5/21.
 */
public class UmengAgentPushService extends UmengBaseIntentService {
    private static int notificationId = 100;

    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);

        String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);

        UMessage msg = null;
        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject js = jsonObject.getJSONObject("body");
            String title = js.getString("title");
            String text = js.getString("text");
            String ticker = js.getString("ticker");
            showNotification(title, text, ticker);
            msg = new UMessage(jsonObject);
            UTrack.getInstance(context).trackMsgClick(msg);
            Intent notifyMessageReceived = new Intent(MessageDef.ACTION_PUSH_RECEIVED);
            sendBroadcast(notifyMessageReceived);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String title, String text, String ticker) {
        Bitmap btm = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon);
        int smallIcon = 0;
        if (OSUtil.getAPILevel() > 20) {
            smallIcon = R.drawable.notification_icon_21;
        } else {
            smallIcon = R.drawable.notification_icon;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setSmallIcon(smallIcon, 1000)
                .setContentTitle(title)
                .setContentText(text);
        mBuilder.setTicker(ticker);
        mBuilder.setNumber(12);
        mBuilder.setLargeIcon(btm);
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId++, mBuilder.build());
    }
}
