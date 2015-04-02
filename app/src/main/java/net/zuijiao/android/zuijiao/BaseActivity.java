package net.zuijiao.android.zuijiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;
import com.zuijiao.controller.ThirdPartySdkManager;
import com.zuijiao.db.DBOpenHelper;
import com.zuijiao.entity.ThirdPartyUserInfo;

public abstract class BaseActivity extends ActionBarActivity {
	protected PreferenceManager mPreferMng = null;
	protected PreferenceInfo mPreferenceInfo = null;
	protected FileManager mFileMng = null ;
    protected DBOpenHelper dbMng= null ;
	protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MessageDef.ACTION_LOGIN_FINISH)){
				onLoginFinish() ;
			}else if(intent.getAction().equals(MessageDef.ACTION_GET_THIRD_PARTY_USER)){
				Bundle data = intent.getBundleExtra("userinfo") ;
				String name = data.getString("name") ;
				ThirdPartyUserInfo userInfo = new ThirdPartyUserInfo() ;
				userInfo.setUserName(name); 
				ThirdPartySdkManager.getInstance(getApplicationContext()).setThirdPartyUser(userInfo);
				onThirdPartyUserInfoGot() ;
			}
		}
	} ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViews();
		com.lidroid.xutils.ViewUtils.inject(this);
		registeViews();
		IntentFilter filter = new IntentFilter() ;
		filter.addAction(MessageDef.ACTION_LOGIN_FINISH) ;
		filter.addAction(MessageDef.ACTION_GET_THIRD_PARTY_USER);
		registerReceiver(mReceiver, filter) ;
		mPreferMng = PreferenceManager.getInstance(getApplicationContext());
		if (mPreferMng.getPreferInfo() == null) {
			mPreferMng.initPreferenceInfo();
		}
		mPreferenceInfo = mPreferMng.getPreferInfo();
		mFileMng = FileManager.getInstance(getApplicationContext());
        dbMng = DBOpenHelper.getmInstance(getApplicationContext());
		ActivityTask.getInstance().addActivity(this);
	}
	protected void onLoginFinish(){
		
	}
	protected void onThirdPartyUserInfoGot(){
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		ActivityTask.getInstance().removeActivity(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Deprecated
	protected abstract void findViews();

	protected abstract void registeViews();
}
