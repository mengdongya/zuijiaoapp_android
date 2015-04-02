package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.controller.PreferenceManager.PreferenceInfo;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
	@ViewInject(R.id.splash_text1)
	private TextView text1 = null;
	@ViewInject(R.id.splash_text2)
	private TextView text2 = null;
	private PreferenceManager mPreferMng = null;
	private PreferenceInfo mPreferInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		initPreferenceInfo();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
                networkSetup();
				if (mPreferInfo.isAppFirstLaunch()) {
					goToGuide();
				} else {
					goToMain();
				}
				finish() ;
			}
		}, 800);

	}

	private void goToMain() {
		Intent mainIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		startActivity(mainIntent);
	}

	private void goToGuide() {
		Intent mainIntent = new Intent(getApplicationContext(),
				GuideActivity.class);
		startActivity(mainIntent);
	}

	private void initPreferenceInfo() {
		mPreferMng = PreferenceManager.getInstance(getApplicationContext());
		mPreferInfo = mPreferMng.initPreferenceInfo() ;
		FileManager.createRootFolder() ;
	}

    private void networkSetup() {
        Router.getOAuthModule().visitor(() -> System.err.println("Visitor Success"), null);
        Cache.INSTANCE.setup();
    }

	@Override
	protected void findViews() {

	}

	@Override
	protected void registeViews() {

	}

}
