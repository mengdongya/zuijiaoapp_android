package net.zuijiao.android.zuijiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.ActivityTask;

/**
 * Created by xiaqibo on 2015/8/6.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetWorkStateReceiver" ;
    private NetworkInfo mActiveInfo = null ;
    private NetworkInfo mobileInfo = null ;
    private NetworkInfo wifiInfo = null ;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.i(TAG , "mobileInfo" + mobileInfo.getState().toString() + "   wifiInfo" + wifiInfo.getState().toString() ) ;
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if(activeInfo != null ){
            Log.i(TAG , "activeInfo = " + activeInfo.getState().toString()) ;
            try {
                netStep();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }else
            Log.i(TAG , "activeInfo == null!!!!!!") ;
    }

    private void netStep() throws Throwable{
        BaseActivity currentActivity = (BaseActivity) ActivityTask.getInstance() .getActivitiesList().getLast();
        currentActivity.tryLoginFirst(new LambdaExpression() {
            @Override
            public void action() {
                if(!currentActivity.mInfoGot){
                    currentActivity.fetchContent();
                }
            }
        } , null);
    }
}
