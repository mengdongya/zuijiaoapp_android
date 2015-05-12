package com.zuijiao.controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Interceptor;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.OSUtil;

import net.zuijiao.android.zuijiao.BuildConfig;
import net.zuijiao.android.zuijiao.LocationActivity;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ActivityTask extends Application {
    private static final String LOGTAG = "ActivityTask";
    public static Typeface boldFont = null;
    private static ActivityTask mInstance = null;
    public LocationClient mLocationClient;
    public GeofenceClient mGeofenceClient;
    public MyLocationListener mMyLocationListener;
    private String mapUriStr = "http://maps.google.cn/maps/api/geocode/json?latlng={0},{1}&sensor=true&language=zh-CN";
    // opened activities
    private LinkedList<Activity> mActivitiesList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
//        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/NotoSansHans-Light.otf");
//
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mGeofenceClient = new GeofenceClient(getApplicationContext());
        File cacheDirectory = getApplicationContext().getCacheDir();
        Interceptor interceptor = null;

        if (BuildConfig.DEBUG) {
            interceptor = new StethoInterceptor();

            cacheDirectory = null;
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
        Router.setup(BuildConfig.Base_Url, BuildConfig.Request_Key, cacheDirectory, interceptor);

    }

    public static ActivityTask getInstance() {
        if (mInstance == null) {
            mInstance = new ActivityTask();

        }
        return mInstance;
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        int nAPILevl = OSUtil.getAPILevel();
        if (nAPILevl < 20) {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasks = am.getRunningTasks(10);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(
                        context.getPackageName())) {
                    return true;
                }
            }
            return false;
        } else {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = context.getPackageName();
            List<RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            if (appProcesses == null) {
                return true;
            }
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(packageName)) {
                    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                        return true;
                    } else {
                        break;
                    }
                }
            }
            return false;
        }
    }

    public static Typeface getTypeFace(Context context) {
        if (boldFont == null) {
            boldFont = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSansHans-Regular.otf");
        }
        return boldFont;
    }

    public void addActivity(Activity activity) {
        synchronized (mActivitiesList) {
            mActivitiesList.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        synchronized (mActivitiesList) {
            mActivitiesList.remove(activity);
        }
    }

    // exit App
    public void exit() {
        for (Activity activity : mActivitiesList) {
            activity.finish();
        }
        System.exit(0);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String city = location.getCity();
            String province = location.getProvince();
            if (city != null && province != null) {
                if (!province.equals(city)) {
                    LocationActivity.mCurrentLocationTv.setText(province + city);
                } else {
                    LocationActivity.mCurrentLocationTv.setText(province);
                }
                LocationActivity.mSwitcher.showNext();
                LocationActivity.autoLocationCity = city;
                LocationActivity.autoLocationProvince = province;
                System.out.println(city);
                mLocationClient.stop();
            }
        }
    }


}
