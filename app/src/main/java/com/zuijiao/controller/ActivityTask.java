package com.zuijiao.controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;

import com.zuijiao.utils.OSUtil;

import java.util.LinkedList;
import java.util.List;

public class ActivityTask extends Application {
	private static final String LOGTAG = "ActivityTask";
	private static ActivityTask mInstance = null;
	// opened activities
	private LinkedList<Activity> mActivitiesList = new LinkedList<Activity>();

	public static ActivityTask getInstance() {
		if (mInstance == null) {
			mInstance = new ActivityTask();
		}
		return mInstance;
	}

    @Override
    public void onCreate() {
        super.onCreate();

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
}
